package ryzendee.app.client;

import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.MethodLogField;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для поиска и агрегации Method логов.
 *
 * <p>Предоставляет методы для поиска логов по произвольной строке,
 * поиска по конкретному полю и получения статистики по полям.</p>
 *
 * @author Dmitry Ryazantsev
 */
public interface MethodLogSearchClient {

    /**
     * Выполняет поиск Method логов по произвольной строке запроса с возможностью фильтрации.
     *
     * @param query Строка запроса для поиска.
     * @param fieldsToSearch поля, по которым нужно осуществить поиск
     * @param filters пара поле-значение фильтрации.
     * @return Список найденных Method логов.
     */
    List<MethodLogResponse> findByQuery(String query, List<MethodLogField> fieldsToSearch, Map<MethodLogField, String> filters);

    /**
     * Выполняет поиск Method логов по конкретным полям и их значениям.
     *
     * @param fieldValueMap карта типа поле-значение поля
     * @return Список найденных Method логов.
     */
    List<MethodLogResponse> findByField(Map<MethodLogField, String> fieldValueMap);

    /**
     * Выполняет агрегацию Method логов по указанному полю в заданном диапазоне времени и возвращает статистику.
     *
     * @param groupingBy Поле, по которому нужно сгруппировать данные.
     * @param from       Начальная дата/время диапазона.
     * @param to         Конечная дата/время диапазона.
     * @return Статистические данные по количеству логов для каждого значения поля.
     */
    StatsResponse findAggregateWithRange(MethodLogField groupingBy, String from, String to);

}
