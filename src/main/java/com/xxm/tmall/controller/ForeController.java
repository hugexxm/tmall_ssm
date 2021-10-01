package com.xxm.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.User;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.UserService;
import com.xxm.tmall.util.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("") // 表示访问的时候不需要额外的地址
public class ForeController {

    @Resource
    CategoryService categoryService;

    @Resource
    UserService userService;

    @RequestMapping("forehome")
    public String home(Model model) {
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

}
