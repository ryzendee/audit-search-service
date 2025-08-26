package ryzendee.app.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ryzendee.app.testutils.ElasticTestFacade;

@TestConfiguration
class ElasticsearchClientTestConfig {

    @Bean
    @Primary
    public JacksonJsonpMapper jacksonJsonpMapper(ObjectMapper objectMapper) {
        return new JacksonJsonpMapper(objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public ElasticTestFacade elasticTestFacade(ElasticsearchClient client) {
        return new ElasticTestFacade(client);
    }
}
