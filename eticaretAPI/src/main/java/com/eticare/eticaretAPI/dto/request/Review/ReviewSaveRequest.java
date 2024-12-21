package com.eticare.eticaretAPI.dto.request.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewSaveRequest {

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotNull(message = "User ID cannot be null.")
    private Long userId;

    @NotNull(message = "Rating cannot be null.")
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be at most 5.")
    private Integer rating;

    @Size(max = 500, message = "Comment cannot exceed 500 characters.")
    private String comment;

}
