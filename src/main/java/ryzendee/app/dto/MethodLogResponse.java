package ryzendee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для представления логов методов.
 *
 * @author Dmitry Ryazantsev
 */
@Builder
@Schema(description = "Информация о логах вызовов методов")
public record MethodLogResponse(
        @Schema(description = "Уникальный идентификатор трассировки вызова", example = "f9e8d7c6")
        String traceId,

        @Schema(description = "Момент времени, когда был зафиксирован лог", example = "2025-08-21T14:35:45")
        LocalDateTime timestamp,

        @Schema(description = "Уровень логирования", example = "ERROR")
        String logLevel,

        @Schema(description = "Тип события (например, CALL или RETURN)", example = "CALL")
        String eventType,

        @Schema(description = "Полное имя вызванного метода", example = "OrderService.createOrder")
        String methodName,

        @Schema(description = "Сообщение об ошибке, если метод завершился с исключением", example = "NullPointerException: order is null")
        String errorMessage,

        @Schema(description = "Аргументы, переданные в метод", example = "[\"123\", 2]")
        String args,

        @Schema(description = "Результат выполнения метода (если есть)", example = "{\"orderId\":99,\"status\":\"CREATED\"}")
        String result
) {
}
