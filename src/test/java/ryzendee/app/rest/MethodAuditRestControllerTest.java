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
import ryzendee.app.dto.MethodLogFilter;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.service.MethodLogSearchService;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@WebMvcTest(MethodAuditRestController.class)
public class MethodAuditRestControllerTest {

    private static final String BASE_URI = "/audit/methods";

    @MockitoBean
    private MethodLogSearchService methodLogSearchService;

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
        String query = "myQuery";
        String level = "INFO";
        MethodLogResponse logResponse = mock(MethodLogResponse.class);
        when(methodLogSearchService.searchByQuery(query, level))
                .thenReturn(singletonList(logResponse));

        request.queryParam("query", query)
                .queryParam("level", level)
                .get("/search")
                .then()
                .status(HttpStatus.OK);

        verify(methodLogSearchService).searchByQuery(query, level);
    }

    @Test
    void stats_validParams_statusOk() {
        String groupBy = "METHOD";
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        StatsResponse statsResponse = mock(StatsResponse.class);
        when(methodLogSearchService.aggregateStats(groupBy, from, to))
                .thenReturn(statsResponse);

        request.queryParam("groupBy", groupBy)
                .queryParam("from", from.toString())
                .queryParam("to", to.toString())
                .get("/stats")
                .then()
                .status(HttpStatus.OK);

        verify(methodLogSearchService).aggregateStats(groupBy, from, to);
    }

    @Test
    void searchByFilter_validParams_statusOk() {
        MethodLogFilter filter = new MethodLogFilter("method1", "INFO", "END");
        MethodLogResponse logResponse = mock(MethodLogResponse.class);
        when(methodLogSearchService.searchByFilter(filter))
                .thenReturn(singletonList(logResponse));

        request.queryParam("methodName", filter.methodName())
                .queryParam("logLevel", filter.logLevel())
                .queryParam("eventType", filter.eventType())
                .get()
                .then()
                .status(HttpStatus.OK);

        verify(methodLogSearchService).searchByFilter(filter);
    }
}
