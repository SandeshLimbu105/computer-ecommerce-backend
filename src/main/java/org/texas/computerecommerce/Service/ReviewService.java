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

        review.setUser(user);
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
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