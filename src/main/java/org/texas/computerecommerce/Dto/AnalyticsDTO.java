package org.texas.computerecommerce.Dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalyticsDTO {
    private Long totalPageViews;
    private Long totalProductViews;
    private Long totalSearches;
    private Long totalAddToCart;
    private Long totalPurchases;
    private List<Map<String, Object>> topProducts;
    private List<Map<String, Object>> popularSearches;
}