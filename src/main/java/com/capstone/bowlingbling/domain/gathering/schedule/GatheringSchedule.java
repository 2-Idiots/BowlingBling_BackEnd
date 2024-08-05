package com.capstone.bowlingbling.domain.gathering.schedule;

import com.capstone.bowlingbling.domain.gathering.repository.GatheringRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatheringSchedule {

    private final GatheringRepository gatheringRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행 초 분 시 일 월 요일(0일요일 - 6토요일)
    public void markPastGatheringsAsDeleted() {
        LocalDateTime now = LocalDateTime.now();
        gatheringRepository.findAllByDateBeforeAndDeletedAtIsNull(now)
                .forEach(gathering -> {
                    gathering.markAsDeleted();
                    gatheringRepository.save(gathering);
                });

        log.info("Mark past gatherings as deleted completed");
    }
}