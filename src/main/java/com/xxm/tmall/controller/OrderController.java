package com.xxm.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxm.tmall.pojo.Order;
import com.xxm.tmall.service.OrderItemService;
import com.xxm.tmall.service.OrderService;
import com.xxm.tmall.service.UserService;
import com.xxm.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Resource
    OrderService orderService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    UserService userService;

    @RequestMapping("admin_order_list")
    public String list(Model model, Page page) {
        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Order> os = orderService.list();
        int totalPage = (int) new PageInfo<>(os).getTotal();
        page.setTotal(totalPage);

        orderItemService.fill(os);
        model.addAttribute("page", page);
        model.addAttribute("os", os);
        return "admin/listOrder";
    }

    @RequestMapping("admin_order_delivery")
    public String delivery(int id) {
        Order order = orderService.get(id);
        order.setStatus(OrderService.waitConfirm);
        order.setDeliveryDate(new Date());
        orderService.update(order);
        return "redirect:/admin_order_list";
    }
}
