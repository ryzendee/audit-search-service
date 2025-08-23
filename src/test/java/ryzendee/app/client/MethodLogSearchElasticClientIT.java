package ryzendee.app.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import ryzendee.app.AbstractTestcontainers;
import ryzendee.app.client.impl.MethodLogSearchElasticClient;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.enums.MethodLogField;
import ryzendee.app.testutils.ElasticTestFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static ryzendee.app.enums.MethodLogField.METHOD;

@Import(ElasticsearchClientTestConfig.class)
@DataElasticsearchTest
public class MethodLogSearchElasticClientIT extends AbstractTestcontainers {

    private MethodLogSearchClient methodLogSearchClient;

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private ElasticTestFacade elasticTestFacade;
    @Value("${elastic.index.audit-methods.name}")
    private String index;

    private MethodLogResponse methodLogResponse;

    @BeforeAll
    static void startContainer() {
        elasticsearchContainer.start();
        executeElasticScript();
    }

    @BeforeEach
    void setUp() {
        methodLogSearchClient = new MethodLogSearchElasticClient(elasticsearchClient, index);

        elasticTestFacade.deleteAllDocuments(index);
        methodLogResponse = MethodLogResponse.builder()
                .traceId(randomUUID().toString())
                .methodName("method")
                .errorMessage("error")
                .logLevel("INFO")
                .result("result")
                .args("[arg0, arg1, arg2]")
                .build();
        elasticTestFacade.putObject(index, methodLogResponse);
        elasticTestFacade.refreshIndex(index);
    }

    @Test
    void findByQuery_existingDocument_shouldReturnDocument() {
        List<MethodLogResponse> responses
                = methodLogSearchClient.findByQuery(methodLogResponse.methodName(), List.of(METHOD), emptyMap());

        assertThat(responses).isNotEmpty();
        assertThat(responses).containsOnly(methodLogResponse);
    }

    @Test
    void findByQuery_nonExistingDocument_shouldReturnEmptyList() {
        List<MethodLogResponse> responses
                = methodLogSearchClient.findByQuery("dummy", List.of(METHOD), emptyMap());

        assertThat(responses).isEmpty();
    }

    @Test
    void findByField_nonExistingDocument_shouldReturnEmptyList() {
        Map<MethodLogField, String> fieldValueMap = new HashMap<>();
        fieldValueMap.put(METHOD, "dummy");

        List<MethodLogResponse> responses
                = methodLogSearchClient.findByField(fieldValueMap);

        assertThat(responses).isEmpty();
    }

    @Test
    void findByField_existingDocument_shouldReturnDocument() {
        Map<MethodLogField, String> fieldValueMap = new HashMap<>();
        fieldValueMap.put(METHOD, methodLogResponse.methodName());

        List<MethodLogResponse> responses
                = methodLogSearchClient.findByField(fieldValueMap);

        assertThat(responses).isNotEmpty();
        assertThat(responses).containsOnly(methodLogResponse);
    }
}
