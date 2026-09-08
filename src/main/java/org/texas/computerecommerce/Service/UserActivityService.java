// UserActivityService.java
package org.texas.computerecommerce.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.texas.computerecommerce.Entity.UserActivity;
import org.texas.computerecommerce.Repository.UserActivityRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository activityRepository;

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
        if (userAgent.contains("mobile")) {
            return "MOBILE";
        } else if (userAgent.contains("tablet")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }

    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            return "CHROME";
        } else if (userAgent.contains("firefox")) {
            return "FIREFOX";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "SAFARI";
        } else if (userAgent.contains("edg")) {
            return "EDGE";
        } else if (userAgent.contains("opera")) {
            return "OPERA";
        } else {
            return "OTHER";
        }
    }

    // Analytics Methods
    public Map<String, Object> getAnalyticsSummary() {
        Map<String, Object> analytics = new HashMap<>();
        LocalDateTime last7Days = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        analytics.put("totalPageViews", activityRepository.countActivitiesByTypeAndDate("PAGE_VIEW", last7Days));
        analytics.put("totalProductViews", activityRepository.countActivitiesByTypeAndDate("PRODUCT_VIEW", last7Days));
        analytics.put("totalSearches", activityRepository.countActivitiesByTypeAndDate("SEARCH", last7Days));
        analytics.put("totalAddToCart", activityRepository.countActivitiesByTypeAndDate("ADD_TO_CART", last7Days));
        analytics.put("totalPurchases", activityRepository.countActivitiesByTypeAndDate("PURCHASE", last7Days));

        // Most viewed products
        List<Object[]> topProducts = activityRepository.getMostViewedProducts(last7Days);
        analytics.put("topProducts", topProducts.stream().limit(5).toArray());

        // Popular searches
        List<Object[]> popularSearches = activityRepository.getPopularSearches(last7Days);
        analytics.put("popularSearches", popularSearches.stream().limit(5).toArray());

        return analytics;
    }
}