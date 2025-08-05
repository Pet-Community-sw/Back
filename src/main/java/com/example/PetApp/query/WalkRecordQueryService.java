package com.example.PetApp.query;

import com.example.PetApp.domain.WalkRecord;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalkRecordQueryService {
    private final WalkRecordRepository walkRecordRepository;

    public WalkRecord findByWalkRecord(Long walkRecordId) {
        return walkRecordRepository.findById(walkRecordId).orElseThrow(() -> new NotFoundException("해당 산책기록은 없습니다."));
    }
}
