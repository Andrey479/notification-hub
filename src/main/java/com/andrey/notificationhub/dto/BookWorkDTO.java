package com.andrey.notificationhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookWorkDTO {
    @JsonDeserialize(using = DescriptionDeserializer.class)
    private String description;

    //Esse Deserializer existe porque a API pode vir com um tipo de dado diferente tipo: {type, value} ou como objeto
    private static class DescriptionDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
            if (jsonParser.currentToken() == JsonToken.VALUE_STRING) {
                return jsonParser.getString();
            } else {
                JsonNode node = jsonParser.readValueAsTree();
                return node.get("value") != null ? node.get("value").asString() : null;
            }
        }
    }
}
