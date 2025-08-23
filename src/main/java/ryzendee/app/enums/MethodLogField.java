package ryzendee.app.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ryzendee.app.api.SearchableField;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum MethodLogField implements SearchableField {

    TIMESTAMP("timestamp", true),
    METHOD("methodName", true),
    EVENT_TYPE("eventType", true),
    ARGS("args", false),
    RESULT("result", false),
    LOG_LEVEL("logLevel", true);

    private final String fieldName;
    private final boolean supportExactMatch;

    public static Optional<MethodLogField> fromStringFieldName(String fieldName) {
        return Arrays.stream(MethodLogField.values())
                .filter(e -> e.getFieldName().equalsIgnoreCase(fieldName))
                .findFirst();
    }
}
