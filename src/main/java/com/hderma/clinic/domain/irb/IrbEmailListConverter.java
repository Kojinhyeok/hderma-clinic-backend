package com.hderma.clinic.domain.irb;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

import java.util.List;

@Converter
public class IrbEmailListConverter implements AttributeConverter<List<IrbDto.EmailRecipient>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<IrbDto.EmailRecipient> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("JSON writing error", e);
        }
    }

    @Override
    public List<IrbDto.EmailRecipient> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) return null;
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, IrbDto.EmailRecipient.class);
            return objectMapper.readValue(dbData, listType);
        } catch (Exception e) {
            throw new RuntimeException("JSON reading error", e);
        }
    }
}