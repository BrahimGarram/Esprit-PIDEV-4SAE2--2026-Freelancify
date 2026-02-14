package com.example.servicetest.repository;
import com.example.servicetest.entity.AffectationTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffectationTestRepository extends JpaRepository<AffectationTest, Long> {

}