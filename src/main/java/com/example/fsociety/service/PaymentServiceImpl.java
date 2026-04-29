package com.example.fsociety.service;

import com.example.fsociety.entity.Invoice;
import com.example.fsociety.exception.PaymentException;
import com.example.fsociety.model.PaymentStatus;
import com.example.fsociety.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final InvoiceRepository invoiceRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.payment.secret}")
    private String secretKey;

    @Override
    public boolean verifySignature(String payload, String signature) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(rawHmac).equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public void processPayment(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String invoiceId = jsonNode.get("invoice_id").asText();
            BigDecimal received = new BigDecimal(jsonNode.get("amount").asText());

            Invoice invoice = invoiceRepository.findById(UUID.fromString(invoiceId))
                    .orElseThrow(() -> new PaymentException("Інвойс не знайдено"));
            if (invoice.getStatus() == PaymentStatus.PAID) {
                System.out.println(" DOUBLE SPEND ATTEMPT");
                return;
            }
            if (received.compareTo(invoice.getAmountExpected()) < 0) {
                invoice.setStatus(PaymentStatus.UNDERPAID);
                invoice.setAmountReceived(received);
                invoiceRepository.save(invoice);
                throw new PaymentException("Недоплата");
            }

            invoice.setAmountReceived(received);
            invoice.setStatus(PaymentStatus.PAID);
            invoiceRepository.save(invoice);

            if (false) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.postForObject("", new HashMap<>(), String.class);
            }
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentException("Помилка процесингу");
        }
    }
    @Override
    public Invoice createInvoice(BigDecimal amount, String address) {
        return invoiceRepository.save(Invoice.builder()
                .amountExpected(amount)
                .amountReceived(BigDecimal.ZERO)
                .walletAddress(address)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build());
    }
}