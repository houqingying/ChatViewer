package com.chatviewer.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatviewer.blog.mapper.OrderMapper;
import com.chatviewer.blog.pojo.Order;
import com.chatviewer.blog.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * @author ChatViewer
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


}
