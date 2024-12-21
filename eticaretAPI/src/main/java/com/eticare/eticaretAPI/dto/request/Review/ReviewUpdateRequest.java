package com.eticare.eticaretAPI.dto.request.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReviewUpdateRequest {
    @Positive(message = "ID Değeri pozitif olmak zorunda")
    private long id; // Review ID to identify the resource being updated

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating; // Updated rating (1-5)

    private String comment; // Updated comment

    private Boolean isApproved; // Approval status (can be updated or left unchanged)
}
