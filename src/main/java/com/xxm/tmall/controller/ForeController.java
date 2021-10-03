package com.xxm.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.xxm.tmall.comparator.*;
import com.xxm.tmall.pojo.*;
import com.xxm.tmall.service.*;
import com.xxm.tmall.util.Param;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("") // 表示访问的时候不需要额外的地址
public class ForeController {

    @Resource
    CategoryService categoryService;
    @Resource
    UserService userService;
    @Resource
    OrderItemService orderItemService;
    @Resource
    OrderService orderService;
    @Resource
    ProductImageService productImageService;
    @Resource
    ProductService productService;
    @Resource
    PropertyService propertyService;
    @Resource
    ReviewService reviewService;
    @Resource
    PropertyValueService propertyValueService;

    @RequestMapping("forehome")
    public String home(Model model, HttpServletRequest request) {
        String uri = request.getContextPath();
        PageHelper.offsetPage(0, 16);
        List<Category> cs = categoryService.list();
        categoryService.fillProducts(cs);
        categoryService.fillProductsByRow(cs);
        Param param = new Param();
        param.setCategorycount(100);
        model.addAttribute("cs", cs);
        model.addAttribute("param1", param);
//        model.addAttribute("param.categorycount", 3);
        return "fore/home";
    }

    @RequestMapping("foreregister")
    public String register(Model model, User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist) {
            String msg = "用户名已经被使用，不能使用";
            model.addAttribute("msg", msg);
            model.addAttribute("user", null);
            return "fore/register";
        }
        userService.add(user);
        return "redirect:registerSuccessPage";
    }

    @RequestMapping("forelogin")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password,
                        Model model, HttpSession session) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);

        if(user == null) {
            model.addAttribute("msg", "账号密码错误");
            return "fore/login";
        }

        session.setAttribute("user", user);
        return "redirect:forehome";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    @RequestMapping("foreproduct")
    public String product(Model model, int pid) {
        Product p = productService.get(pid);
        List<ProductImage> imagesSingle = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> imagesDetail = productImageService.list(pid, ProductImageService.type_detail);
        p.setProductSingleImages(imagesSingle);
        p.setProductDetailImages(imagesDetail);

        p.setSaleCount(orderItemService.getSaleCount(pid));
        p.setReviewCount(reviewService.getCount(pid));

        List<Review> reviews = reviewService.list(pid);
        List<PropertyValue> pvs = propertyValueService.list(pid);

        model.addAttribute("p", p);
        model.addAttribute("reviews", reviews);
        model.addAttribute("pvs", pvs);

        return "fore/product";
    }

    /**
     * @ResponseBody
     * 作用: 其实是将java对象转为json格式的数据。
     *
     * @responseBody注解的作用是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，写入到response对象的body区，通常用来返回JSON数据或者是XML数据。
     * 注意：在使用此注解之后不会再走视图处理器，而是直接将数据写入到输入流中，他的效果等同于通过response对象输出指定格式的数据。
     * ————————————————
     * 版权声明：本文为CSDN博主「originations」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/originations/article/details/89492884
     */
    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(null == user)
            return "fail";
        else
            return "success";
    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name,
                            @RequestParam("password") String password,
                            HttpSession session) {
        User user = userService.get(name, password);
        if(null == user)
            return "fail";
        else {
            session.setAttribute("user", user);
            return "success";
        }
    }

    @RequestMapping("forecategory")
    public String category(Model model, int cid, String sort, HttpServletRequest request) {
        String uri = request.getContextPath();
        Category c = categoryService.get(cid);
        categoryService.fillProducts(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        if(sort != null) {
            switch (sort) {
                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;
                default:
                    break;
            }
        }

        model.addAttribute("c", c);
        return "fore/category";
    }

    @RequestMapping("foresearch")
    public String search(Model model, String keyword) {
        List<Product> ps = productService.search(keyword);
        ps = (ps.size() > 20) ? ps.subList(0, 20) : ps;
        productService.setSaleAndReviewNumber(ps);
        model.addAttribute("ps", ps);
        return "fore/searchResult";
    }

    @RequestMapping("forebuyone")
    public String buyone(int pid, int num, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        int oid = 0;
        boolean found = false;
        for(OrderItem oi : ois) {
            if(oi.getPid() == pid) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                oid = oi.getId();
                break;
            }
        }

        if(!found) {
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setPid(pid);
            oi.setNumber(num);
            orderItemService.add(oi);
            oid = oi.getId();
        }
        return "redirect:forebuy?oiid=" + oid;
    }

    @RequestMapping("forebuy")
    public String buy(Model model, String[] oiid, HttpSession session) {
        List<OrderItem> ois = new ArrayList<>();
        double total = 0;
        for(String str : oiid) {
            int oid = 0;
            try {
                oid = Integer.parseInt(str);
                OrderItem oi = orderItemService.get(oid);
                oi.setProduct(productService.get(oi.getPid()));
                total += oi.getProduct().getPromotePrice()*oi.getNumber();
                ois.add(oi);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        session.setAttribute("ois", ois);
        model.addAttribute("total", total);

        return "fore/buy";
    }

    @RequestMapping("foreaddCart")
    @ResponseBody
    public String addCart(int pid, int num, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());

        boolean found = false;
        for(OrderItem oi : ois) {
            if(oi.getPid() == pid) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }

        if(found == false) {
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setPid(pid);
            oi.setNumber(num);
            orderItemService.add(oi);
        }

        return "success";
    }

    @RequestMapping("forecart")
    public String cart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        model.addAttribute("ois", ois);
        return "fore/cart";
    }

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(OrderItem oi) { // id, pid, num
        orderItemService.update(oi);
        return "success";
    }

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(int oiid) {
        orderItemService.delete(oiid);
        return "success";
    }

    @RequestMapping("forecreateOrder")
    public String createOrder(Order order, HttpSession session){
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setStatus(OrderService.waitPay);
        order.setUid(user.getId());

        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        float total = orderService.add(order, ois);
        return "redirect:forealipay?oid=" + order.getId() + "&total=" + total;
    }

    @RequestMapping("forepayed")
    public String payed(Model model, int oid, float total) {
        Order order = orderService.get(oid);
        order.setPayDate(new Date());
        order.setStatus(OrderService.waitDelivery);
        orderService.update(order);
        model.addAttribute("o", order);
        return "fore/payed"; // 服务端跳转，会将forepayed?total=total 中的total一并带到param中
    }

    @RequestMapping("forebought")
    public String bought(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Order> orders = orderService.list(user.getId(), OrderService.delete);
        orderItemService.fill(orders);
        model.addAttribute("os", orders);

        return "fore/bought";
    }

    @RequestMapping("foreconfirmPay")
    public String confirmPay(Model model, int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        model.addAttribute("o", order);

        return "fore/confirmPay";
    }

    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);

        return "fore/orderConfirmed";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);

        return "success";
    }

    @RequestMapping("forereview")
    public String review(Model model, int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        List<OrderItem> ois = order.getOrderItems();
        Product product = ois.get(0).getProduct();
        productService.setSaleAndReviewNumber(product);
        List<Review> reviews = reviewService.list(product.getId());

        model.addAttribute("p", product);
        model.addAttribute("o", order);
        model.addAttribute("reviews", reviews);
        return "fore/review";
    }

    @RequestMapping("foredoreview")
    public String doreview(Review review, int oid, HttpSession session) { // oid, pid
        review.setCreateDate(new Date());
        reviewService.update(review);

        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);

        int pid = review.getPid();
        Product product = productService.get(pid);
        String content = HtmlUtils.htmlEscape(review.getContent());


        User user = (User) session.getAttribute("user");
        review.setUid(user.getId());
        review.setUser(user);
        review.setContent(content);
        review.setCreateDate(new Date());

        reviewService.add(review);

        return "redirect:forereview?oid=" + oid + "&showonly=true";
    }

}
