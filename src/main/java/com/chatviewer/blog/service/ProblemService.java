package com.chatviewer.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.pojo.Problem;

/**
 * @author ChatViewer
 */
public interface ProblemService extends IService<Problem> {

    /**
     * 根据分类查询问题列表
     * @param page 页面序号
     * @param pageSize 页面大小
     * @param categoryId 分类id
     * @return Page<Problem>
     */
    Page<Problem> pageOfCategory(int page, int pageSize, Long categoryId);

}
