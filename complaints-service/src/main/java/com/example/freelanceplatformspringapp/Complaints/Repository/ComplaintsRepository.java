package com.example.freelanceplatformspringapp.Complaints.Repository;

import com.example.freelanceplatformspringapp.Complaints.Entity.Complaints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintsRepository extends JpaRepository<Complaints,Long> {
    List<Complaints> findByUserId(Long userId);

    Optional<Complaints> findByIdReclamationAndUserId(Long idReclamation, Long userId);
}
