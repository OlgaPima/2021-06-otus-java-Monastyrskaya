package ru.otus.jdbc.mapper;

/**
 * Создает SQL - запросы
 */
public interface IEntitySQLMetaData {
    String getSelectAllSql();

    String getSelectByIdSql();

    String getInsertSql();

    String getUpdateSql();
}
