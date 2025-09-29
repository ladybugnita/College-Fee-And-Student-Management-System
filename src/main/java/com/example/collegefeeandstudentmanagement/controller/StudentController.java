package com.example.collegefeeandstudentmanagement.controller;

import com.example.collegefeeandstudentmanagement.dto.StudentFeeResponseDTO;
import com.example.collegefeeandstudentmanagement.dto.StudentResponseDTO;
import com.example.collegefeeandstudentmanagement.dto.UpdateStudentDTO;
import com.example.collegefeeandstudentmanagement.entity.Student;
import com.example.collegefeeandstudentmanagement.repository.StudentRepository;
import com.example.collegefeeandstudentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/students")

public class StudentController {
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public StudentController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student saved = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> ResponseEntity.ok(mapToDTO(student)))
                .orElse(ResponseEntity.notFound().build());
    }
    private StudentResponseDTO mapToDTO(Student student){
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setRollNo(student.getRollNo());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setProgram(student.getProgram());
        dto.setCreatedAt(student.getCreatedAt());

        if(student.getStudentFee() != null){
            dto.setStudentFee(new StudentFeeResponseDTO(student.getStudentFee()));
        }
        return dto;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) {
        Student updated = studentService.updateStudent(id, student);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteStudent (@PathVariable Long id){
            if (studentService.deleteStudent(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        }
    @PatchMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentDTO dto){
        Optional<Student> updated = studentService.updateStudent(id, dto);
        return updated
                .map(student ->ResponseEntity.<Student>ok(student))
                .orElse(ResponseEntity.notFound().build());
    }
}
