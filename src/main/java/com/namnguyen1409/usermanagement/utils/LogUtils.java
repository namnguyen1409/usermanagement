package com.namnguyen1409.usermanagement.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@Slf4j
public class LogUtils {

    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";
    public static final String WHITE = "\u001B[37m";
    public static final String GRAY = "\u001B[90m";
    public static final String BOLD = "\u001B[1m";

    public String logObject(Object object, String... ignoredFields) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            JsonNode jsonNode = mapper.valueToTree(object);

            removeFieldsRecursively(jsonNode, ignoredFields);

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
                    .replace("\n", "")
                    .replace("\t", "")
                    .replaceAll("\\s{2,}", " ");
        } catch (Exception e) {
            return "Error converting object to JSON";
        }
    }

    private void removeFieldsRecursively(JsonNode node, String... ignoredFields) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            for (String field : ignoredFields) {
                objectNode.remove(field);
            }

            objectNode.fields().forEachRemaining(
                    entry -> removeFieldsRecursively(entry.getValue(), ignoredFields)
            );
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                removeFieldsRecursively(arrayElement, ignoredFields);
            }
        }
    }


    public String color(String text, String color) {
        return color + text + RESET;
    }

    public String bold(String text) {
        return BOLD + text + RESET;
    }

    public String colorStatus(int status, String text) {
        String color = switch (status / 100) {
            case 2 -> GREEN;
            case 4 -> YELLOW;
            case 5 -> RED;
            default -> WHITE;
        };
        return color(text, color);
    }

    public String tag(String label, String color) {
        return "[" + color(label, color) + "]";
    }


    public Object getBody(byte[] bodyBytes, String encoding) {
        Object body = null;
        try {
            String bodyString = new String(bodyBytes, encoding);
            if (!bodyString.isBlank()) {
                body = new ObjectMapper().readValue(bodyString, Object.class);
            }
        } catch (IOException exception) {
            log.warn("Cannot parse body to object");
        }
        return body;
    }
}
