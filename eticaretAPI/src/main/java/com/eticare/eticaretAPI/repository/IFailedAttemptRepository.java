package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.FailedAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFailedAttemptRepository extends JpaRepository<FailedAttempt , Long> {
    List<FailedAttempt> findByEmail(String email);
}
