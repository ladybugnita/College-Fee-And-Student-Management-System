package com.example.collegefeeandstudentmanagement.dto;

import com.example.collegefeeandstudentmanagement.entity.FeeInstallment;
import com.example.collegefeeandstudentmanagement.entity.StudentFee;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public class StudentFeeResponseDTO {
    private BigDecimal totalFee;
    private BigDecimal scholarshipAmount;
    private BigDecimal discountAmount;
    private BigDecimal netFee;
    private int courseDurationYears;
    private  List<InstallmentDTO> installments;

    public StudentFeeResponseDTO(BigDecimal totalFee, BigDecimal scholarshipAmount, BigDecimal discountAmount, BigDecimal netFee, int courseDurationYears, List<InstallmentDTO> installments){
        this.totalFee = totalFee;
        this.scholarshipAmount = scholarshipAmount;
        this.discountAmount = discountAmount;
        this.netFee = netFee;
        this.courseDurationYears = courseDurationYears;
        this.installments = installments == null ? new ArrayList<>() : installments;
    }
    public StudentFeeResponseDTO(StudentFee fee){
        if(fee == null){
            this.installments = new ArrayList<>();
            return;
        }
        this.totalFee = fee.getTotalFee();
        this.scholarshipAmount = fee.getScholarshipAmount();
        this.discountAmount = fee.getDiscountAmount();
        this.netFee = fee.getNetFee();
        this.courseDurationYears = fee.getCourseDurationYears();

        List<FeeInstallment> insts = fee.getInstallments();
        if(insts == null){
            this.installments = new ArrayList<>();
        } else {
            this.installments = insts.stream()
                    .map(i-> new InstallmentDTO(i.getInstallmentNumber(), i.getAmount(),i.isPaid()))
                    .collect(Collectors.toList());
        }
    }

    public BigDecimal getTotalFee(){
        return totalFee;
    }
    public void setTotalFee(BigDecimal totalFee){
        this.totalFee =totalFee;
    }
    public BigDecimal getScholarshipAmount(){
        return scholarshipAmount;
    }
    public void setScholarshipAmount(BigDecimal scholarshipAmount){
        this.scholarshipAmount =scholarshipAmount;
    }
    public BigDecimal getDiscountAmount(){
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount){
        this.discountAmount =discountAmount;
    }
    public BigDecimal getNetFee(){
        return netFee;
    }
    public void setNetFee(BigDecimal netFee){
        this.netFee = netFee;
    }
    public int getCourseDurationYears(){
        return courseDurationYears;
    }
    public void setCourseDurationYears(int courseDurationYears){
        this.courseDurationYears = courseDurationYears;
    }
    public List<InstallmentDTO> getInstallments(){
        return installments;
    }
    public void setInstallments(List<InstallmentDTO> installments){
        this.installments = installments;
    }
}
