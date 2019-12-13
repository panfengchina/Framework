package com.beeei.Framework.database;

public class BaseIndexDetails {

    private String Key_name;

    private String Column_name;

    private Boolean Non_unique;

    public BaseIndexDetails(String Key_name, String Column_name, Boolean Non_unique) {
        this.Key_name = Key_name;
        this.Column_name = Column_name;
        this.Non_unique = Non_unique;
    }

    public String getKey_name() {
        return Key_name;
    }

    public void setKey_name(String key_name) {
        Key_name = key_name;
    }

    public String getColumn_name() {
        return Column_name;
    }

    public void setColumn_name(String column_name) {
        Column_name = column_name;
    }

    public Boolean getNon_unique() {
        return Non_unique;
    }

    public void setNon_unique(Boolean non_unique) {
        Non_unique = non_unique;
    }

}
