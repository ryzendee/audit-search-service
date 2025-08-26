package ryzendee.app.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ryzendee.app.client.MethodLogSearchClient;
import ryzendee.app.dto.MethodLogFilter;
import ryzendee.app.dto.MethodLogResponse;
import ryzendee.app.dto.StatsResponse;
import ryzendee.app.enums.MethodLogField;
import ryzendee.app.service.impl.MethodLogSearchServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ryzendee.app.enums.MethodLogField.*;

@ExtendWith(MockitoExtension.class)
public class MethodLogSearchServiceTest {

    @InjectMocks
    private MethodLogSearchServiceImpl service;
    @Mock
    private MethodLogSearchClient client;
    @Mock
    private MethodLogResponse methodLogResponse;
    @Captor
    private ArgumentCaptor<Map<MethodLogField, String>> captor;

    @Test
    void searchByQuery_shouldCallClientWithCorrectLogLevel() {
        // Arrange
        String query = "test";
        String logLevel = "INFO";
        List<MethodLogField> fieldsToSearch = List.of(METHOD, ARGS, RESULT);

        when(client.findByQuery(eq(query), eq(fieldsToSearch), anyMap()))
                .thenReturn(singletonList(methodLogResponse));

        // Act
        List<MethodLogResponse> result = service.searchByQuery(query, logLevel);

        // Assert
        assertThat(result).containsExactly(methodLogResponse);
        verify(client).findByQuery(eq(query), eq(fieldsToSearch), captor.capture());

        Map<MethodLogField, String> captured = captor.getValue();
        assertThat(captured.get(LOG_LEVEL)).isEqualTo(logLevel);
    }

    @NullSource
    @EmptySource
    @ParameterizedTest
    void searchByQuery_withNullOrBlankLogLevel_shouldUseDefault(String optionalLogLevel) {
        // Arrange
        String query = "test";

        when(client.findByQuery(eq(query), anyList(), anyMap()))
                .thenReturn(singletonList(methodLogResponse));

        // Act
        List<MethodLogResponse> result = service.searchByQuery(query, optionalLogLevel);

        // Assert
        assertThat(result).containsExactly(methodLogResponse);

        verify(client).findByQuery(eq(query), anyList(), captor.capture());

        Map<MethodLogField, String> logLevelFilter = captor.getValue();
        assertThat(logLevelFilter.get(LOG_LEVEL)).isNotBlank();
    }

    @Test
    void searchByFilter_shouldCallClientWithCorrectFieldMap() {
        // Arrange
        MethodLogFilter filter = new MethodLogFilter("INFO", "methodName", "EVENT_TYPE");

        when(client.findByField(anyMap())).thenReturn(singletonList(methodLogResponse));

        // Act
        List<MethodLogResponse> result = service.searchByFilter(filter);

        // Assert
        assertThat(result).containsExactly(methodLogResponse);
        verify(client).findByField(captor.capture());

        Map<MethodLogField, String> captured = captor.getValue();
        assertThat(captured.get(LOG_LEVEL)).isEqualTo(filter.logLevel());
        assertThat(captured.get(METHOD)).isEqualTo(filter.methodName());
        assertThat(captured.get(EVENT_TYPE)).isEqualTo(filter.eventType());
    }

    @Test
    void aggregateStats_shouldCallClientWithCorrectFieldAndRange() {
        // Arrange
        MethodLogField groupingBy = METHOD;
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        StatsResponse statsResponse = mock(StatsResponse.class);

        when(client.findAggregateWithRange(eq(groupingBy), eq(from.toString()), eq(to.toString())))
                .thenReturn(statsResponse);

        // Act
        StatsResponse result = service.aggregateStats(groupingBy.getFieldName(), from, to);

        // Assert
        assertThat(result).isEqualTo(statsResponse);
        verify(client).findAggregateWithRange(eq(METHOD), eq(from.toString()), eq(to.toString()));
    }
}
