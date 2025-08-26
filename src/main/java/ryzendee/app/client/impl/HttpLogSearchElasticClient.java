package ryzendee.app.client.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ryzendee.app.client.HttpLogSearchClient;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.HttpLogField;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Component
@Slf4j
public class HttpLogSearchElasticClient
        extends AbstractElasticsearchClient<HttpLogResponse, HttpLogField>
        implements HttpLogSearchClient {

    public HttpLogSearchElasticClient(ElasticsearchClient client,
                                      @Value("${elastic.index.audit-http.name}") String index) {
        super(client, index, HttpLogResponse.class);
    }

    @Override
    public List<HttpLogResponse> findByQuery(String query, List<HttpLogField> fieldsToSearch, Map<HttpLogField, String> filters) {
        List<Query> queryFilters = (filters == null || filters.isEmpty())
                ? emptyList()
                : toQuery(filters);
        return super.findByQuery(query, fieldsToSearch, queryFilters);
    }

    @Override
    public List<HttpLogResponse> findByField(Map<HttpLogField, String> fieldValueMap) {
        return super.findByField(fieldValueMap);
    }

    @Override
    public StatsResponse findAggregate(HttpLogField groupingBy, Map<HttpLogField, String> filters) {
        Query queryFilter = Query.of(q -> q.matchAll(m -> m));

        if (filters != null && !filters.isEmpty()) {
            List<Query> mappedQueries = toQuery(filters);
            queryFilter = Query.of(q -> q.bool(b -> b.filter(mappedQueries)));
        }

        return new StatsResponse(aggregateByField(groupingBy, queryFilter));
    }
}
