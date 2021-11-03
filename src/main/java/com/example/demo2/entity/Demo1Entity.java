package com.example.demo2.entity;

/**
 * @author wlei3
 * @since 2021/6/7 18:29
 */
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("demo1")
public class Demo1Entity {

    @TableId
    private Integer id;

    @TableField("name")
    private Integer name;

    @TableField("gmt_leave")
    private LocalDate gmtLeave;
}
