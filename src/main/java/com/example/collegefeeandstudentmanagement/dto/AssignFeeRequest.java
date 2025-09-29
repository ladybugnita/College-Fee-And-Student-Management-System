package com.example.collegefeeandstudentmanagement.dto;
import java.math.BigDecimal;
public class AssignFeeRequest {
    private BigDecimal totalFee;
    private BigDecimal scholarship;
    private BigDecimal discount;
    private Integer years;

    public BigDecimal getTotalFee(){
        return totalFee;
    }
    public void setTotalFee(BigDecimal totalFee){
        this.totalFee = totalFee;
    }
    public BigDecimal getScholarship(){
        return scholarship;
    }
    public void setScholarship(BigDecimal scholarship){
        this.scholarship= scholarship;
    }
    public BigDecimal getDiscount(){
        return discount;
    }
    public void setDiscount(BigDecimal discount){
        this.discount = discount;
    }
    public Integer getYears(){
        return years;
    }
    public void setYears(Integer years){
        this.years = years;
    }
}
