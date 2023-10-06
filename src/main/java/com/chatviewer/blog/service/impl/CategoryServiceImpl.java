package com.chatviewer.blog.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.dto.CategoryDto;
import com.chatviewer.blog.mapper.CategoryMapper;
import com.chatviewer.blog.pojo.Category;
import com.chatviewer.blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.chatviewer.blog.constant.RedisConstant.CATEGORY_ALL_KEY;
import static com.chatviewer.blog.constant.RedisConstant.CATEGORY_ALL_TTL;

/**
 * @author ChatViewer
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 根分类的ID与名称
     */
    public static final Long ROOT_ID = 0L;
    public static final String ROOT_CATEGORY = "根分类";


    /**
     * 返回组织好的category树，即以列表形式，返回根节点的所有子节点
     * 如果Redis中存在，则直接从Redis中读取，不必再进行查询
     * @return 返回顶层根节点的所有子节点
     */
    @Override
    public List<CategoryDto> allCategory() {
        List<CategoryDto> categoryDtos;
        if (BooleanUtil.isTrue(stringRedisTemplate.hasKey(CATEGORY_ALL_KEY))) {
            // 先查询Redis
            String jsonStr = stringRedisTemplate.opsForValue().get(CATEGORY_ALL_KEY);
            categoryDtos = JSONUtil.toList(jsonStr, CategoryDto.class);
            log.info("从Redis中获取目录信息");
        }
        else {
            //  查询不到，访问数据库、存入Redis
            categoryDtos = allCategoryOf(ROOT_ID);
            String jsonStr = JSONUtil.toJsonStr(categoryDtos);
            stringRedisTemplate.opsForValue().set(CATEGORY_ALL_KEY, jsonStr, CATEGORY_ALL_TTL, TimeUnit.MINUTES);
            log.info("从MySQL中获取目录信息");
        }
        return categoryDtos;
    }


    /**
     * 以列表形式，返回某个节点的的所有子节点，
     * @param categoryId 节点唯一ID
     * @return 子节点Dto列表，按照前端需要的value、label、children形式组织
     */
    private List<CategoryDto> allCategoryOf(Long categoryId) {
        // 1、选出所有一级节点
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 可以考虑在parent列建立索引
        queryWrapper.eq(Category::getParentId, categoryId);
        List<Category> categories = this.list(queryWrapper);
        // 2、向结果里填充数据
        List<CategoryDto> res = new ArrayList<>();
        for (Category category: categories) {
            CategoryDto dto = new CategoryDto();
            dto.setValue(category.getCategoryId());
            dto.setLabel(category.getCategoryName());
            // 递归调用得到孩子节点
            dto.setChildren(allCategoryOf(category.getCategoryId()));
            res.add(dto);
        }
        return res;
    }


    /**
     * 前提条件：应保证该分类下没有文章和习题，否则会有问题哦
     * 1、删除category下的所有节点，设左右坐标为[l, r]，则所有[l, r]内的节点都需要删除，
     * 2、width = r - l + 1，后面的节点应该往前移动的步数
     *   然后更新所有 左坐标 > r -> - width
     *             右坐标 > r -> - width
     * @param categoryId 分类Id
     */
    @Override
    public void deleteCategory(Long categoryId) {
        Category category = getById(categoryId);

        int left = category.getLeftIndex();
        int right = category.getRightIndex();

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 删除[l, r]内的节点
        queryWrapper.ge(Category::getLeftIndex, left);
        queryWrapper.le(Category::getRightIndex, right);
        this.remove(queryWrapper);

        // width = r - l + 1，后面的节点应该往前移动的步数
        // 左坐标 > r -> - width
        // 右坐标 > r -> - width
        categoryMapper.updateAfterDelete(right, right - left + 1);

        // 让缓存失效
        stringRedisTemplate.delete(CATEGORY_ALL_KEY);
    }


    /**
     * 查询category的所有子孙节点（包含category本身）
     * 设左右坐标为[l, r]，即查询所有所有[l, r]内的节点
     * @param categoryId 分类Id
     * @return List<Category> 查询结果
     */
    @Override
    public List<Category> childrenOf(Long categoryId) {
        Category category = this.getById(categoryId);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // >= l, <= r
        queryWrapper.ge(Category::getLeftIndex, category.getLeftIndex());
        queryWrapper.le(Category::getRightIndex, category.getRightIndex());
        return this.list(queryWrapper);
    }


    /**
     * 查询category的所有子孙节点Id（包含category本身）
     * 设左右坐标为[l, r]，即查询所有所有[l, r]内的节点
     * @param categoryId 分类Id
     * @return List<Category> 查询结果
     */
    @Override
    public List<Long> childrenIdOf(Long categoryId) {
        Category category = this.getById(categoryId);
        return categoryMapper.queryChildrenId(category.getLeftIndex(), category.getRightIndex());
    }


    /**
     * 查询category的所有祖先节点（包含category本身）
     * 设左右坐标为[l, r]，即查询所有所有左坐标小于等于l，右坐标大于等于r的节点
     * @param categoryId 分类Id
     * @return List<Category> 查询结果
     */
    public List<Category> ancestorsOf(Long categoryId) {
        Category category = this.getById(categoryId);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(Category::getLeftIndex, category.getLeftIndex());
        queryWrapper.ge(Category::getRightIndex, category.getRightIndex());
        // 按照左序号排序，可以得到自顶向下的目录
        queryWrapper.orderByAsc(Category::getLeftIndex);
        return this.list(queryWrapper);
    }


    /**
     * 在parentId后插入新节点
     * 插入该节点，左坐标 >= parent.r -> + 2
     *          右坐标 >= parent.r -> + 2
     * @param parentId 其父节点
     * @param categoryName 待插入节点名
     * @return 生成的待插入节点
     */
    @Override
    public Category insertAfter(Long parentId, String categoryName) {
        Category parent = getById(parentId);
        return insertAfter(parent, categoryName);
    }


    /**
     * 约定新增的节点在同一层最右侧
     * 插入该节点，左坐标 >= parent.r -> + 2
     *          右坐标 >= parent.r -> + 2
     * @param parent 父节点
     * @param categoryName 待插入节点名
     * @return 生成的待插入节点
     */
    public Category insertAfter(Category parent, String categoryName) {
        // 更新 >= 父节点右坐标的所有index
        categoryMapper.updateBeforeInsert(parent.getRightIndex());
        // 插入新节点，设置各个属性
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setParentId(parent.getCategoryId());
        category.setLeftIndex(parent.getRightIndex());
        category.setRightIndex(category.getLeftIndex() + 1);
        this.save(category);
        // 让缓存失效
        stringRedisTemplate.delete(CATEGORY_ALL_KEY);
        return category;
    }


    /**
     * 通过parentId与当前节点名称，从数据库获取节点实体
     * @param parentId 父节点Id
     * @param categoryName 节点名
     * @return 待查询分类
     */
    public Category getByParent(Long parentId, String categoryName) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, parentId);
        queryWrapper.eq(Category::getCategoryName, categoryName);
        return this.getOne(queryWrapper);
    }


    @Override
    public void insertPath(List<String> categoryPath) {
        // 初始化为根节点
        Category parent = getById(ROOT_ID);
        for (String categoryName: categoryPath) {
            // 根据父节点Id与该节点名称，检查category是否在表中
            Category category = this.getByParent(parent.getCategoryId(), categoryName);
            if (category == null) {
                category = insertAfter(parent, categoryName);
            }
            parent = category;
        }
    }

}
