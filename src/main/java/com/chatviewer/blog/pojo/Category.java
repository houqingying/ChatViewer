package com.chatviewer.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 码为categoryId，或者categoryName和parentId
 * @author ChatViewer
 */
@Data
@TableName(value ="blog_category")
public class Category {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private String categoryName;

    /**
     * 为了更快地找到其父节点，约定一个分类的children没有重名
     */
    private Long parentId;

    private Integer leftIndex;

    private Integer rightIndex;

}
