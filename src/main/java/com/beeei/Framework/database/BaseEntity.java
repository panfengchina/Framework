package com.beeei.Framework.database;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -7024713938431309774L;

    @BaseColumn(columnName ="id", columnType = "varchar(32)", columnComment = "主键id",isNullable = false)
    protected String id = UUID.randomUUID().toString().replaceAll("-", "");

    @BaseColumn(columnName ="useless", columnType = "bigint(20)", columnDefault = "0",columnComment = "是否删除，0为否",isNullable = false)
    protected Long useless = 0L;

    @BaseColumn(columnName ="creat_time", columnType = "datetime", columnComment = "记录创建时间" ,isNullable = false)
    protected Date creatTime = new Date();

    @BaseColumn(columnName ="update_time", columnType = "datetime", columnComment = "记录修改时间" ,isNullable = true)
    protected Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUseless() {
        return useless;
    }

    public void setUseless(Long useless) {
        this.useless = useless;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
