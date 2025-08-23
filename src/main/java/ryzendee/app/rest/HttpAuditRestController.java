package ryzendee.app.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ryzendee.app.dto.HttpLogFilter;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.rest.api.HttpLogSearchApi;
import ryzendee.app.service.HttpLogSearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HttpAuditRestController implements HttpLogSearchApi {

    private final HttpLogSearchService httpLogSearchService;

    @Override
    public List<HttpLogResponse> search(String query, Integer statusCode) {
        return httpLogSearchService.searchByQuery(query, statusCode);
    }

    @Override
    public StatsResponse stats(String groupBy, String direction) {
        return httpLogSearchService.aggregationStats(groupBy, direction);
    }

    @Override
    public List<HttpLogResponse> searchByFilter(HttpLogFilter filter) {
        return httpLogSearchService.searchByFilter(filter);
    }
}
