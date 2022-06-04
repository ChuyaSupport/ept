package com.ept.powersupport.controller.user;

import com.ept.powersupport.resObj.CommodityDtl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class GetCommodityDtl {

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/getCommodityDtl")
    public Object getComDtl(@RequestParam String eptcode, @RequestParam String com_id) {

        CommodityDtl commodityDtl = userService.getCommodityDtl(com_id, eptcode);

        return commodityDtl;
    }
}
