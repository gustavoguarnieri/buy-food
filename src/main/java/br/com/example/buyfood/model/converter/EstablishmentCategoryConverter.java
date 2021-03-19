package br.com.example.buyfood.model.converter;

import br.com.example.buyfood.enums.EstablishmentCategory;

import javax.persistence.AttributeConverter;

public class EstablishmentCategoryConverter implements AttributeConverter<EstablishmentCategory, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EstablishmentCategory attribute) {
        if (attribute == null)
            return null;

        switch (attribute) {
            case RESTAURANTE:
                return 1;

            case PIZZARIA:
                return 2;

            case BAR:
                return 3;

            default:
                throw new IllegalArgumentException(attribute + " not supported.");
        }
    }

    @Override
    public EstablishmentCategory convertToEntityAttribute(Integer dbData) {
        if (dbData == null)
            return null;

        switch (dbData) {
            case 1:
                return EstablishmentCategory.RESTAURANTE;

            case 2:
                return EstablishmentCategory.PIZZARIA;

            case 3:
                return EstablishmentCategory.BAR;

            default:
                throw new IllegalArgumentException(dbData + " not supported.");
        }
    }
}