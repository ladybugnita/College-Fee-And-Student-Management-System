package com.example.collegefeeandstudentmanagement.controller;

import com.example.collegefeeandstudentmanagement.dto.AssignFeeRequest;
import com.example.collegefeeandstudentmanagement.dto.StudentFeeResponseDTO;
import com.example.collegefeeandstudentmanagement.entity.Student;
import com.example.collegefeeandstudentmanagement.service.StudentFeeService;
import com.example.collegefeeandstudentmanagement.repository.StudentRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/studentfee")
public class StudentFeeController {

    private final StudentFeeService feeService;
    private final StudentRepository studentRepository;

    public StudentFeeController(StudentFeeService feeService, StudentRepository studentRepository) {
        this.feeService = feeService;
        this.studentRepository = studentRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> assignFee(
            @PathVariable Long studentId,
            @RequestBody AssignFeeRequest request
    ) {
        return studentRepository.findById(studentId)
                .map(student -> {
                    if (student.getStudentFee() != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).<StudentFeeResponseDTO>build();
                    }
                    return feeService.assignFee(studentId, request.getTotalFee(), request.getScholarship(),
                                    request.getDiscount(), request.getYears())
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> getAssignedFee(@PathVariable Long studentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Student student = studentOpt.get();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))
                && !student.getEmail().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return feeService.getAssignedFee(studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> updateFee(@PathVariable Long studentId, @RequestBody AssignFeeRequest request) {
        return feeService.updateFee(studentId, request.getTotalFee(), request.getScholarship(),
                        request.getDiscount(), request.getYears())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> patchFee(@PathVariable Long studentId, @RequestBody AssignFeeRequest request) {
        return feeService.patchFee(studentId, request.getTotalFee(), request.getScholarship(),
                        request.getDiscount(), request.getYears())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/discount/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> updateDiscount(
            @PathVariable Long studentId,
            @RequestBody Map<String, Object> body
    ) {
        if (!body.containsKey("discount")) {
            return ResponseEntity.badRequest().build();
        }

        BigDecimal newDiscount = new BigDecimal(body.get("discount").toString());
        var updated = feeService.updateDiscount(studentId, newDiscount);
        return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/assign/{studentId}")
    public ResponseEntity<Void> deleteFee(@PathVariable Long studentId) {
        if (feeService.deleteFee(studentId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}