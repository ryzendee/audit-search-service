package ryzendee.app.testutils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ElasticTestFacade {

    private final ElasticsearchClient client;

    public void deleteAllDocuments(String index) {
        try {
            client.deleteByQuery(d -> d
                    .index(index)
                    .query(q -> q
                            .matchAll(m -> m))
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String putObject(String index, Object document) {
        try {
            return client.index(IndexRequest.of(i -> i
                            .index(index)
                            .document(document)
                    )
            ).id();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void refreshIndex(String index) {
        try {
            client.indices().refresh(r -> r.index(index));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
