package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);


}
