package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.entity.FailedAttempt;
import com.eticare.eticaretAPI.repository.IBlockedIpRepository;
import com.eticare.eticaretAPI.repository.IFailedAttemptRepository;
import com.eticare.eticaretAPI.service.BlockedIpService;
import com.eticare.eticaretAPI.service.FailedAttemptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class FailedAttemptServiceServiceImpl implements FailedAttemptService {

    private  final IFailedAttemptRepository failedAttemptRepository;
    private  final IBlockedIpRepository blockedIpRepository;
    private  final BlockedIpService blockedIpService;
    public FailedAttemptServiceServiceImpl(IFailedAttemptRepository failedAttemptRepository, IBlockedIpRepository blockedIpRepository, BlockedIpService blockedIpService) {
        this.failedAttemptRepository = failedAttemptRepository;
        this.blockedIpRepository = blockedIpRepository;
        this.blockedIpService = blockedIpService;
    }

    @Override
    public void createOrUpdateFailedAttempt(String email, BlockedIp blockedIP, Map<String, String> userAgent) {
        FailedAttempt failedAttempt = new FailedAttempt();
        failedAttempt.setEmail(email);
        failedAttempt.setBlockedIP(blockedIP);
        failedAttempt.setAttemptTime(new Date());
        failedAttempt.setDeviceInfo(userAgent.get("Device"));
        failedAttempt.setBrowser(userAgent.get("Browser"));
        failedAttempt.setOs(userAgent.get("OS"));
        failedAttemptRepository.save(failedAttempt);
    }

    @Override
    public void recordFailedAttempts(String email) {
        List<FailedAttempt> failedAttemptList = getRecordFailedAttemptList(email);
         FailedAttempt failedAttempt = failedAttemptList.get(failedAttemptList.size()-1);
        blockedIpService.incrementFailedIpAttempts(failedAttempt);
       failedAttemptRepository.save(failedAttempt);



    }


    @Override
    public boolean isFailedAttemptsValid(String email, String blockedIP, String device) {
      List<FailedAttempt> failedAttemptList = failedAttemptRepository.findByEmail(email);
      for(FailedAttempt failedAttempt : failedAttemptList){
          if (!failedAttempt.getBlockedIP().equals(blockedIP)) {
              continue;
          }
          return failedAttempt.getBlockedIP().isBlockedIpStatus();
      }
    return false;
    }



    @Override
    public List<FailedAttempt> getRecordFailedAttemptList(String email) {
        return failedAttemptRepository.findByEmail(email);
    }

}
