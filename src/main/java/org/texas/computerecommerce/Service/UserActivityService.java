package org.texas.computerecommerce.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.texas.computerecommerce.Dto.AnalyticsDTO;
import org.texas.computerecommerce.Entity.UserActivity;
import org.texas.computerecommerce.Repository.UserActivityRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository activityRepository;

    // ✅ Track activity without HttpServletRequest (for API calls)
    public void trackActivity(Long userId, String activityType, String pageUrl,
                              Long productId, Long categoryId, String searchKeyword) {
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setActivityType(activityType);
        activity.setPageUrl(pageUrl);
        activity.setProductId(productId);
        activity.setCategoryId(categoryId);
        activity.setSearchKeyword(searchKeyword);
        activity.setCreatedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    // ✅ Track activity with HttpServletRequest (for web tracking)
    public void trackActivity(Long userId, String activityType, String pageUrl,
                              Long productId, Long categoryId, String searchKeyword,
                              HttpServletRequest request) {
        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setSessionId(getSessionId(request));
        activity.setActivityType(activityType);
        activity.setPageUrl(pageUrl);
        activity.setProductId(productId);
        activity.setCategoryId(categoryId);
        activity.setSearchKeyword(searchKeyword);
        activity.setReferrerUrl(request.getHeader("Referer"));
        activity.setIpAddress(getClientIp(request));
        activity.setUserAgent(request.getHeader("User-Agent"));
        activity.setDeviceType(getDeviceType(request));
        activity.setBrowser(getBrowser(request));
        activityRepository.save(activity);
    }

    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-ID");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }
        return sessionId;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        if (userAgent.contains("mobile")) return "MOBILE";
        if (userAgent.contains("tablet")) return "TABLET";
        return "DESKTOP";
    }

    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        if (userAgent.contains("chrome") && !userAgent.contains("edg")) return "CHROME";
        if (userAgent.contains("firefox")) return "FIREFOX";
        if (userAgent.contains("safari") && !userAgent.contains("chrome")) return "SAFARI";
        if (userAgent.contains("edg")) return "EDGE";
        if (userAgent.contains("opera")) return "OPERA";
        return "OTHER";
    }

    // ✅ Updated to return AnalyticsDTO
    public AnalyticsDTO getAnalyticsSummary() {
        AnalyticsDTO analytics = new AnalyticsDTO();
        LocalDateTime last7Days = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        analytics.setTotalPageViews(activityRepository.countActivitiesByTypeAndDate("PAGE_VIEW", last7Days));
        analytics.setTotalProductViews(activityRepository.countActivitiesByTypeAndDate("PRODUCT_VIEW", last7Days));
        analytics.setTotalSearches(activityRepository.countActivitiesByTypeAndDate("SEARCH", last7Days));
        analytics.setTotalAddToCart(activityRepository.countActivitiesByTypeAndDate("ADD_TO_CART", last7Days));
        analytics.setTotalPurchases(activityRepository.countActivitiesByTypeAndDate("PURCHASE", last7Days));

        // Most viewed products
        List<Object[]> topProducts = activityRepository.getMostViewedProducts(last7Days);
        List<Map<String, Object>> topProductsList = topProducts.stream()
                .limit(5)
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", row[0]);
                    map.put("views", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
        analytics.setTopProducts(topProductsList);

        // Popular searches
        List<Object[]> popularSearches = activityRepository.getPopularSearches(last7Days);
        List<Map<String, Object>> popularSearchesList = popularSearches.stream()
                .limit(5)
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", row[0]);
                    map.put("count", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
        analytics.setPopularSearches(popularSearchesList);

        return analytics;
    }
}