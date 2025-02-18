package com.eticare.eticaretAPI.service;

import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.entity.FailedAttempt;

import java.util.List;
import java.util.Map;

public interface FailedAttemptService {
    void createOrUpdateFailedAttempt(String email, BlockedIp blockedIP, Map<String, String> userAgent);
    void recordFailedAttempts(String email);
    boolean isFailedAttemptsValid(String email, String blockedIP, String device);
    List<FailedAttempt> getRecordFailedAttemptList(String email);

}