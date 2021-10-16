package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс - генератор SQL-запросов для указанной сущности
 * @param <T> тип сущности
 */
public class EntitySQLMetaData<T> implements IEntitySQLMetaData {

    //Класс параметризован Т, чтобы избежать ругани оптимизатора "Raw use of parameterized class" и стирания типов
    //(иначе много вылезало ниже по коду)
    private final IEntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaData(IEntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
//        StringBuilder result = new StringBuilder();
//        entityClassMetaData.getAllFields().forEach(field -> result.append(field.getClass() + ", "));

        return "select " + concatenate(entityClassMetaData.getAllFields(), ", ") +
                " from " + entityClassMetaData.getName();
    }

    @Override
    public String getSelectByIdSql() {
        return getSelectAllSql() + " where " + entityClassMetaData.getIdField().getName() + " = ?";
    }

    @Override
    public String getInsertSql() {
        if (entityClassMetaData.getFieldsWithoutId().size() == 0)
            return "";

        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        StringBuilder result = new StringBuilder(
                "insert into " + entityClassMetaData.getName() + "(" + concatenate(fieldsWithoutId, ", ") + ")" +
                " values(");
        for (int i = 0; i < fieldsWithoutId.size(); i++) {
            result.append(i == fieldsWithoutId.size()-1 ? "?" : "?, ");
        }
        return result + ")";
    }

    @Override
    public String getUpdateSql() {
        if (entityClassMetaData.getFieldsWithoutId().size() == 0)
            return "";
        String result = "update " + entityClassMetaData.getName() +
                " set " + concatenate(entityClassMetaData.getFieldsWithoutId(), " = ?, ") + "= ? " +
                " where " + entityClassMetaData.getIdField().getName() + " = ?";

        return result;
    }

    /**
     * Объединение списка в одну строку через разделитель
     * @param listOfFields
     * @param separator
     * @return
     */
    private String concatenate(List<Field> listOfFields, String separator) {
        return listOfFields.stream().map(Field::getName).collect(Collectors.joining(separator));
    }
}
