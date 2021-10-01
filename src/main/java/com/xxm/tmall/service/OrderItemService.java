package com.xxm.tmall.service;

import com.xxm.tmall.pojo.Order;
import com.xxm.tmall.pojo.OrderItem;

import java.util.List;

public interface OrderItemService {

    void add(OrderItem orderItem);
    void delete(int id);
    void update(OrderItem orderItem);
    OrderItem get(int id);
    List<OrderItem> list();

    void fill(List<Order> orders);
    void fill(Order order);
}
