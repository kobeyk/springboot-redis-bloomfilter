package com.appleyk.dao.entity;

import javax.persistence.Table;
import java.util.Date;

/**
 * <p>用户数据实体</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 2020/5/19 10:46 PM
 */
@Table(name = "tuser")
public class UserEntity {

    private String uuid;
    private Date cTime;

    public UserEntity(){

    }

    public UserEntity(String uuid, Date cTime) {
        this.uuid = uuid;
        this.cTime = cTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }
}
