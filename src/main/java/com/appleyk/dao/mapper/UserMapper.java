package com.appleyk.dao.mapper;

import com.appleyk.dao.entity.UserEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * <p>用户通用Mapper接口</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 2020/5/19 10:46 PM
 */
public interface UserMapper extends Mapper<UserEntity> {
    @Select("select count(*) from tuser")
    Long count();
    List<UserEntity> queryAll();
    UserEntity queryByUUID(String uuid);
}
