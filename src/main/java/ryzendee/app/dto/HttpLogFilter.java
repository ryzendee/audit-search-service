package ryzendee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Фильтры для поиска HTTP-логов")
public record HttpLogFilter(

        @Schema(description = "Путь запроса (например, /api/orders)", example = "/api/orders")
        String url,

        @Schema(description = "HTTP-метод (GET, POST и т.д.)", example = "GET")
        String method,

        @Schema(description = "HTTP-статус ответа", example = "200")
        Integer statusCode
) {}
