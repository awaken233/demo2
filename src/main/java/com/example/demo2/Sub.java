package com.example.demo2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author wanglei
 * @since 2023/6/14 00:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Sub extends Base{

    private Integer sid;

    private Req req;
}
