package ru.otus.jdbc.mapper;

import ru.otus.repository.DataTemplate;
import ru.otus.repository.executor.DbExecutable;
import ru.otus.sessionmanager.DataBaseOperationException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Сохраняет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutable dbExecutable;
    private final IEntityClassMetaData<T> entityClassMetaData;
    private final IEntitySQLMetaData entitySQLMetaData;

    public DataTemplateJdbc(DbExecutable dbExecutable, IEntityClassMetaData<T> entityClassMetaData) { //IEntitySQLMetaData entitySQLMetaData) {
        this.dbExecutable = dbExecutable;
        this.entityClassMetaData = entityClassMetaData;
        this.entitySQLMetaData = new EntitySQLMetaData<>(entityClassMetaData);
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutable.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id),
                (resultSet) -> createEntityInstance(resultSet));
    }

    @Override
    public List<T> findAll(Connection connection) {
        Optional<List<T>> result =
                dbExecutable.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), List.of(),
            (resultSet) -> {
                ArrayList<T> resList = null;
                T entity = createEntityInstance(resultSet);
                while (entity != null) {
                    if (resList == null) {
                        resList = new ArrayList<>();
                    }
                    resList.add(entity);
                    entity = createEntityInstance(resultSet);
                }
                return resList;
            }
        );
        return result.orElse(null);
    }

    @Override
    public long insert(Connection connection, T entity) {
        List<Object> valuesForInsert = entityClassMetaData.getFieldValues(entity, entityClassMetaData.getFieldsWithoutId());
        return dbExecutable.executeStatement(connection, entitySQLMetaData.getInsertSql(), valuesForInsert);
    }

    @Override
    public void update(Connection connection, T entity) {
        List<Field> allFields = entityClassMetaData.getFieldsWithoutId();
        //Поле id нужно переместить в конец списка для корректного формирования Sql-оператора update:
        allFields.remove(entityClassMetaData.getIdField());
        allFields.add(entityClassMetaData.getIdField());

        List<Object> valuesForUpdate = entityClassMetaData.getFieldValues(entity, allFields);
        dbExecutable.executeStatement(connection, entitySQLMetaData.getUpdateSql(), valuesForUpdate);
    }

    private T createEntityInstance(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                //привыкаю к концепции стримов. Понятно, что проще и прозрачнее написать цикл for, но мне захотелось так
//                Object[] args = IntStream.range(1, resultSet.getMetaData().getColumnCount()+1)
//                        .mapToObj(i -> getObjectValue(resultSet, i)).toArray();
                HashMap<String, Object> valuesMapper = new HashMap<>();
                ResultSetMetaData rsStructure = resultSet.getMetaData();
                for(int i = 1; i <= rsStructure.getColumnCount(); i++) {
                    valuesMapper.put(rsStructure.getColumnName(i), resultSet.getObject(i));
                }
                return entityClassMetaData.createEntityInstance(valuesMapper);
            }
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseOperationException("DataTemplateJdbc: ошибка получения записи из БД", e);
        }
    }

    private Object getObjectValue(ResultSet rs, int columnIndex) {
        Object result;
        try {
            result = rs.getObject(columnIndex);
        }
        catch (SQLException ex) {
            throw new DataBaseOperationException("DataTemplateJdbc: ошибка получения значения для поля № " + columnIndex, ex);
        }
        return result;
    }
}
