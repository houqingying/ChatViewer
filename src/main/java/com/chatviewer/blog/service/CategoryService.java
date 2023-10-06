package com.chatviewer.blog.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatviewer.blog.dto.CategoryDto;
import com.chatviewer.blog.pojo.Category;

import java.util.List;


/**
 * @author ChatViewer
 */
public interface CategoryService extends IService<Category> {


    /**
     * 返回组织好的category树，即以列表形式，返回根节点的所有子节点
     * 如果Redis中存在，则直接从Redis中读取，不必再进行查询
     * @return 返回顶层根节点的所有子节点
     */
    List<CategoryDto> allCategory();


    /**
     * 1、删除category下的所有节点，设左右坐标为[l, r]，则所有[l, r]内的节点都需要删除，
     * 2、width = r - l + 1，后面的节点应该往前移动的步数
     *   然后更新所有 左坐标 > r -> - width
     *             右坐标 > r -> - width
     * @param categoryId 分类Id
     */
    void deleteCategory(Long categoryId);


    /**
     * 查询category的所有子孙节点（包含category本身）
     * @param categoryId 分类Id
     * @return List<Category>
     */
    List<Category> childrenOf(Long categoryId);


    /**
     * 查询category的所有子孙节点Id（包含category本身）
     * @param categoryId 分类Id
     * @return List<Long>
     */
    List<Long> childrenIdOf(Long categoryId);

    /**
     * 根据父节点与分类名，插入分类，更新数据库并令缓存失效
     * @param parentId 父节点Id
     * @param categoryName 分类名称
     * @return 插入后的分类实体
     */
    Category insertAfter(Long parentId, String categoryName);


    /**
     * 插入路径上的所有分类节点，约定一个分类下的孩子分类不重名
     * @param categoryPath 分类节点列表，由上至下层级递增
     */
    void insertPath(List<String> categoryPath);

}
