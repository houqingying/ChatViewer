package com.chatviewer.blog.controller;

import com.chatviewer.blog.base.Result;
import com.chatviewer.blog.pojo.Category;
import com.chatviewer.blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;


/**
 * @author ChatViewer
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    CategoryService categoryService;

    /**
     * 返回组织好的category树，即以列表形式，返回根节点的所有子节点
     * 如果Redis中存在，则直接从Redis中读取，不必再进行MySQL查询
     */
    @GetMapping("/allCategory")
    public Result<Object> allCategory() {
        return Result.success(categoryService.allCategory());
    }

    /**
     * 插入路径上的所有分类节点，约定一个分类下的孩子分类不重名
     */
    @PostMapping("/insertPath")
    public Result<Object> insertPath(@RequestBody List<String> categoryPath) {
        categoryService.insertPath(categoryPath);
        return Result.success();
    }

    /**
     * 根据父节点与分类名，插入分类，更新数据库并令缓存失效
     */
    @PostMapping("/insertAfter")
    public Result<Object> insertAfter(Long parentId, String categoryName) {
        Category category = categoryService.insertAfter(parentId, categoryName);
        return Result.success(category);
    }

    /**
     * 删除category下的所有节点
     */
    @DeleteMapping("")
    public Result<Object> delete(Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success();
    }
}
