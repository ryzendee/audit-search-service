package ryzendee.app.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ryzendee.app.api.SearchableField;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum HttpLogField implements SearchableField {

    REQUEST_PATH("requestPath", false),
    DIRECTION("direction", true),
    METHOD("httpMethod", true),
    REQUEST_BODY("requestBody", false),
    STATUS_CODE("httpStatusCode", true);

    private final String fieldName;
    private final boolean supportExactMatch;

    public static Optional<HttpLogField> fromStringFieldName(String fieldName) {
        return Arrays.stream(HttpLogField.values())
                .filter(e -> e.getFieldName().equalsIgnoreCase(fieldName))
                .findFirst();
    }
}
