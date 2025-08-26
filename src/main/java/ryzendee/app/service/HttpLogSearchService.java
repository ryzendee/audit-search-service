package ryzendee.app.service;

import ryzendee.app.dto.HttpLogFilter;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;

import java.util.List;

/**
 * Сервис для поиска и агрегации HTTP-логов.
 *
 * @author Dmitry Ryazantsev
 */
public interface HttpLogSearchService {

    /**
     * Поиск HTTP-логов по текстовому запросу.
     *
     * @param query текст для поиска
     * @param optionalHttpStatus HTTP-статус для фильтрации; может быть null
     * @return список логов {@link HttpLogResponse}
     */
    List<HttpLogResponse> searchByQuery(String query, int optionalHttpStatus);

    /**
     * Получение статистики и агрегаций HTTP-логов.
     *
     * @param groupingBy поле для группировки
     * @param requestDirection тип HTTP-запроса (входящий/исходящий)
     * @return объект статистики {@link StatsResponse}
     */
    StatsResponse aggregationStats(String groupingBy, String requestDirection);

    /**
     * Поиск HTTP-логов по конкретным полям.
     *
     * @param filter фильтр {@link HttpLogFilter}
     * @return список логов {@link HttpLogResponse}
     */
    List<HttpLogResponse> searchByFilter(HttpLogFilter filter);
}
