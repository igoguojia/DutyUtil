package com.jnu.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 */
@Data
@TableName("DCUSERS")
public class User {
    @TableField("OPERATOR_NO")
    private String operatorNo;
    @TableField("OPERATOR_NAME")
    private String operatorName;
    @TableField("OPERATOR_PWD")
    private String operatorPWD;
    @TableField("GROUP_NAME")
    private String groupName;
    @TableField("E_MAIL")
    private String email;
    @TableField("MOBILE_TEL")
    private String mobileTel;
    @TableField("IS_LEADER")
    private Character isLeader;
}
