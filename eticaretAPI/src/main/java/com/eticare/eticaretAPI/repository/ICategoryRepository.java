package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.CMS;
import com.eticare.eticaretAPI.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category,Long> {

}
