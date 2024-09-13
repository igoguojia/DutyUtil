package com.jnu.common.service.api;

import com.jnu.common.pojo.UserNN;

import java.util.List;

public interface UserService {
    /**
     * 根据组别查询员工号和姓名
     *
     * @param groupName 组别
     * @return 员工号，姓名 对象列表
     */
    List<UserNN> searchUserNNByGroupName(String groupName);
}
