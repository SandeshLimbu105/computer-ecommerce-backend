// UserActivityRepository.java
package org.texas.computerecommerce.Repository;

import org.texas.computerecommerce.Entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findByUserId(Long userId);

    List<UserActivity> findByActivityType(String activityType);

    @Query("SELECT COUNT(a) FROM UserActivity a WHERE a.activityType = :type AND a.createdAt >= :startDate")
    long countActivitiesByTypeAndDate(@Param("type") String type, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.productId, COUNT(a) as count FROM UserActivity a " +
            "WHERE a.activityType = 'PRODUCT_VIEW' AND a.createdAt >= :startDate " +
            "GROUP BY a.productId ORDER BY count DESC")
    List<Object[]> getMostViewedProducts(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.searchKeyword, COUNT(a) as count FROM UserActivity a " +
            "WHERE a.activityType = 'SEARCH' AND a.createdAt >= :startDate " +
            "GROUP BY a.searchKeyword ORDER BY count DESC")
    List<Object[]> getPopularSearches(@Param("startDate") LocalDateTime startDate);
}