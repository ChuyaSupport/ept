package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/mece")
public class GrpDel {

    @Autowired
    private BusinessServiceImpl businessService;

    @RequestMapping("/grpDel")
    private Object grpDel(@RequestParam String group_id) {

        if(businessService.grpDelete(group_id))
            return 1;
        else
            return 0;
    }
}
