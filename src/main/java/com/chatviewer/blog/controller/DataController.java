package com.chatviewer.blog.controller;


import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 统计网站API接口访问情况
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/data")
public class DataController {

    @Resource
    DataService dataService;

    @GetMapping("/uv")
    public Result<Object> queryUv(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start, @DateTimeFormat(pattern = "yyyy-MM-dd")Date end) {
        return Result.success(dataService.calIntervalUv(start, end));
    }
}
