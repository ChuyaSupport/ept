package com.ept.powersupport.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 该类用于经纬度距离计算，每隔0.01度，距离相差约1000米；
 */
@Component
public class DistanceCalc {

    public BigDecimal calcuateDistance(BigDecimal business_longitude, BigDecimal business_latitude, BigDecimal user_longitude, BigDecimal user_latitude) {

        BigDecimal SUBRES1 = business_longitude.subtract(user_longitude);
        BigDecimal SUBRES2 = business_latitude.subtract(user_latitude);
        BigDecimal SQUARES1 = SUBRES1.multiply(SUBRES1);
        BigDecimal SQUARES2 = SUBRES2.multiply(SUBRES2);
        BigDecimal RES = SQUARES1.subtract(SQUARES2);
        return BigDecimal.valueOf(Math.sqrt(Math.abs(RES.doubleValue())));
    }

}
