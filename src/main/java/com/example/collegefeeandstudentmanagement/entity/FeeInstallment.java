package com.example.collegefeeandstudentmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table (name = "fee_installments")
public class FeeInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int installmentNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "student_fee_id")
    @JsonBackReference
    private StudentFee studentFee;

    public FeeInstallment(){}
    public FeeInstallment(int installmentNumber, BigDecimal amount, StudentFee studentFee){
        this.installmentNumber = installmentNumber;
        this.amount =amount;
        this.studentFee = studentFee;
    }
    public Long getId(){
        return id;
    }
    public int getInstallmentNumber(){
        return installmentNumber;
    }
    public void setInstallmentNumber(int installmentNumber){
        this.installmentNumber = installmentNumber;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount =amount;
    }
    public StudentFee getStudentFee(){
        return studentFee;
    }
    public void setStudentFee(StudentFee studentFee){
        this.studentFee = studentFee;
    }
    public boolean isPaid(){
        return paid;
    }
    public void setPaid(boolean paid){
        this.paid= paid;
    }
}
