package ryzendee.app.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ryzendee.app.dto.MethodLogFilter;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Method Audit API", description = "Методы для поиска и агрегации логов методов")
@RequestMapping("/audit/methods")
public interface MethodAuditApi {

    @Operation(
            summary = "Полнотекстовый поиск по логам методов",
            description = "Поиск по method, args, result. Опционально можно фильтровать по уровню.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных логов")
            }
    )
    @GetMapping("/search")
    List<MethodLogResponse> search(
            @Parameter(description = "Поисковый запрос") @RequestParam String query,
            @Parameter(description = "Уровень логирования (например DEBUG)") @RequestParam(required = false) String level
    );

    @Operation(
            summary = "Агрегация логов методов",
            description = "Группировка по level или method, можно ограничить по дате (from/to).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Количество агрегированных документов по полю"),
                    @ApiResponse(responseCode = "400", description = "Неверно указано поле для группировки")
            }
    )
    @GetMapping("/stats")
    StatsResponse stats(
            @Parameter(description = "Поле группировки (level или method)") @RequestParam String groupBy,
            @Parameter(description = "Дата начала интервала") @RequestParam(required = false) LocalDateTime from,
            @Parameter(description = "Дата конца интервала") @RequestParam(required = false) LocalDateTime to
    );

    @Operation(
            summary = "Поиск по конкретным полям",
            description = "Точный match по method, level, eventType.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных логов")
            }
    )
    @GetMapping
    List<MethodLogResponse> searchByFilter(MethodLogFilter filter);

}
