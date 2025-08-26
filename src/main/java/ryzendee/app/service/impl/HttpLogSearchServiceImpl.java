package ryzendee.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ryzendee.app.util.SearchQueryBuilder;
import ryzendee.app.client.HttpLogSearchClient;
import ryzendee.app.dto.HttpLogFilter;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.HttpLogField;
import ryzendee.app.service.HttpLogSearchService;

import java.util.List;
import java.util.Map;

import static ryzendee.app.enums.HttpLogField.*;

@Service
@RequiredArgsConstructor
public class HttpLogSearchServiceImpl implements HttpLogSearchService {

    private static final String DEFAULT_STR_STATUS = "200";

    private final HttpLogSearchClient client;

    @Override
    public List<HttpLogResponse> searchByQuery(String query, int optionalHttpStatus) {
        List<HttpLogField> fieldsToSearch = List.of(REQUEST_PATH, REQUEST_BODY);

        String stringStatus = resolveOrGetDefaultStatus(optionalHttpStatus);
        Map<HttpLogField, String> filter = getSearchQueryBuilder()
                .add(STATUS_CODE, stringStatus)
                .build();

        return client.findByQuery(query, fieldsToSearch, filter);
    }

    @Override
    public StatsResponse aggregationStats(String groupingBy, String requestDirection) {
        Map<HttpLogField, String> filter = getSearchQueryBuilder()
                .add(DIRECTION, requestDirection)
                .build();

        return client.findAggregate(toHttpLogField(groupingBy), filter);
    }

    @Override
    public List<HttpLogResponse> searchByFilter(HttpLogFilter filter) {
        Map<HttpLogField, String> fieldValueMap = getSearchQueryBuilder()
                .add(METHOD, filter.method())
                .add(REQUEST_PATH, filter.url())
                .add(STATUS_CODE, filter.statusCode().toString())
                .build();

        return client.findByField(fieldValueMap);
    }

    private String resolveOrGetDefaultStatus(int status) {
        try {
            return String.valueOf(HttpStatus.valueOf(status).value());
        } catch (IllegalArgumentException e) {
            return DEFAULT_STR_STATUS;
        }
    }

    private SearchQueryBuilder<HttpLogField> getSearchQueryBuilder() {
        return SearchQueryBuilder.builder();
    }

    private HttpLogField toHttpLogField(String field) {
        return HttpLogField.fromStringFieldName(field)
                .orElseThrow(() -> new IllegalArgumentException("Invalid field definition: " + field));
    }
}
