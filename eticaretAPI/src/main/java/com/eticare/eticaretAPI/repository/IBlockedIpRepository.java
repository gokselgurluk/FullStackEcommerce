package com.eticare.eticaretAPI.repository;

import com.eticare.eticaretAPI.entity.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBlockedIpRepository  extends JpaRepository<BlockedIp ,Long> {
    Optional<BlockedIp> findByIpAddresses(String ipAddresses);

}
