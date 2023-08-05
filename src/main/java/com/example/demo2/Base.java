package com.example.demo2;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * @author wanglei
 * @since 2023/6/14 00:57
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class Base {

    @NotNull(message = "id不能为空")
    private Integer id;
}
