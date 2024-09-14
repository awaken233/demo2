package com.example.demo2.entity;

/**
 * @author wlei3
 * @since 2021/6/7 18:29
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("demo1")
public class Demo1Entity {

    @TableId
    private Long id;

    @TableField("name")
    private String value;

}
