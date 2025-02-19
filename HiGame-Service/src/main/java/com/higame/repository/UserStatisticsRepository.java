package com.higame.repository;

import com.higame.entity.UserStatistics;
import com.higame.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    Optional<UserStatistics> findByDateAndUserType(LocalDate date, UserType userType);
    List<UserStatistics> findByDateBetweenAndUserType(LocalDate startDate, LocalDate endDate, UserType userType);
    List<UserStatistics> findByUserTypeOrderByDateDesc(UserType userType);
    void deleteByDateBefore(LocalDate date);
}
