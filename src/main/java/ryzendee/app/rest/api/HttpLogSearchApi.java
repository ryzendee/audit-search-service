package ryzendee.app.rest.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ryzendee.app.dto.HttpLogFilter;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;

import java.util.List;

@Tag(name = "HTTP Audit API", description = "Методы для поиска и агрегации HTTP-запросов")
@RequestMapping("/audit/requests")
public interface HttpLogSearchApi {

    @Operation(
            summary = "Полнотекстовый поиск по HTTP-запросам",
            description = "Поиск по url и requestBody. Опционально можно фильтровать по statusCode.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных логов")
            }
    )
    @GetMapping("/search")
    List<HttpLogResponse> search(
            @Parameter(description = "Поисковый запрос") @RequestParam String query,
            @Parameter(description = "HTTP статус (например 200)") @RequestParam(required = false) Integer statusCode
    );

    @Operation(
            summary = "Агрегация HTTP-запросов",
            description = "Группировка по statusCode, method или url. Можно фильтровать по direction.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Количество агрегированных документов по полю"),
                    @ApiResponse(responseCode = "400", description = "Неверно указано поле для группировки")
            })
    @GetMapping("/stats")
    StatsResponse stats(
            @Parameter(description = "Поле группировки (statusCode, method, url)") @RequestParam String groupBy,
            @Parameter(description = "Направление (INCOMING/OUTGOING)") @RequestParam(required = false) String direction
    );

    @Operation(
            summary = "Поиск по конкретным полям",
            description = "Точный match по url, method, statusCode.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных логов")
            }
    )
    @GetMapping
    List<HttpLogResponse> searchByFilter(HttpLogFilter filter);

}
