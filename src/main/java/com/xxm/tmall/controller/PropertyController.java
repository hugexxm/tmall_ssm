package com.xxm.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.Property;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.PropertyService;
import com.xxm.tmall.util.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("")
public class PropertyController {

    @Resource
    CategoryService categoryService;

    @Resource
    PropertyService propertyService;

    @RequestMapping("admin_property_list")
    public String list(Model model, Page page, int cid) { // 关于参数的传递，我的猜测，普通类型不会放进model中。
        Category c = categoryService.get(cid);

        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Property> ps = propertyService.list(cid);

        int total = (int) new PageInfo<>(ps).getTotal();
        page.setTotal(total);
        page.setParam("&cid=" + c.getId());
        model.addAttribute("ps", ps);
        model.addAttribute("page", page);
        model.addAttribute("c", c);

        return "admin/listProperty";
    }

    @RequestMapping("admin_property_add")
    public String add(Property property) { // name cid
        propertyService.add(property);
        return "redirect:/admin_property_list?cid=" + property.getCid();
    }

    @RequestMapping("admin_property_edit")
    public String edit(Model model, int id) {
        Property property = propertyService.get(id);
        Category c = categoryService.get(property.getCid());
        property.setCategory(c);
        model.addAttribute("p", property);
        return "admin/editProperty";
    }

    @RequestMapping("admin_property_update")
    public String update(Property property, int id, int cid) { // 有歧义的时候，传参要名字吻合，这里的id，cid是一个示意。
        propertyService.update(property);
        return "redirect:/admin_property_list?cid=" + property.getCid();
    }

    @RequestMapping("admin_property_delete")
    public String delete(int id) {
        Property property = propertyService.get(id);
        propertyService.delete(id);
        return "redirect:/admin_property_list?cid=" + property.getCid();
    }
}
