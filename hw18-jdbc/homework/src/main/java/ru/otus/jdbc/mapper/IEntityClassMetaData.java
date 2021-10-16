package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * "Разбирает" объект на составные части
 */
public interface IEntityClassMetaData<T> {
    String getName();

//    Constructor<T> getConstructor() throws NoSuchMethodException, SecurityException;

    T createEntityInstance(HashMap<String, Object> valueMapper);

    //Поле Id должно определять по наличию аннотации Id
    //Аннотацию @Id надо сделать самостоятельно
    Field getIdField();

    List<Field> getAllFields();

    List<Field> getFieldsWithoutId();

    List<Object> getFieldValues(T entity, List<Field> fieldsToRead);
}
