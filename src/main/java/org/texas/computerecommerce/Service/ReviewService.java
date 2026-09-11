package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Entity.Review;
import org.texas.computerecommerce.Entity.User;
import org.texas.computerecommerce.Repository.ProductRepository;
import org.texas.computerecommerce.Repository.ReviewRepository;
import org.texas.computerecommerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Review createReview(Review review) {
        User user = userRepository.findById(review.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + review.getUser().getUserId()));
        Product product = productRepository.findById(review.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + review.getProduct().getProductId()));

        // ✅ FIX (bug #12): reject ratings outside the valid 1-5 range
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        review.setUser(user);
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    // ✅ NEW (fix for bug #6): lets the controller check ownership before delete
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found: " + reviewId));
    }

    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId);
    }

    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUser_UserId(userId);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}