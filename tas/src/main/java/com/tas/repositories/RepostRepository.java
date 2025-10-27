package com.tas.repositories;

import com.tas.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepostRepository extends JpaRepository<Report,Long> {
}
