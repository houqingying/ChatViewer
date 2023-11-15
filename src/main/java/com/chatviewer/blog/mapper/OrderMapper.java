package com.chatviewer.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chatviewer.blog.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * @author ChaterViewer
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询购买过commodityId的用户，以限制不能重复购买
     * @param commodityId 商品id
     * @return 用户id列表
     */
    @Select("select user_id from blog_order where commodity_id = #{commodityId} and status in (0, 1)")
    List<Long> queryBoughtUsers(Long commodityId);

}
