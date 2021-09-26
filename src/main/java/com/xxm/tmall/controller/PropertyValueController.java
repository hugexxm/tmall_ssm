package com.xxm.tmall.controller;

import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.pojo.PropertyValue;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.ProductService;
import com.xxm.tmall.service.PropertyValueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class PropertyValueController {

    @Resource
    ProductService productService;

    @Resource
    PropertyValueService propertyValueService;

    @Resource
    CategoryService categoryService;

    @RequestMapping("admin_propertyValue_edit")
    public String edit(Model model, int pid) {
        Product p = productService.get(pid);
        p.setCategory(categoryService.get(p.getCid()));
        List<PropertyValue> pvs = propertyValueService.list(pid);
        propertyValueService.init(p);
        model.addAttribute("p", p);
        model.addAttribute("pvs", pvs);
        return "admin/editPropertyValue";
    }

    @RequestMapping("admin_propertyValue_update")
    @ResponseBody
    public String update(PropertyValue pv) { // value id
        propertyValueService.update(pv);
        return "success";
    }
}
