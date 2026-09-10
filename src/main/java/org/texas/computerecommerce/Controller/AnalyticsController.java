package org.texas.computerecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.texas.computerecommerce.Dto.AnalyticsDTO;
import org.texas.computerecommerce.Service.UserActivityService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {

    @Autowired
    private UserActivityService userActivityService;

    // Get analytics summary
    @GetMapping
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = userActivityService.getAnalyticsSummary();
        return ResponseEntity.ok(analytics);
    }

    // Track user activity (called from frontend)
    @PostMapping("/track")
    public ResponseEntity<Void> trackActivity(@RequestBody Map<String, Object> request) {
        Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
        String activityType = (String) request.get("activityType");
        String pageUrl = (String) request.get("pageUrl");
        Long productId = request.get("productId") != null ? Long.valueOf(request.get("productId").toString()) : null;
        Long categoryId = request.get("categoryId") != null ? Long.valueOf(request.get("categoryId").toString()) : null;
        String searchKeyword = (String) request.get("searchKeyword");

        userActivityService.trackActivity(userId, activityType, pageUrl, productId, categoryId, searchKeyword);

        return ResponseEntity.ok().build();
    }
}