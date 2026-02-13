package com.foodexpress.customer.dto;

import java.util.List;

public class PaginatedResponse<T> {

    public List<T> data;
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;

    public PaginatedResponse() {}

    public PaginatedResponse(List<T> data, int page, int size, long totalElements) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
}
