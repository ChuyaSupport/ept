package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/mece")
public class GroupLookup {

    @Autowired
    private BusinessServiceImpl businessService;

    @RequestMapping("/getGrpInfo")
    public Object getGrpInfo() {

        return businessService.grpInfoLookup();
    }
}
