package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.pojo.Commodity;
import com.chatviewer.blog.service.CommodityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/commodity")
public class CommodityController {

    @Resource
    CommodityService commodityService;

    @PostMapping("/add")
    public Result<Commodity> addCommodity(@RequestBody Commodity commodity) {
        commodity.setCreateTime(LocalDateTime.now());
        commodityService.save(commodity);
        return Result.success(commodity);
    }

    @PostMapping("/seckill/{id}")
    public Result<String> seckill(@PathVariable("id") Long commodityId) throws InterruptedException {
        return Result.success(commodityService.seckill(commodityId));
    }
}
