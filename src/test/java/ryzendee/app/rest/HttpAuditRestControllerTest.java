package ryzendee.app.rest;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ryzendee.app.dto.HttpLogFilter;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.service.HttpLogSearchService;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@WebMvcTest(HttpAuditRestController.class)
public class HttpAuditRestControllerTest {

    private static final String BASE_URI = "/audit/requests";

    @MockitoBean
    private HttpLogSearchService httpLogSearchService;

    @Autowired
    private MockMvc mockMvc;

    private MockMvcRequestSpecification request;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.basePath = BASE_URI;
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        request = RestAssuredMockMvc.given()
                .contentType(ContentType.JSON);
    }

    @Test
    void search_validParams_statusOk() {
        String query = "/api/test";
        int statusCode = 200;
        HttpLogResponse logResponse = mock(HttpLogResponse.class);

        when(httpLogSearchService.searchByQuery(query, statusCode))
                .thenReturn(singletonList(logResponse));

        request.queryParam("query", query)
                .queryParam("statusCode", statusCode)
                .get("/search")
                .then()
                .status(HttpStatus.OK);

        verify(httpLogSearchService).searchByQuery(query, statusCode);
    }

    @Test
    void stats_validParams_statusOk() {
        String groupBy = "statusCode";
        String direction = "INCOMING";
        StatsResponse statsResponse = mock(StatsResponse.class);

        when(httpLogSearchService.aggregationStats(groupBy, direction))
                .thenReturn(statsResponse);

        request.queryParam("groupBy", groupBy)
                .queryParam("direction", direction)
                .get("/stats")
                .then()
                .status(HttpStatus.OK);

        verify(httpLogSearchService).aggregationStats(groupBy, direction);
    }

    @Test
    void searchByFilter_validParams_statusOk() {
        HttpLogFilter filter = new HttpLogFilter("/api/test", "GET", 200);
        HttpLogResponse logResponse = mock(HttpLogResponse.class);

        when(httpLogSearchService.searchByFilter(filter))
                .thenReturn(singletonList(logResponse));

        request.queryParam("url", filter.url())
                .queryParam("method", filter.method())
                .queryParam("statusCode", filter.statusCode())
                .get()
                .then()
                .status(HttpStatus.OK);

        verify(httpLogSearchService).searchByFilter(filter);
    }
}
