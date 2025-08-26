package ryzendee.app.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ryzendee.app.dto.MethodLogFilter;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.rest.api.MethodAuditApi;
import ryzendee.app.service.MethodLogSearchService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MethodAuditRestController implements MethodAuditApi {

    private final MethodLogSearchService methodLogSearchService;

    @Override
    public List<MethodLogResponse> search(String query, String level) {
        return methodLogSearchService.searchByQuery(query, level);
    }

    @Override
    public StatsResponse stats(String groupBy, LocalDateTime from, LocalDateTime to) {
        return methodLogSearchService.aggregateStats(groupBy, from, to);
    }

    @Override
    public List<MethodLogResponse> searchByFilter(MethodLogFilter filter) {
        return methodLogSearchService.searchByFilter(filter);
    }
}
