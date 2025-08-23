package ryzendee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для представления http-логов приложения.
 *
 * @author Dmitry Ryazantsev
 */
@Builder
@Schema(description = "Информация об http-логах")
public record HttpLogResponse(

        @Schema(description = "Уникальный идентификатор трассировки запроса", example = "a1b2c3d4")
        String traceId,

        @Schema(description = "Момент времени, когда был зафиксирован лог", example = "2025-08-21T14:32:10")
        LocalDateTime timestamp,

        @Schema(description = "Уровень логирования", example = "DEBUG")
        String logLevel,

        @Schema(description = "Направление запроса (INCOMING/OUTGOING)", example = "INCOMING")
        String direction,

        @Schema(description = "HTTP метод запроса", example = "GET")
        String httpMethod,

        @Schema(description = "HTTP статус ответа", example = "200")
        int httpStatusCode,

        @Schema(description = "Путь запроса (URI)", example = "/api/orders/123")
        String requestPath,

        @Schema(description = "Длительность обработки запроса в миллисекундах", example = "152")
        long durationMs,

        @Schema(description = "Тело входящего HTTP-запроса (если есть)", example = "{\"productId\":42,\"quantity\":2}")
        Object requestBody,

        @Schema(description = "Тело HTTP-ответа (если есть)", example = "{\"orderId\":99,\"status\":\"CREATED\"}")
        Object responseBody
) {
}
