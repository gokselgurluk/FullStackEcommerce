package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.ModelMapper.IModelMapperService;
import com.eticare.eticaretAPI.dto.request.Review.ReviewSaveRequest;
import com.eticare.eticaretAPI.dto.request.Review.ReviewUpdateRequest;
import com.eticare.eticaretAPI.dto.response.ReviewResponse;
import com.eticare.eticaretAPI.entity.Review;
import com.eticare.eticaretAPI.service.ReviewService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final IModelMapperService modelMapperService;
    private final ModelMapper modelMapper;

    public ReviewController(ReviewService reviewService, IModelMapperService modelMapperService, ModelMapper modelMapper) {
        this.reviewService = reviewService;
        this.modelMapperService = modelMapperService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/created")
    public ResponseEntity<ReviewResponse> createReview (@RequestBody @Valid ReviewSaveRequest saveRequest ){
        Review review =modelMapper.map(saveRequest,Review.class);
        reviewService.createOrUpdate(review);
        ReviewResponse response = this.modelMapperService.forResponse().map(review,ReviewResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/update")
    public ResponseEntity<ReviewResponse> updateReview(@RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest){
        Review review =this.modelMapperService.forRequest().map(reviewUpdateRequest , Review.class);
        reviewService.createOrUpdate(review);
        ReviewResponse response =this.modelMapperService.forResponse().map(review,ReviewResponse.class);
        return new ResponseEntity<>(response , HttpStatus.valueOf("Update Completed"));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReview (){
        List<Review> review =reviewService.getAllReview();
        List<ReviewResponse> response =review.stream().map(Review->this.modelMapperService.forResponse().map(Review,ReviewResponse.class)).collect(Collectors.toList());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id){
        Optional<Review> review =reviewService.getReviewById(id);
        if(review.isPresent()){
            ReviewResponse response= this.modelMapperService.forResponse().map(review.get(),ReviewResponse.class);
            //Optional türü maplenemez. Önce içerdiği nesneyi .get ile almanız gerekiyor.
            return ResponseEntity.ok(response);
        }else{
           return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/product/{productId}")
    ResponseEntity<List<ReviewResponse>> getReviewByProductId(@PathVariable Long id){
        List<Review> reviews = reviewService.getReviewByProductId(id);
        List<ReviewResponse> response =reviews.stream().map(Review->this.modelMapperService.forResponse().map(Review,ReviewResponse.class)).collect(Collectors.toList());
        return   ResponseEntity.ok(response);
    }

    @DeleteMapping("delete/{id}")
    ResponseEntity<Review> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return  ResponseEntity.noContent().build();
    }

}
