package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.Review;
import com.eticare.eticaretAPI.repository.IProductRepository;
import com.eticare.eticaretAPI.repository.IReviewRepository;
import com.eticare.eticaretAPI.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private  final IReviewRepository reviewRepository;


    public ReviewServiceImpl(IReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;

    }

    @Override
    public Review create(Review review) {

        return  reviewRepository.save(review);
    }

    @Override
    public List<Review> getAllReview() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public void deleteReview(Long id) {
        if(reviewRepository.existsById(id)){
            reviewRepository.deleteById(id);
        }else{
           throw new RuntimeException("Review Not found with id "+ id);
        }

    }
}
