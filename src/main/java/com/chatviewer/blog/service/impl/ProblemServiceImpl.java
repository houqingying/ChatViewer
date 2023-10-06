package com.chatviewer.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.mapper.ProblemMapper;
import com.chatviewer.blog.pojo.Problem;
import com.chatviewer.blog.service.CategoryService;
import com.chatviewer.blog.service.ProblemService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author ChatViewer
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    CategoryService categoryService;

    /**
     * 根据分类查询问题列表
     * @param page 页面序号
     * @param pageSize 页面大小
     * @param categoryId 分类id
     * @return Page<Problem>
     */
    @Override
    public Page<Problem> pageOfCategory(int page, int pageSize, Long categoryId) {
        // 设置好分页所用Page
        Page<Problem> pageInfo = new Page<>(page, pageSize);

        // 构造条件构造器, 添加过滤条件和排序条件
        LambdaQueryWrapper<Problem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Problem::getCreateTime);

        // 得到子分类列表，判断是否在子分类中
        if (categoryId != null) {
            List<Long> ids = categoryService.childrenIdOf(categoryId);
            queryWrapper.in(Problem::getCategoryId, ids);
        }

        // 查询
        this.page(pageInfo, queryWrapper);
        return pageInfo;
    }

}
