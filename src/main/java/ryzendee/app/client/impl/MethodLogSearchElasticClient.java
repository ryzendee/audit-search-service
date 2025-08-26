package ryzendee.app.client.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ryzendee.app.client.MethodLogSearchClient;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.MethodLogField;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static ryzendee.app.enums.MethodLogField.TIMESTAMP;

@Component
public class MethodLogSearchElasticClient
        extends AbstractElasticsearchClient<MethodLogResponse, MethodLogField>
        implements MethodLogSearchClient {

    public MethodLogSearchElasticClient(ElasticsearchClient client,
                                      @Value("${elastic.index.audit-methods.name}") String index) {
        super(client, index, MethodLogResponse.class);
    }

    @Override
    public List<MethodLogResponse> findByQuery(String query, List<MethodLogField> fieldsToSearch, Map<MethodLogField, String> filters) {
        List<Query> queryFilters = (filters == null || filters.isEmpty())
                ? emptyList()
                : toQuery(filters);
        return super.findByQuery(query, fieldsToSearch, queryFilters);
    }

    @Override
    public List<MethodLogResponse> findByField(Map<MethodLogField, String> fieldValueMap) {
        return super.findByField(fieldValueMap);
    }

    @Override
    public StatsResponse findAggregateWithRange(MethodLogField groupingBy, String from, String to) {
        Query rangeFilter = Query.of(q -> q
                .range(r -> r
                        .date(d -> d
                                .field(TIMESTAMP.getFieldName())
                                .gte(from)
                                .lte(to)
                        )
                )
        );

        return new StatsResponse(aggregateByField(groupingBy, rangeFilter));
    }
}
