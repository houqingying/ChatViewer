package com.chatviewer.blog.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author ChatViewer
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 在插入节点之前需要先更新右边树结构的index
     * @param right 待插入节点的父节点右坐标
     */
    void updateBeforeInsert(int right);

    /**
     * 在删除节点之前需要先更新右边树结构的index
     * @param right 待删除节点的右坐标
     * @param width 待删除节点的宽度
     */
    void updateAfterDelete(int right, int width);

    /**
     * 给定节点的左右index，求得节点的所有子孙节点id
     * @param left 根节点左坐标
     * @param right 根节点右坐标
     * @return 包括根节点在内的所有子孙节点id
     */
    List<Long> queryChildrenId(int left, int right);
}
