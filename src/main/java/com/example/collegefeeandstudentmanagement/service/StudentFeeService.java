package com.example.collegefeeandstudentmanagement.service;

import com.example.collegefeeandstudentmanagement.entity.FeeInstallment;
import com.example.collegefeeandstudentmanagement.entity.Student;
import com.example.collegefeeandstudentmanagement.entity.StudentFee;
import com.example.collegefeeandstudentmanagement.repository.FeeInstallmentRepository;
import com.example.collegefeeandstudentmanagement.repository.StudentFeeRepository;
import com.example.collegefeeandstudentmanagement.repository.StudentRepository;
import com.example.collegefeeandstudentmanagement.dto.StudentFeeResponseDTO;
import com.example.collegefeeandstudentmanagement.dto.InstallmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public class StudentFeeService {
    private final StudentFeeRepository feeRepository;
    private final StudentRepository studentRepository;
    private final FeeInstallmentRepository installmentRepository;
    private final EsewaPaymentService esewaPaymentService;

    @Autowired
    public StudentFeeService(StudentFeeRepository feeRepository, StudentRepository studentRepository, FeeInstallmentRepository installmentRepository, EsewaPaymentService esewaPaymentService){
        this.feeRepository = feeRepository;
        this.studentRepository = studentRepository;
        this.installmentRepository= installmentRepository;
        this.esewaPaymentService = esewaPaymentService;
    }
    public Optional<StudentFeeResponseDTO> assignFee(Long studentId, BigDecimal totalFee, BigDecimal scholarship, BigDecimal discount,int years) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if(studentOpt.isEmpty()) return Optional.empty();

        Student student = studentOpt.get();

            if (student.getStudentFee() != null) {
                return Optional.empty();
            }
            BigDecimal safeScholar = Objects.requireNonNullElse(scholarship, BigDecimal.ZERO);
            BigDecimal safeDiscount = Objects.requireNonNullElse(discount, BigDecimal.ZERO);
            BigDecimal netFee = totalFee.subtract(safeScholar).subtract(safeDiscount);

            StudentFee studentFee = new StudentFee();
            studentFee.setTotalFee(totalFee);
            studentFee.setScholarshipAmount(safeScholar);
            studentFee.setDiscountAmount(safeDiscount);
            studentFee.setNetFee(netFee);
            studentFee.setCourseDurationYears(years);
            studentFee.setStudent(student);


            int count = Math.max(1, years*2);
            BigDecimal perInstallment = netFee.divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP);
            List<FeeInstallment> installments = new ArrayList<>();

            for (int i = 1; i <= count; i++) {
                FeeInstallment inst = new FeeInstallment();
                inst.setInstallmentNumber(i);
                inst.setAmount(perInstallment);
                inst.setPaid(false);
                inst.setStudentFee(studentFee);
                installments.add(inst);
            }
            studentFee.setInstallments(installments);

            student.setStudentFee(studentFee);
            feeRepository.save(studentFee);
            studentRepository.save(student);

            List<InstallmentDTO> installmentDTOs = new ArrayList<>();
            for (FeeInstallment inst : installments) {
                installmentDTOs.add(new InstallmentDTO(inst.getInstallmentNumber(), inst.getAmount(), inst.isPaid()));
            }
            StudentFeeResponseDTO response = new StudentFeeResponseDTO(
                    studentFee.getTotalFee(),
                    studentFee.getScholarshipAmount(),
                    studentFee.getDiscountAmount(),
                    studentFee.getNetFee(),
                    studentFee.getCourseDurationYears(),
                    installmentDTOs
            );
        return Optional.of(response);
    }

    public Optional<StudentFeeResponseDTO> getAssignedFee(Long studentId){
        return studentRepository.findById(studentId)
                .map(Student::getStudentFee)
                .filter(Objects::nonNull)
                .map(StudentFeeResponseDTO:: new);
    }
    public Optional<StudentFeeResponseDTO> updateFee(Long studentId, BigDecimal totalFee, BigDecimal scholarship, BigDecimal discount, int years){
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if(studentOpt.isEmpty()) return Optional.empty();
        Student student = studentOpt.get();
        StudentFee fee = student.getStudentFee();
        if(fee == null) return Optional.empty();

        BigDecimal safeScholar = Objects.requireNonNullElse(scholarship, BigDecimal.ZERO);
        BigDecimal safeDiscount = Objects.requireNonNullElse(discount, BigDecimal.ZERO);
        BigDecimal netFee = totalFee.subtract(safeScholar).subtract(safeDiscount);

        fee.setTotalFee(totalFee);
        fee.setScholarshipAmount(safeScholar);
        fee.setDiscountAmount(safeDiscount);
        fee.setNetFee(netFee);

        if(fee.getCourseDurationYears() != years || fee.getInstallments() == null || fee.getInstallments().isEmpty()){
            List<FeeInstallment> installments = fee.getInstallments();
             installments.clear();
            int count = Math.max(1, years*2);
            BigDecimal perInstallment= netFee.divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP);
            for(int i = 1; i<= count; i++){
                FeeInstallment inst = new FeeInstallment();
                inst.setInstallmentNumber(i);
                inst.setAmount(perInstallment);
                inst.setPaid(false);
                inst.setStudentFee(fee);
                installments.add(inst);
            }
        }else{
            int count = fee.getInstallments().size();
            BigDecimal perInstallment = netFee.divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP);
            for(FeeInstallment inst : fee.getInstallments()){
                inst.setAmount(perInstallment);
            }
        }
        fee.setCourseDurationYears(years);
        feeRepository.save(fee);
        studentRepository.save(student);
        return Optional.of(new StudentFeeResponseDTO(fee));
    }
    public Optional<StudentFeeResponseDTO> patchFee(
            Long studentId, BigDecimal totalFee, BigDecimal scholarship, BigDecimal discount, Integer years){
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if(studentOpt.isEmpty()) return Optional.empty();

        Student student = studentOpt.get();
        StudentFee fee = student.getStudentFee();
        if(fee == null) return Optional.empty();

        if(totalFee != null) fee.setTotalFee(totalFee);
        if(scholarship != null) fee.setScholarshipAmount(scholarship);
        if(discount != null) fee.setDiscountAmount(discount);
        if(years != null) fee.setCourseDurationYears(years);

        BigDecimal safeTotal = Objects.requireNonNullElse(fee.getTotalFee(), BigDecimal.ZERO);
        BigDecimal safeScholar = Objects.requireNonNullElse(fee.getScholarshipAmount(),BigDecimal.ZERO);
        BigDecimal safeDiscount = Objects.requireNonNullElse(fee.getDiscountAmount(),BigDecimal.ZERO);
        BigDecimal netFee = safeTotal.subtract(safeScholar).subtract(safeDiscount);
        fee.setNetFee(netFee);

        if(years != null){
            List<FeeInstallment> installments = fee.getInstallments();
            installments.clear();
            int count = Math.max(1, years*2);
            BigDecimal perInstallment = netFee.divide(BigDecimal.valueOf(count),2, RoundingMode.HALF_UP);
             for(int i =1; i<= count; i++){
                 FeeInstallment inst = new FeeInstallment();
                 inst.setInstallmentNumber(i);
                 inst.setAmount(perInstallment);
                 inst.setPaid(false);
                 inst.setStudentFee(fee);
                 installments.add(inst);
             }
        }
        feeRepository.save(fee);
        studentRepository.save(student);

        return Optional.of(new StudentFeeResponseDTO(fee));
    }
    public boolean deleteFee(Long studentId){
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if(studentOpt.isEmpty()) return false;
        Student student = studentOpt.get();
        StudentFee fee = student.getStudentFee();
        if(fee == null) return false;

        student.setStudentFee(null);
        studentRepository.save(student);
        feeRepository.delete(fee);
        return true;
    }

    public Optional<FeeInstallment> payInstallment(Long installmentId) {
        Optional<FeeInstallment> installmentOpt = installmentRepository.findById(installmentId);
        if (installmentOpt.isEmpty()) return Optional.empty();
        FeeInstallment feeInstallment = installmentOpt.get();

        String transactionId = "TXN-"+ installmentId;
        String refId = "REF-" + installmentId;
        String productId = "COLLEGE_FEE_" + installmentId;

        boolean paymentVerified = esewaPaymentService.verifyPayment(transactionId,feeInstallment.getAmount().doubleValue(),refId, productId);
        if (paymentVerified) {
            feeInstallment.setPaid(true);
            return Optional.of(installmentRepository.save(feeInstallment));
        } else {
            throw new RuntimeException("Payment verification failed for installment ID:" + installmentId);
        }
    }
    public double getInstallmentAmountByNumber(Long studentId, int installmentNumber){
        FeeInstallment installment = installmentRepository.findByStudentFee_Student_IdAndInstallmentNumber(studentId, installmentNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Installment number" + installmentNumber + " not found for student"+ studentId
                ));
        return installment.getAmount().doubleValue();
    }
    public void markInstallmentPaidByNumber(Long studentId, int installmentNumber){
       FeeInstallment installment = installmentRepository.findByStudentFee_Student_IdAndInstallmentNumber(studentId, installmentNumber)
               .orElseThrow(() -> new RuntimeException(
                       "Installment number" + installmentNumber + "not found for student" + studentId
               ));
       installment.setPaid(true);
       installmentRepository.save(installment);
        }
    }


