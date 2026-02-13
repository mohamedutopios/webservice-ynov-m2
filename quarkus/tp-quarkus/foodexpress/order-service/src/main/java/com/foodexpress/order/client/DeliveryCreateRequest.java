package com.foodexpress.order.client;

public class DeliveryCreateRequest {
    public Long orderId;
    public String pickupAddress;
    public String deliveryAddress;

    public DeliveryCreateRequest() {}

    public DeliveryCreateRequest(Long orderId, String pickupAddress, String deliveryAddress) {
        this.orderId = orderId;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
    }
}
