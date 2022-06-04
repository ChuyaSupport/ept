package com.ept.powersupport.controller.sysInterface;

import com.ept.powersupport.service.SysService.GrpStatUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GrpStatusChkJob {

    @Autowired
    private GrpStatUpdate grpStatUpdate;

    @RequestMapping("/grpStatusUpdate")
    public void grpStatusUpdate() {
        grpStatUpdate.update();
    }

    @RequestMapping("PayIntime")
    public void payIntimeTask(){

    }
}