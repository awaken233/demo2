package com.example.demo2;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author evtok
 * @since 2023/6/19 22:16
 */
@Data
public class Req {

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
}
