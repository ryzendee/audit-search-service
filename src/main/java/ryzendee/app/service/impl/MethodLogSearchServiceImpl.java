package ryzendee.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ryzendee.app.util.SearchQueryBuilder;
import ryzendee.app.client.MethodLogSearchClient;
import ryzendee.app.dto.MethodLogFilter;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.MethodLogField;
import ryzendee.app.service.MethodLogSearchService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ryzendee.app.enums.MethodLogField.*;

@Service
@RequiredArgsConstructor
public class MethodLogSearchServiceImpl implements MethodLogSearchService {

    private static final String DEFAULT_LEVEL = "DEBUG";

    private final MethodLogSearchClient client;

    @Override
    public List<MethodLogResponse> searchByQuery(String query, String optionalLogLevel) {
        List<MethodLogField> fieldsToSearch = List.of(METHOD, ARGS, RESULT);

        optionalLogLevel = optionalLogLevel == null || optionalLogLevel.isBlank()
                ? DEFAULT_LEVEL
                : optionalLogLevel;
        Map<MethodLogField, String> filters = getSearchQueryBuilder()
                .add(LOG_LEVEL, optionalLogLevel)
                .build();

        return client.findByQuery(query, fieldsToSearch, filters);
    }

    @Override
    public StatsResponse aggregateStats(String groupingBy, LocalDateTime from, LocalDateTime to) {
        return client.findAggregateWithRange(toMethodLogField(groupingBy), from.toString(), to.toString());
    }

    @Override
    public List<MethodLogResponse> searchByFilter(MethodLogFilter filter) {
        Map<MethodLogField, String> fieldValueMap = getSearchQueryBuilder()
                .add(LOG_LEVEL, filter.logLevel())
                .add(METHOD, filter.methodName())
                .add(EVENT_TYPE, filter.eventType())
                .build();

        return client.findByField(fieldValueMap);
    }

    private SearchQueryBuilder<MethodLogField> getSearchQueryBuilder() {
        return SearchQueryBuilder.builder();
    }

    private MethodLogField toMethodLogField(String field) {
        return MethodLogField.fromStringFieldName(field)
                .orElseThrow(() -> new IllegalArgumentException("Invalid field definition: " + field));
    }
}


