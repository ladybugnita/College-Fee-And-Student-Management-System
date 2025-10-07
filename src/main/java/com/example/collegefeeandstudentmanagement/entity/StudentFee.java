package com.example.collegefeeandstudentmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name ="student_fees", uniqueConstraints = {
        @UniqueConstraint(columnNames = "student_id")
})

public class StudentFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name= "student_id",nullable =false, unique =true)
    @JsonBackReference
    private Student student;

    @Column(nullable = false)
    private BigDecimal totalFee;

    private BigDecimal scholarshipAmount= BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable =false)
    private BigDecimal netFee;

    private int courseDurationYears;

    @OneToMany(mappedBy = "studentFee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FeeInstallment> installments = new ArrayList<>();
    public StudentFee(){}
        public StudentFee(Student student, BigDecimal totalFee, BigDecimal scholarshipAmount, BigDecimal discountAmount, BigDecimal netFee, int years)
        {
            this.student = student;
            this.totalFee = totalFee;
            this.scholarshipAmount = scholarshipAmount;
            this.discountAmount = discountAmount;
            this.netFee = totalFee.subtract(scholarshipAmount).subtract(discountAmount);
            this.courseDurationYears = years;
        }

        public Long getId(){
            return id;
        }
        public Student getStudent(){
                return student;
            }
            public void setStudent(Student student){
            this.student =student;
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
            this.scholarshipAmount = scholarshipAmount;
        }
        public BigDecimal getDiscountAmount(){
            return discountAmount;
        }
        public void setDiscountAmount(BigDecimal discountAmount){
            this.discountAmount = discountAmount;
        }
        public BigDecimal getNetFee(){
            return netFee;
        }
        public void setNetFee(BigDecimal netFee){
            this.netFee= netFee;
        }
        public int getCourseDurationYears(){
            return courseDurationYears;
        }
        public void setCourseDurationYears(int courseDurationYears){
            this.courseDurationYears =  courseDurationYears;
        }
        public List<FeeInstallment> getInstallments() {
            return installments;
        }
        public void setInstallments(List<FeeInstallment> installments){
            this.installments = installments;
        }
}

