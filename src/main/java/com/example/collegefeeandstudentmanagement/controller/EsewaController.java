package com.example.collegefeeandstudentmanagement.controller;

import com.example.collegefeeandstudentmanagement.service.EsewaPaymentService;
import com.example.collegefeeandstudentmanagement.service.StudentFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.collegefeeandstudentmanagement.entity.Student;
import com.example.collegefeeandstudentmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/esewa")
public class EsewaController {
    private final EsewaPaymentService esewaPaymentService;
    private final StudentFeeService studentFeeService;
    private final StudentRepository studentRepository;

    public EsewaController(EsewaPaymentService esewaPaymentService, StudentFeeService studentFeeService,StudentRepository studentRepository){
        this.esewaPaymentService = esewaPaymentService;
        this.studentFeeService = studentFeeService;
        this.studentRepository = studentRepository;
    }
    @PostMapping("/pay/{studentId}/{installmentNumber}")
    public ResponseEntity<?> initiatePayment(@PathVariable Long studentId, @PathVariable int installmentNumber, Authentication auth){

        String username = auth.getName();
        Student loggedInStudent = studentRepository.findByEmail(username)
                .orElseThrow(()-> new RuntimeException("Logged-in student not found"));

        if(!loggedInStudent.getId().equals(studentId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: you are not allowed to pay other student's installments");
        }
        boolean alreadyPaid = studentFeeService.isInstallmentPaid(studentId, installmentNumber);
        if(alreadyPaid){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: you have already paid this installment");
        }
        double amount = studentFeeService.getInstallmentAmountByNumber(studentId, installmentNumber);
        String transactionId = "TXN-" + installmentNumber + "-" + studentId + "-" + System.currentTimeMillis();

        Map<String, String> payload = new HashMap<>();
        payload.put("psc", "0");
        payload.put("tAmt", String.valueOf(amount));
        payload.put("su", "http://localhost:8080/api/esewa/success");
        payload.put("scd", "EPAYTEST");
        payload.put("amt", String.valueOf(amount));
        payload.put("pid", transactionId);
        payload.put("pdc", "0");
        payload.put("txAmt", "0");
        payload.put("fu", "http://localhost:8080/api/esewa/failure");

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
