package com.strizhonovapps.skinsearcher.osteamdia.service;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class FinanceUtils {

    private static final int STEAM_FEE_PERCENTAGE = 15;

    public int toCents(String usd) {
        return (int) (Double.parseDouble(usd) * 100d);
    }

    public int addFee(int price) {
        BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);
        int calculatedFee = bigDecimalPrice
                .multiply(
                        BigDecimal.valueOf(STEAM_FEE_PERCENTAGE).multiply(BigDecimal.valueOf(0.01))
                )
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
        int actualFee = Math.max(2, calculatedFee);
        return BigDecimal.valueOf(actualFee)
                .add(bigDecimalPrice)
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }

}