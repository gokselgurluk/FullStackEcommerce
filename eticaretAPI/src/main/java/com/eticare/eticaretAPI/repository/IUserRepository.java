package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUserName(String username);


}
