package com.higame.repository;

import com.higame.entity.UserStatistics;
import com.higame.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    Optional<UserStatistics> findByDateAndUserType(LocalDate date, UserType userType);
    List<UserStatistics> findByDateBetweenAndUserType(LocalDate startDate, LocalDate endDate, UserType userType);
    List<UserStatistics> findByUserTypeOrderByDateDesc(UserType userType);
    void deleteByDateBefore(LocalDate date);

    @Query("""
        SELECT new map(
            us.date as date,
            us.activeUsers as activeUsers,
            us.newUsers as newUsers
        )
        FROM UserStatistics us
        WHERE us.date BETWEEN :startDate AND :endDate
        ORDER BY us.date
    """)
    List<Map<String, Object>> findUserTrendsByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
