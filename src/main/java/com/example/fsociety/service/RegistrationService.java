package com.example.fsociety.service;

import com.example.fsociety.entity.Invoice;
import com.example.fsociety.entity.User;
import com.example.fsociety.model.PaymentStatus;
import com.example.fsociety.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;

    @Transactional
    public void registerUserWithInitialPayment(String username, BigDecimal amount) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");

        Invoice invoice = new Invoice();
        invoice.setAmountExpected(amount);
        invoice.setStatus(PaymentStatus.PENDING);
        invoice.setExternalId(UUID.randomUUID().toString());

        user.addInvoice(invoice);

        userRepository.save(user);
    }
}