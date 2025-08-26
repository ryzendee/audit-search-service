package ryzendee.app.client;

import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.HttpLogField;

import java.util.List;
import java.util.Map;

/**
 /**
 * Интерфейс для поиска и агрегации HTTP логов.
 *
 * <p>Предоставляет методы для поиска логов по произвольной строке,
 * поиска по конкретному полю и получения статистики по полям.</p>
 *
 * @author Dmitry Ryazantsev
 */
public interface HttpLogSearchClient {

    /**
     * Выполняет поиск HTTP логов по произвольной строке запроса с возможностью фильтрации.
     *
     * @param query Строка запроса для поиска.
     * @param fieldsToSearch поля, по которым нужно осуществить поиск
     * @param filters пара поле-значение для фильтрации.
     * @return Список найденных HTTP логов.
     */
    List<HttpLogResponse> findByQuery(String query, List<HttpLogField> fieldsToSearch, Map<HttpLogField, String> filters);

    /**
     * Выполняет поиск HTTP логов по конкретным полям и их значениям.
     *
     * @param fieldValueMap карта типа поле-значение поля
     * @return Список найденных HTTP логов.
     */
    List<HttpLogResponse> findByField(Map<HttpLogField, String> fieldValueMap);

    /**
     * Выполняет агрегацию логов по указанному полю и возвращает статистику.
     *
     * @param groupingBy Поле, по которому нужно сгруппировать данные.
     * @param filters пара поле-значение для фильтрации.
     * @return Статистические данные по количеству логов для каждого значения поля.
     */
    StatsResponse findAggregate(HttpLogField groupingBy, Map<HttpLogField, String> filters);
}
