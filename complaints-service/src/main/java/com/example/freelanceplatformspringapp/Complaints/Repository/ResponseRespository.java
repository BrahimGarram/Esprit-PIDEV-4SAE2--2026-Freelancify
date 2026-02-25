package com.example.freelanceplatformspringapp.Complaints.Repository;

import com.example.freelanceplatformspringapp.Complaints.Entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRespository extends JpaRepository<Response,Long> {
}
