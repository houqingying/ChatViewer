package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.Like;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ChatViewer
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    /**
     * 根据entityType更新文章表或评论表likeCounts
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param likeCounts 点赞数
     */
    void updateLikeCounts(Integer entityType, Long entityId, Integer likeCounts);
}
