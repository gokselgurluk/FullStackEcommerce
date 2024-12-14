package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.response.ReviewResponse;
import com.eticare.eticaretAPI.entity.Review;
import com.eticare.eticaretAPI.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final IModelMapperService modelMapperService;

    public ReviewController(ReviewService reviewService, IModelMapperService modelMapperService) {
        this.reviewService = reviewService;
        this.modelMapperService = modelMapperService;
    }

    @PostMapping
    public ResponseEntity<Review> createOrUpdateReview (@RequestBody Review review){
        return  ResponseEntity.ok(reviewService.createOrUpdate(review));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReview (){
        List<Review> review =reviewService.getAllReview();
        List<ReviewResponse> response =review.stream().map(Review->this.modelMapperService.forResponse().map(Review,ReviewResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);

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
