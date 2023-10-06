package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.dto.ArticleDto;
import com.chatviewer.blog.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ChatViewer
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 根据articleId，做连表查询，返回文章信息、作者信息
     * @param articleId 文章Id
     * @return ArticleDto
     */
    ArticleDto getWithAuthor(Long articleId);
}
