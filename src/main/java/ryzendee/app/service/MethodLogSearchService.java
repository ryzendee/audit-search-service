package ryzendee.app.service;

import ryzendee.app.dto.MethodLogFilter;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для поиска и агрегации логов методов.
 *
 * @author Dmitry Ryazantsev
 */
public interface MethodLogSearchService {

    /**
     * Поиск логов по текстовому запросу.
     *
     * @param query текст для поиска
     * @param optionalLogLevel уровень логов (например, DEBUG, INFO); может быть null
     * @return список логов {@link MethodLogResponse}
     */
    List<MethodLogResponse> searchByQuery(String query, String optionalLogLevel);

    /**
     * Получение статистики и агрегаций логов.
     *
     * @param groupingBy поле для группировки
     * @param from начало диапазона времени
     * @param to конец диапазона времени
     * @return объект статистики {@link StatsResponse}
     */
    StatsResponse aggregateStats(String groupingBy, LocalDateTime from, LocalDateTime to);

    /**
     * Поиск логов по конкретным полям.
     *
     * @param filter фильтр {@link MethodLogFilter}
     * @return список логов {@link MethodLogResponse}
     */
    List<MethodLogResponse> searchByFilter(MethodLogFilter filter);
}
