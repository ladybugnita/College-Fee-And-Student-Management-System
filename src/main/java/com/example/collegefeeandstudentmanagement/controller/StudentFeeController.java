package com.example.collegefeeandstudentmanagement.controller;

import com.example.collegefeeandstudentmanagement.dto.AssignFeeRequest;
import com.example.collegefeeandstudentmanagement.dto.StudentFeeResponseDTO;
import com.example.collegefeeandstudentmanagement.dto.StudentResponseDTO;
import com.example.collegefeeandstudentmanagement.entity.FeeInstallment;
import com.example.collegefeeandstudentmanagement.entity.Student;
import com.example.collegefeeandstudentmanagement.service.StudentFeeService;
import com.example.collegefeeandstudentmanagement.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/studentfee")

public class StudentFeeController {
    private final StudentFeeService feeService;
    private final StudentRepository studentRepository;

    public StudentFeeController(StudentFeeService feeService, StudentRepository studentRepository){
        this.feeService = feeService;
        this.studentRepository =studentRepository;
    }
    @PostMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> assignFee(
            @PathVariable Long studentId,
          @RequestBody AssignFeeRequest request
    ){
        return studentRepository.findById(studentId)
                .map(student -> {
                    if(student.getStudentFee() != null){
                        return ResponseEntity.status(HttpStatus.CONFLICT).<StudentFeeResponseDTO>build();
                    }
                    return feeService.assignFee(studentId,request.getTotalFee(), request.getScholarship(),request.getDiscount(),request.getYears())
                            .map(dto -> ResponseEntity.ok(dto))
                            .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> getAssignedFee(@PathVariable Long studentId){
        return feeService.getAssignedFee(studentId)
                .map(dto -> ResponseEntity.ok(dto))
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> updateFee(@PathVariable Long studentId, @RequestBody AssignFeeRequest request){
        return feeService.updateFee(studentId, request.getTotalFee(), request.getScholarship(),request.getDiscount(),request.getYears())
                .map(dto -> ResponseEntity.ok(dto))
                .orElse(ResponseEntity.notFound().build());
    }
    @PatchMapping("/assign/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> patchFee(@PathVariable Long studentId, @RequestBody AssignFeeRequest request){
        return feeService.patchFee(studentId, request.getTotalFee(),request.getScholarship(),request.getDiscount(),request.getYears())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/assign/{studentId}")
    public ResponseEntity<Void> deleteFee(@PathVariable Long studentId){
        if(feeService.deleteFee(studentId)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/payinstallment/{installmentId}")
    public ResponseEntity<FeeInstallment> payInstallment(@PathVariable Long installmentId){
        return feeService.payInstallment(installmentId)
                .map(inst ->ResponseEntity.ok(inst))
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable Long id){
        return studentRepository.findById(id)
                .map(student -> {
                    StudentResponseDTO dto = new StudentResponseDTO();
                    dto.setId(student.getId());
                    dto.setRollNo(student.getRollNo());
                    dto.setFirstName(student.getFirstName());
                    dto.setLastName(student.getLastName());
                    dto.setEmail(student.getEmail());
                    dto.setPhone(student.getPhone());
                    dto.setProgram(student.getProgram());
                    dto.setCreatedAt(student.getCreatedAt());

                    if(student.getStudentFee()!= null){
                        dto.setStudentFee(new StudentFeeResponseDTO(student.getStudentFee()));
                    }
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
