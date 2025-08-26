package ryzendee.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ryzendee.app.api.SearchableField;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Утилитный класс для построения карты фильтров поиска {@link SearchableField → значение}.
 * <p>
 * Позволяет удобно добавлять поля с проверкой на пустые значения и получать готовую
 * неизменяемую карту для поиска.
 *
 * @author Dmitry Ryazantsev
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchQueryBuilder<F extends SearchableField> {

    private final Map<F, String> fieldValueMap = new HashMap<>();

    /**
     * Создает новый экземпляр билдера.
     *
     * @return новый {@link SearchQueryBuilder}
     */
    public static <F extends SearchableField> SearchQueryBuilder<F> builder() {
        return new SearchQueryBuilder<>();
    }

    /**
     * Добавляет поле и его значение в карту фильтров.
     * <p>
     * Если значение пустое или null, поле не добавляется.
     *
     * @param field поле {@link SearchableField} для поиска
     * @param value значение для фильтрации
     * @return текущий экземпляр билдера для цепочки вызовов
     */
    public SearchQueryBuilder<F> add(F field, String value) {
        if (!isBlank(value)) {
            fieldValueMap.put(field, value);
        }

        return this;
    }

    /**
     * Возвращает готовую неизменяемую карту фильтров.
     *
     * @return {@link Map} с полями и значениями для поиска
     */
    public Map<F, String> build() {
        return Map.copyOf(fieldValueMap);
    }
}
