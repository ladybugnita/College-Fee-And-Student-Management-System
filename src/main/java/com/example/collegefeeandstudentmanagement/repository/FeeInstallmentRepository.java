package com.example.collegefeeandstudentmanagement.repository;

import com.example.collegefeeandstudentmanagement.entity.FeeInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FeeInstallmentRepository extends JpaRepository<FeeInstallment, Long>{
 Optional <FeeInstallment> findByStudentFee_Student_IdAndInstallmentNumber(Long studentId,int installmentNumber);
}
