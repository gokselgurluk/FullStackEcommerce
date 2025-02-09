package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import com.eticare.eticaretAPI.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ICodeRepository extends JpaRepository<Code, Long> {
    Optional<Code> findByUserAndCode(User user, String code);

    Optional<Code> findByUser(User user);


}
