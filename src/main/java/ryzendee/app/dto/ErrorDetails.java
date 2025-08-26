package ryzendee.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO для передачи списка сообщений об ошибках.
 *
 * @author Dmitry Ryazantsev
 */
@Schema(description = "DTO для передачи списка сообщений об ошибках")
public record ErrorDetails(
        @Schema(description = "Список сообщений об ошибках", example = "[\"Ошибка валидации\", \"Поле 'name' обязательно\"]")
        List<String> messages
) {
}