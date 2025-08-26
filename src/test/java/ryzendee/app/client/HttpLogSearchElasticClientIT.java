package ryzendee.app.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ryzendee.app.AbstractTestcontainers;
import ryzendee.app.client.impl.HttpLogSearchElasticClient;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.HttpLogField;
import ryzendee.app.testutils.ElasticTestFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ryzendee.app.enums.HttpLogField.*;
import static java.util.Collections.emptyMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@Import(ElasticsearchClientTestConfig.class)
@DataElasticsearchTest
public class HttpLogSearchElasticClientIT extends AbstractTestcontainers {

    private HttpLogSearchElasticClient httpLogSearchClient;

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private ElasticTestFacade elasticTestFacade;
    @Value("${elastic.index.audit-http.name}")
    private String index;

    private HttpLogResponse httpLogResponse;

    @BeforeAll
    static void startContainer() {
        elasticsearchContainer.start();
        executeElasticScript();
    }

    @BeforeEach
    void setUp() {
        httpLogSearchClient = new HttpLogSearchElasticClient(elasticsearchClient, index);

        elasticTestFacade.deleteAllDocuments(index);
        httpLogResponse = HttpLogResponse.builder()
                .traceId(randomUUID().toString())
                .httpStatusCode(HttpStatus.OK.value())
                .requestPath("/api/logs")
                .build();
        elasticTestFacade.putObject(index, httpLogResponse);
        elasticTestFacade.refreshIndex(index);
    }

    @Test
    void findByQuery_existingDocument_shouldReturnDocument() {
        List<HttpLogResponse> responses
                = httpLogSearchClient.findByQuery(httpLogResponse.requestPath(), List.of(REQUEST_PATH), emptyMap());

        assertThat(responses).isNotEmpty();
        assertThat(responses).containsOnly(httpLogResponse);
    }

    @Test
    void findByQuery_nonExistingDocument_shouldReturnEmptyList() {
        List<HttpLogResponse> responses
                = httpLogSearchClient.findByQuery("dummy", List.of(REQUEST_PATH), emptyMap());

        assertThat(responses).isEmpty();
    }

    @Test
    void findByField_nonExistingDocument_shouldReturnEmptyList() {
        Map<HttpLogField, String> fieldValueMap = new HashMap<>();
        fieldValueMap.put(REQUEST_PATH, "dummy");

        List<HttpLogResponse> responses
                = httpLogSearchClient.findByField(fieldValueMap);

        assertThat(responses).isEmpty();
    }

    @Test
    void findByField_existingDocument_shouldReturnDocument() {
        Map<HttpLogField, String> fieldValueMap = new HashMap<>();
        fieldValueMap.put(REQUEST_PATH, httpLogResponse.requestPath());

        List<HttpLogResponse> responses
                = httpLogSearchClient.findByField(fieldValueMap);

        assertThat(responses).isNotEmpty();
        assertThat(responses).containsOnly(httpLogResponse);
    }

    @Test
    void findAggregate_existsDocument_shouldReturnAggregate() {
        StatsResponse response = httpLogSearchClient.findAggregate(STATUS_CODE, emptyMap());

        assertThat(response).isNotNull();
        assertThat(response.stats()).isNotEmpty();
    }
}