package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.Product;
import com.eticare.eticaretAPI.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review,Long> {

    List<Review> findByProductId(Long productId);
}
