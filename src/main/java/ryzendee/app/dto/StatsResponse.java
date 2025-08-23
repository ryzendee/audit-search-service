package ryzendee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO для представления статистических или агрегированных данных.
 *
 * <p>Содержит отображение значения поля на количество его вхождений
 * или другую агрегированную метрику. </p>
 *
 * @author Dmitry Ryazantsev
 */

@Schema(description = "Результат статистики/агрегации по логам")
public record StatsResponse(

        @Schema(
                description = "Карта вида <значение поля, количество/метрика>. " +
                        "Ключ зависит от параметра группировки (например, statusCode, method, level)",
                example = "{\"DEBUG\": 150, \"ERROR\": 5}"
        )
        Map<String, Long> stats
) {
}
