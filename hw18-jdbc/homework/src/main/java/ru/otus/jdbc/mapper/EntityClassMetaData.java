package ru.otus.jdbc.mapper;

import reflection.ReflectionHelper;
import java.lang.reflect.Field;
import java.util.*;

public class EntityClassMetaData<T> implements IEntityClassMetaData<T> {

    private final Class<T> classRef;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaData(Class<T> classRef) {
        this.classRef = classRef;
        //TODO: а можно ли выцепить Т без передачи параметра в конструкторе?
//        classRef = (Class<T>) ((ParameterizedType)
//                this.getClass().getGenericInterfaces()[0])
//                .getActualTypeArguments()[0];

//        Type type = getClass().getGenericSuperclass();
//        ParameterizedType paramType = (ParameterizedType) type;
//        Class<T> temp =  (Class<T>) paramType.getActualTypeArguments()[0];
//
//        int a = 0;

        List<Field> idFields = ReflectionHelper.findFieldsAnnotatedBy(classRef, Id.class);
        idField = idFields.size() > 0 ? (Field)idFields.toArray()[0] : null;

        if (idFields.size() > 1) { //TODO: проверить, нужен ли try catch в HomeWork.main()
            throw new RuntimeException("Найдено больше 1 поля в классе " + classRef.getName() + " с аннотацией @Id. " +
                    "Такое поле должно быть только одно.");
        }

        allFields = Arrays.asList(classRef.getDeclaredFields());

        fieldsWithoutId = new ArrayList<>(Arrays.asList(classRef.getDeclaredFields()));
        if (idField != null) {
            fieldsWithoutId.remove(idField);
        }
    }

    @Override
    public String getName() {
        return classRef.getSimpleName();
    }

    @Override
    public T createEntityInstance(HashMap<String, Object> valuesMapper) {
        T instance = ReflectionHelper.instantiate(classRef);
        valuesMapper.forEach((colName, colValue) -> ReflectionHelper.setFieldValue(instance, colName, colValue));
        return instance;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return new ArrayList<>(allFields);
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return new ArrayList<>(fieldsWithoutId);
    }

    @Override
    public List<Object> getFieldValues(T entity, List<Field> fieldsToRead) {
        ArrayList<Object> result = new ArrayList<>();
        fieldsToRead.forEach(field -> result.add(ReflectionHelper.getFieldValue(entity, field.getName())));
        return result;
    }
}
