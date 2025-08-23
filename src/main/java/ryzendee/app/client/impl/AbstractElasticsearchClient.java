    package ryzendee.app.client.impl;

    import co.elastic.clients.elasticsearch.ElasticsearchClient;
    import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
    import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
    import co.elastic.clients.elasticsearch._types.query_dsl.Query;
    import co.elastic.clients.elasticsearch.core.SearchResponse;
    import co.elastic.clients.elasticsearch.core.search.Hit;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import ryzendee.app.api.SearchableField;

    import java.io.IOException;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    /**
     * Абстрактный базовый класс для работы с клиентом ElasticSearch {@link ElasticsearchClient}.
     * <p>
     * Предоставляет универсальные методы для поиска и агрегаций над данными любого типа {@code T}
     * с использованием полей типа {@code F}, которые реализуют интерфейс {@link SearchableField}.
     *
     * @author Dmitry Ryazantsev
     */
    @Slf4j
    @RequiredArgsConstructor
    public abstract class AbstractElasticsearchClient<T, F extends SearchableField> {

        private static final String COUNT_BY_FIELD_AGG_KEY = "count";

        protected final ElasticsearchClient client;
        protected final String index;

        private final Class<T> clazz;

        /**
         * Поиск документов по текстовому запросу с возможностью фильтрации по нескольким полям.
         * <p>
         * Строится multi-match запрос по {@code fieldsToSearch} для поиска текста {@code query},
         * а также добавляются дополнительные фильтры из {@code filters} в блок filter bool-запроса.
         *
         * @param query          текст для поиска
         * @param fieldsToSearch список полей {@link SearchableField}, по которым выполняется текстовый поиск
         * @param filters        карта фильтров типа поле-значение ({@link SearchableField} → значение); может быть пустой или null
         * @return список найденных объектов типа {@code T}
         */
        protected List<T> findByQuery(String query,
                                      List<F> fieldsToSearch,
                                      List<Query> filters) {

            List<Query> mustQueries = fieldsToSearch.stream()
                    .map(f -> this.buildTermOrMatchQueryForField(f, query))
                    .toList();

            Query esQuery;
            if (filters == null || filters.isEmpty()) {
                esQuery = Query.of(q -> q.bool(b -> b.must(mustQueries)));
            } else {
                esQuery = Query.of(q -> q.bool(b -> b
                        .must(mustQueries)
                        .filter(filters)
                ));
            }

            return executeSearch(esQuery);
        }

        /**
         * Поиск документов по конкретным полям.
         * <p>
         * Выбирает term или match query в зависимости от того, поддерживает ли поле
         * точное совпадение {@link SearchableField#isSupportExactMatch()}.
         *
         * @param fieldValueMap параметры типа поле-значение
         * @return Список найденных объектов типа {@code T}
         */
        protected List<T> findByField(Map<F, String> fieldValueMap) {
            List<Query> mustQueries = toQuery(fieldValueMap);

            Query esQuery = Query.of(q -> q.bool(b -> b.must(mustQueries)));

            return executeSearch(esQuery);
        }

        /**
         * Выполняет произвольный запрос к ElasticSearch и возвращает результаты.
         *
         * @param esQuery Запрос в формате ElasticSearch DSL
         * @return Список объектов {@code T}, удовлетворяющих запросу
         */
        protected List<T> executeSearch(Query esQuery) {
            try {
                SearchResponse<T> response = client.search(s -> s
                        .index(index)
                        .query(esQuery), clazz);

                return response.hits().hits().stream()
                        .map(Hit::source)
                        .toList();
            } catch (IOException ex) {
                log.error("Failed to execute search query: {}", esQuery, ex);
                return List.of();
            }
        }

        /**
         * Выполняет агрегацию по указанному полю с дополнительным фильтром.
         *
         * @param field            Поле, по которому выполняется агрегация
         * @param additionalFilter Дополнительный фильтр запроса
         * @return Map, где key = значение поля, value = количество документов
         */
        protected Map<String, Long> aggregateByField(F field, Query additionalFilter) {
            try {
                SearchResponse<Void> response = client.search(s -> s
                        .index(index)
                        .size(0)
                        .query(additionalFilter)
                        .aggregations(COUNT_BY_FIELD_AGG_KEY, a -> a
                                .terms(t -> t
                                        .field(field.getFieldName()
                                        )
                                )
                        ), Void.class
                );

                Aggregate aggregate = response.aggregations()
                        .get(COUNT_BY_FIELD_AGG_KEY);

                return extractStringTermsFromAggregate(aggregate);
            } catch (Exception ex) {
                log.error("Failed to perform aggregation on field: {}", field, ex);
                return Map.of();
            }
        }

        /**
         * Создает term query для точного поиска по полю.
         *
         * @param field Поле поиска
         * @param value Значение для поиска
         * @return Объект {@link Query}
         */
        protected Query termForField(F field, String value) {
            return Query.of(q -> q.term(t -> t.field(field.getFieldName()).value(value)));
        }

        /**
         * Создает match query для поиска по полю (не точное совпадение).
         *
         * @param field Поле поиска
         * @param value Значение для поиска
         * @return Объект {@link Query}
         */
        protected Query matchForField(F field, String value) {
            return Query.of(q -> q.match(m -> m.field(field.getFieldName()).query(value)));
        }

        /**
         * Переводит поля со значениями для поиска в список запросов.
         *
         * @param fieldValueMap карта типа поле-значение
         * @return Объект {@link Query}
         */
        protected List<Query> toQuery(Map<F, String> fieldValueMap) {
            return fieldValueMap.entrySet().stream()
                    .map(this::buildQueryForEntry)
                    .toList();
        }

        private Query buildQueryForEntry(Map.Entry<F, String> entry) {
            F field = entry.getKey();
            String value = entry.getValue();
            return buildTermOrMatchQueryForField(field, value);
        }

        private Query buildTermOrMatchQueryForField(F field, String value) {
            return field.isSupportExactMatch()
                    ? termForField(field, value)
                    : matchForField(field, value);
        }

        private Map<String, Long> extractStringTermsFromAggregate(Aggregate aggregate) {
            return aggregate.sterms().buckets().array().stream()
                    .collect(Collectors.toMap(
                            bucket -> bucket.key().stringValue(),
                            StringTermsBucket::docCount
                    ));
        }
    }
