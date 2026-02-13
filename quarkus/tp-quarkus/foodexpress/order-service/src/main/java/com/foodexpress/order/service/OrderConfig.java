package com.foodexpress.order.service;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import java.math.BigDecimal;

@ConfigMapping(prefix = "foodexpress.order")
public interface OrderConfig {

    @WithName("minimum-amount")
    @WithDefault("10.00")
    BigDecimal minimumAmount();

    @WithName("max-delivery-radius-km")
    @WithDefault("15")
    int maxDeliveryRadiusKm();
}
