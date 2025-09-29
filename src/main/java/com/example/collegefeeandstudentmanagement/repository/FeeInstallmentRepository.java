package com.example.collegefeeandstudentmanagement.repository;

import com.example.collegefeeandstudentmanagement.entity.FeeInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeeInstallmentRepository extends JpaRepository<FeeInstallment, Long>{
 List<FeeInstallment> findByStudentFeeId(Long studentFeeId);
}
