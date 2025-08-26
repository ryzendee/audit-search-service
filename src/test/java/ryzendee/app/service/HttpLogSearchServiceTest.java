package ryzendee.app.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ryzendee.app.client.HttpLogSearchClient;
import ryzendee.app.dto.HttpLogFilter;
import ryzendee.app.dto.HttpLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.HttpLogField;
import ryzendee.app.service.impl.HttpLogSearchServiceImpl;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static ryzendee.app.enums.HttpLogField.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpLogSearchServiceTest {

    @InjectMocks
    private HttpLogSearchServiceImpl service;
    @Mock
    private HttpLogSearchClient client;
    @Mock
    private HttpLogResponse httpLogResponse;
    @Captor
    private ArgumentCaptor<Map<HttpLogField, String>> captor;

    @Test
    void searchByQuery_shouldCallClientWithCorrectParams() {
        // Arrange
        String query = "test";
        int status = 200;
        List<HttpLogField> fieldsToSearch = getFieldsToSearch();

        when(client.findByQuery(eq(query), eq(fieldsToSearch), anyMap()))
                .thenReturn(singletonList(httpLogResponse));

        // Act
        List<HttpLogResponse> result = service.searchByQuery(query, status);

        // Assert
        assertThat(result).containsExactly(httpLogResponse);
        verify(client).findByQuery(eq(query), eq(fieldsToSearch), captor.capture());

        Map<HttpLogField, String> statusFilter = captor.getValue();
        assertThat(statusFilter.get(STATUS_CODE)).isEqualTo(String.valueOf(status));
    }

    @Test
    void searchByQuery_withInvalidStatus_shouldUseDefaultStatus() {
        // Arrange
        String query = "test";
        int invalidStatus = 999;

        List<HttpLogField> fieldsToSearch = getFieldsToSearch();
        when(client.findByQuery(eq(query), eq(fieldsToSearch), anyMap()))
                .thenReturn(singletonList(httpLogResponse));

        // Act
        List<HttpLogResponse> result = service.searchByQuery(query, invalidStatus);

        // Assert
        assertThat(result).containsExactly(httpLogResponse);
        verify(client).findByQuery(eq(query), eq(fieldsToSearch), captor.capture());

        Map<HttpLogField, String> filter = captor.getValue();
        assertThat(filter.get(STATUS_CODE)).isNotBlank();
    }

    @Test
    void searchByFilter_shouldCallClientWithCorrectFieldMap() {
        // Arrange
        HttpLogFilter filter = new HttpLogFilter("/test", "GET", 200);

        when(client.findByField(anyMap())).thenReturn(singletonList(httpLogResponse));

        // Act
        List<HttpLogResponse> result = service.searchByFilter(filter);

        // Assert
        assertThat(result).containsExactly(httpLogResponse);
        verify(client).findByField(captor.capture());

        Map<HttpLogField, String> fieldFilter = captor.getValue();
        assertThat(fieldFilter.get(REQUEST_PATH)).isEqualTo(filter.url());
        assertThat(fieldFilter.get(METHOD)).isEqualTo(filter.method());
        assertThat(fieldFilter.get(STATUS_CODE)).isEqualTo(String.valueOf(filter.statusCode()));
    }

    @Test
    void aggregationStats_shouldCallClientWithCorrectField() {
        // Arrange
        HttpLogField groupingBy = METHOD;
        String direction = "OUTGOING";
        StatsResponse statsResponse = mock(StatsResponse.class);

        when(client.findAggregate(eq(groupingBy), anyMap())).thenReturn(statsResponse);

        // Act
        StatsResponse result = service.aggregationStats(groupingBy.getFieldName(), direction);

        // Assert
        assertThat(result).isEqualTo(statsResponse);
        verify(client).findAggregate(eq(groupingBy), captor.capture());
        
        Map<HttpLogField, String> directionFilter = captor.getValue();
        assertThat(directionFilter.get(DIRECTION)).isEqualTo(direction);
    }

    private List<HttpLogField> getFieldsToSearch() {
        return List.of(REQUEST_PATH, REQUEST_BODY);
    }
}
