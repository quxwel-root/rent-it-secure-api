package com.example.fsociety.service;


import com.example.fsociety.entity.Invoice;
import java.math.BigDecimal;

public interface PaymentService {
    boolean verifySignature(String payload, String signature);
    void processPayment(String payload);
    Invoice createInvoice(BigDecimal amount, String address);
}