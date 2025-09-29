package com.example.collegefeeandstudentmanagement.dto;

import java.math.BigDecimal;
public class InstallmentDTO {
    private int installmentNumber;
    private BigDecimal amount;
    private boolean paid;

    public InstallmentDTO(){}

    public InstallmentDTO(int installmentNumber, BigDecimal amount, boolean paid){
        this.installmentNumber = installmentNumber;
        this.amount= amount;
        this.paid = paid;
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
        this.amount = amount;
    }
    public boolean isPaid(){
        return paid;
    }
    public void setPaid(boolean paid){
        this.paid = paid;
    }
}
