package ryzendee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Фильтры для поиска логов методов")
public record MethodLogFilter(

        @Schema(description = "Название метода (например, Service.method)", example = "Service.getOrders")
        String methodName,

        @Schema(description = "Уровень логирования (DEBUG, INFO, ERROR)", example = "ERROR")
        String logLevel,

        @Schema(description = "Тип события", example = "INVOCATION")
        String eventType
) {}