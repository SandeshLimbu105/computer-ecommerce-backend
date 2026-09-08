package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.ProductDTO;
import org.texas.computerecommerce.Dto.ReviewDTO;
import org.texas.computerecommerce.Dto.UserDTO;
import org.texas.computerecommerce.Entity.Review;
import org.texas.computerecommerce.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody Review review) {
        Review created = reviewService.createReview(review);
        return new ResponseEntity<>(convertToDTO(created), HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        List<ReviewDTO> dtos = reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        List<ReviewDTO> dtos = reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        if (review.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(review.getUser().getUserId());
            userDTO.setName(review.getUser().getName());
            userDTO.setEmail(review.getUser().getEmail());
            dto.setUser(userDTO);
        }

        if (review.getProduct() != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(review.getProduct().getProductId());
            productDTO.setName(review.getProduct().getName());
            productDTO.setBrand(review.getProduct().getBrand());
            productDTO.setPrice(review.getProduct().getPrice());
            dto.setProduct(productDTO);
        }

        return dto;
    }
}