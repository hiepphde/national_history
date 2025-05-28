package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode extractAfterPayload(String raw) throws IOException {
        JsonNode root = mapper.readTree(raw);
        return mapper.readTree(root.get("after").asText());
    }

    public static String get(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : null;
    }
}
