package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.CMS;
import com.eticare.eticaretAPI.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICMSRepository extends JpaRepository<CMS,Long> {


    List<CMS> findByAuthorId(Long authorId);
}
