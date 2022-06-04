package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.resObj.ResBusCommodity;
import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/business/mece")
public class CommodityLookup {

    @Autowired
    private BusinessServiceImpl businessService;

    @RequestMapping("/comLookup")
    public Object commodityLookup() {

        List list = businessService.comLookup();

        return list;
    }
}
