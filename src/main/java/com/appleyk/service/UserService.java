package com.appleyk.service;

import com.appleyk.dao.entity.UserEntity;
import com.appleyk.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * <p>用户业务操作实现类</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 2020/5/19 10:46 PM
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<UserEntity> queryAll() {
        return userMapper.queryAll();
    }

    public UserEntity query(String uuid) {
        return userMapper.queryByUUID(uuid);
    }

    public Long count() {
        return userMapper.count();
    }
}
