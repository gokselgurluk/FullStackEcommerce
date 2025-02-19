package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.entity.FailedAttempt;

import java.util.Optional;

public interface BlockedIpService {

    Optional<BlockedIp> findByBlockedIp(String ipAddresses);
    BlockedIp blockedIpCreate(String clientIp);
    boolean blockedIpDiffTime(BlockedIp blockedIp);
    void incrementFailedIpAttempts(FailedAttempt failedAttempt);

}
