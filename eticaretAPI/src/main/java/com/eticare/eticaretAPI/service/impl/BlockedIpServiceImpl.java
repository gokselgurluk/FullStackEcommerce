package com.eticare.eticaretAPI.service.impl;

import com.eticare.eticaretAPI.entity.BlockedIp;
import com.eticare.eticaretAPI.entity.FailedAttempt;
import com.eticare.eticaretAPI.repository.IBlockedIpRepository;
import com.eticare.eticaretAPI.service.BlockedIpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Service
public class BlockedIpServiceImpl implements BlockedIpService {
    @Override
    public void blockedIpUpdate(BlockedIp blockedIp) {
        if(blockedIp.getUnblocked_at() != null && blockedIp.getUnblocked_at().after(new Date())) {
            blockedIpDiffTime(blockedIp);
            blockedIp.setBlockedIpStatus(true);
        }
        if((blockedIp.getUnblocked_at() == null || blockedIp.getUnblocked_at().before(new Date()))&& blockedIp.isBlockedIpStatus()){
            blockedIp.setBlockedIpStatus(false);
            blockedIp.setIncrementFailedAttempts(0);
            blockedIp.setUnblocked_at(null);
            blockedIp.setDiffLockedTime(0);
            blockedIp.setBlockedIpStatus(false);
        }
        blockedIpRepository.save(blockedIp);
    }

    @Value("${MAX.FAILED.ENTER.COUNT}")
    private Integer MAX_FAILED_ENTER_COUNT;
    private final static long IP_BLOCKED_TIME =1000*60*60; //1 saat
    private final IBlockedIpRepository blockedIpRepository;

    public BlockedIpServiceImpl(IBlockedIpRepository blockedIpRepository) {
        this.blockedIpRepository = blockedIpRepository;
    }

    @Override
    public BlockedIp blockedIpCreate(String clientIp) {
        Optional<BlockedIp> optionalBlockedIp = findByBlockedIp(clientIp);
        System.out.println(optionalBlockedIp.toString());
        BlockedIp newBlockedIp = new BlockedIp();
        if(optionalBlockedIp.isEmpty()){
            newBlockedIp.setIpAddresses(clientIp);
            newBlockedIp.setBlocked_at(null);
            newBlockedIp.setUnblocked_at(null);
            newBlockedIp.setBlockedIpStatus(false);
            newBlockedIp.setDiffLockedTime(0);
        }else{
            newBlockedIp=optionalBlockedIp.get();
        }
        blockedIpRepository.save(newBlockedIp);
        return newBlockedIp;
    }

    @Override
    public Optional<BlockedIp> findByBlockedIp(String ipAddresses) {
        return  blockedIpRepository.findByIpAddresses(ipAddresses);
    }

    @Override
    public void incrementFailedIpAttempts(FailedAttempt failedAttempt) {
       BlockedIp blockedIp= failedAttempt.getBlockedIP();

        if (blockedIp.isBlockedIpStatus()){
          return;
        }
        blockedIp.setIncrementFailedAttempts(blockedIp.getIncrementFailedAttempts()+1);

        if( blockedIp.getIncrementFailedAttempts()>=MAX_FAILED_ENTER_COUNT){
            blockedIp.setBlocked_at(new Date());
            blockedIp.setUnblocked_at(new Date(System.currentTimeMillis() + IP_BLOCKED_TIME));
            blockedIpRepository.save(blockedIp);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                   /* blockedIp.setIncrementFailedAttempts(0);
                    blockedIp.setBlockedIpStatus(false);
                    blockedIpRepository.save(blockedIp);*/
                    blockedIpUpdate(blockedIp);
                }
            },blockedIp.getUnblocked_at().getTime() - System.currentTimeMillis());
        }
    }

    @Override
    public void blockedIpDiffTime(BlockedIp blockedIp) {
        long diffMillis = blockedIp.getUnblocked_at().getTime() - System.currentTimeMillis();
        long diffTimeMinute = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        blockedIp.setDiffLockedTime(Math.max(diffTimeMinute, 0));
    }
}
