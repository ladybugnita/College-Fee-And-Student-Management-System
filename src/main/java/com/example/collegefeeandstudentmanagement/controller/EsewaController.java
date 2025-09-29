package com.example.collegefeeandstudentmanagement.controller;

import com.example.collegefeeandstudentmanagement.service.EsewaPaymentService;
import com.example.collegefeeandstudentmanagement.service.StudentFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/esewa")
public class EsewaController {
    private final EsewaPaymentService esewaPaymentService;
    private final StudentFeeService studentFeeService;

    public EsewaController(EsewaPaymentService esewaPaymentService, StudentFeeService studentFeeService){
        this.esewaPaymentService = esewaPaymentService;
        this.studentFeeService = studentFeeService;
    }
    @PostMapping("/pay/{studentId}/{installmentNumber}")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long studentId, @PathVariable int installmentNumber){

        double amount = studentFeeService.getInstallmentAmountByNumber(studentId, installmentNumber);
        String transactionId = "TXN-"+ installmentNumber + "-" + studentId + "-" + System.currentTimeMillis();
        Map<String, String> payload = esewaPaymentService.initiatePayment(transactionId, amount);
        return ResponseEntity.ok(payload);
    }
    @GetMapping("/success")
    public ResponseEntity<String> successCallback(@RequestParam("oid") String transactionId,
                                                  @RequestParam("amt") double amount,
                                                  @RequestParam("refId") String refId,
                                                  @RequestParam("productId") String productId,
                                                  @RequestParam("studentId") Long studentId,
                                                  @RequestParam("installmentNumber") int installmentNumber){
        boolean verified = esewaPaymentService.verifyPayment(transactionId, amount, refId,productId);
        if(verified) {
            studentFeeService.markInstallmentPaidByNumber(studentId, installmentNumber);
            return ResponseEntity.ok("Payment successful and installment marked as paid!");
        } else {
            return ResponseEntity.badRequest().body("Payment verification failed.");
        }
    }
    @GetMapping("/failure")
    public ResponseEntity<String> failureCallback(){
        return ResponseEntity.badRequest().body("Payment failed or cancelled.");
    }
}
