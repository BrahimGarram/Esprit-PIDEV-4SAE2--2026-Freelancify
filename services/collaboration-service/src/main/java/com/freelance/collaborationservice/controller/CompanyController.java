package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CompanyDTO;
import com.freelance.collaborationservice.dto.CreateCompanyRequest;
import com.freelance.collaborationservice.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final CompanyService companyService;

    /** Only ENTERPRISE can create a company */
    @PostMapping
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<CompanyDTO> create(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyDTO created = companyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<CompanyDTO>> getByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(companyService.getByOwnerId(ownerId));
    }
}
