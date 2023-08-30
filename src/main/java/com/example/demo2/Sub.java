package com.example.demo2;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author wanglei
 * @since 2023/6/14 00:57
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class Sub {

    @NotBlank(message = "name不能为空")
    @Length(min = 0, max = 10, message = "name长度不能超过10")
    private String sid;

}
