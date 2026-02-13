package com.foodexpress.order.client;

import java.time.LocalDateTime;

public class DeliveryDTO {
    public Long id;
    public Long orderId;
    public Long driverId;
    public String status;
    public String pickupAddress;
    public String deliveryAddress;
    public LocalDateTime estimatedTime;
}
