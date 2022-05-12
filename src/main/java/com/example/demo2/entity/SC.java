package com.example.demo2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wlei3
 * @since 2022/5/12 11:51
 */
@Data
@TableName("SC")
public class SC {

    @TableId(type = IdType.AUTO)
    private Integer scId;

    private Integer sId;

    private Integer cId;

    private Integer score;
}
