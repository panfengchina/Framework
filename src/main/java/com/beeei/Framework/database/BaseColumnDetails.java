package com.beeei.Framework.database;

import java.io.Serializable;

public class BaseColumnDetails implements Serializable {

    private static final long serialVersionUID = -8651623872761522964L;
    private String columnName;
    private String columnType;
    private String columnDefault;
    private Boolean isNullable;
    private String columnComment;

    public BaseColumnDetails(String columnName, String columnType, String columnDefault, Boolean isNullable,
                             String columnComment) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnDefault = columnDefault;
        this.isNullable = isNullable;
        this.columnComment = columnComment;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public Boolean getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}