package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createOrUpdate(Review review);
    List<Review> getAllReview();
    Optional<Review> getReviewById(Long id);
    List<Review> getReviewByProductId(Long productId);
    void deleteReview (Long id);

}
