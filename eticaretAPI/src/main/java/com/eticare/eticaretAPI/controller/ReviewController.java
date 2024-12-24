package com.eticare.eticaretAPI.controller;

import com.eticare.eticaretAPI.config.modelMapper.IModelMapperService;
import com.eticare.eticaretAPI.config.result.ResultData;
import com.eticare.eticaretAPI.config.result.ResultHelper;
import com.eticare.eticaretAPI.dto.request.Review.ReviewSaveRequest;
import com.eticare.eticaretAPI.dto.request.Review.ReviewUpdateRequest;
import com.eticare.eticaretAPI.dto.response.ReviewResponse;
import com.eticare.eticaretAPI.entity.Review;
import com.eticare.eticaretAPI.service.ReviewService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public ResultData<ReviewResponse> createReview (@RequestBody @Valid ReviewSaveRequest saveRequest ){
        Review review =modelMapper.map(saveRequest,Review.class);
        reviewService.createOrUpdate(review);
        ReviewResponse response = this.modelMapperService.forResponse().map(review,ReviewResponse.class);
        return ResultHelper.created(response);
    }
    @PutMapping("/update")
    public ResultData<ReviewResponse> updateReview(@RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest){
        Review review =this.modelMapperService.forRequest().map(reviewUpdateRequest , Review.class);
        reviewService.createOrUpdate(review);
        ReviewResponse response =this.modelMapperService.forResponse().map(review,ReviewResponse.class);
        return  ResultHelper.success(response);
    }

    @GetMapping
    public ResultData<List<ReviewResponse>> getAllReview (){
        List<Review> review =reviewService.getAllReview();
        List<ReviewResponse> response =review.stream().map(Review->this.modelMapperService.forResponse().map(Review,ReviewResponse.class)).collect(Collectors.toList());
        return ResultHelper.success(response);

    }

    @GetMapping("/{id}")
    ResultData<ReviewResponse> getReviewById(@PathVariable Long id){
        Optional<Review> review =reviewService.getReviewById(id);

        if(review.isPresent()){
            ReviewResponse response = this.modelMapperService.forResponse().map(review.get(),ReviewResponse.class);
            //Optional türü maplenemez. Önce içerdiği nesneyi .get ile almanız gerekiyor.
            return ResultHelper.created(response);
        }else{
           return ResultHelper.errorWithData("veri silme hatası",null,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/product/{productId}")
    ResultData<List<ReviewResponse>> getReviewByProductId(@PathVariable Long id){
        List<Review> reviews = reviewService.getReviewByProductId(id);
        List<ReviewResponse> response =reviews.stream().map(Review->this.modelMapperService.forResponse().map(Review,ReviewResponse.class)).collect(Collectors.toList());
        return   ResultHelper.success(response);
    }

    @DeleteMapping("delete/{id}")
    ResultData<Review> deleteReview(@PathVariable Long id){
       reviewService.deleteReview(id);
        return  ResultHelper.success(null);
    }

}
