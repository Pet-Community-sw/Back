package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.WalkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WalkRecordRepository extends JpaRepository<WalkRecord, Long> {
}
