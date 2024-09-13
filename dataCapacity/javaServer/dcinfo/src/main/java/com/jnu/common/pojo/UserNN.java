package com.jnu.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 */
@Data
@TableName("DCUSERS")
public class UserNN {
    @TableField("OPERATOR_NO")
    private String operatorNo;
    @TableField("OPERATOR_NAME")
    private String operatorName;
}
