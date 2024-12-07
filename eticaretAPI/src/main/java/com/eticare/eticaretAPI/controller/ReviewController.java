package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.entity.Review;
import com.eticare.eticaretAPI.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> createOrUpdateReview (@RequestBody Review review){
        return  ResponseEntity.ok(reviewService.createOrUpdate(review));
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReview (){
        return ResponseEntity.ok(reviewService.getAllReview());

    }

    @GetMapping("/{id}")
    ResponseEntity<Optional<Review>> getReviewById(@PathVariable Long id){
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/product/{productId}")
    ResponseEntity<List<Review>> getReviewByProductId(@PathVariable Long id){
      return   ResponseEntity.ok(reviewService.getReviewByProductId(id));
    }

    @DeleteMapping("delete/{id}")
    ResponseEntity<Review> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return  ResponseEntity.noContent().build();
    }

}
