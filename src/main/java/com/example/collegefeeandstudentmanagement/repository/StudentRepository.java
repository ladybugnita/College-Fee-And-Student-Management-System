package com.example.collegefeeandstudentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.collegefeeandstudentmanagement.entity.Student;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNo(String rollNo);
    boolean existsByRollNo(String rollNo);
}
