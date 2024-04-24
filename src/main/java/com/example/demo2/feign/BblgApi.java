package com.example.demo2.feign;

import com.example.demo2.config.FeignConfiguration;
import com.example.demo2.vo.BaseDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bblg", url = "test.benbenwang.com/bblg", configuration = FeignConfiguration.class)
public interface BblgApi {

    /**
     * 查询企业信息
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "/interface/company/queryCompanyInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    String queryCompanyInfo(@RequestBody BaseDataVo vo);

    /**
     * 批量结算
     * @param vo
     * @return
     */
    @PostMapping("/interface/settle/settleBatch")
    BaseDataVo settleBatch(@RequestBody BaseDataVo vo);

    /**
     * 查询项目
     * @param vo
     * @return
     */
    @PostMapping("/interface/item/myItem")
    BaseDataVo myItem(@RequestBody BaseDataVo vo);

    /**
     * 查询任务
     * @param vo
     * @return
     */
    @PostMapping("/interface/task/mytask")
    BaseDataVo mytask(@RequestBody BaseDataVo vo);

    @PostMapping("/interface/settle/queryBatch")
    BaseDataVo settleQueryBatch(@RequestBody BaseDataVo vo);
}