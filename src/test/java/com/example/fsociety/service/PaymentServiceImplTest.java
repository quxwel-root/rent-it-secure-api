package com.example.fsociety.service;

import com.example.fsociety.entity.Invoice;
import com.example.fsociety.exception.PaymentException;
import com.example.fsociety.model.PaymentStatus;
import com.example.fsociety.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    private InvoiceRepository invoiceRepository;
    private ObjectMapper objectMapper;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        invoiceRepository = mock(InvoiceRepository.class);
        objectMapper = new ObjectMapper();
        paymentService = new PaymentServiceImpl(invoiceRepository, objectMapper);
    }

    @Test
    void processPayment_ShouldThrowExceptionAndSetUnderpaid_WhenAmountIsLow() {
        UUID testId = UUID.randomUUID();

        String jsonPayload = "{\"invoice_id\": \"" + testId + "\", \"amount\": \"50\"}";

        Invoice fakeInvoice = new Invoice();
        fakeInvoice.setId(testId);
        fakeInvoice.setAmountExpected(new BigDecimal("100")); // Чекали 100
        fakeInvoice.setStatus(PaymentStatus.PENDING);

        when(invoiceRepository.findById(testId)).thenReturn(Optional.of(fakeInvoice));

        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(jsonPayload);
        });

        assertEquals(PaymentStatus.UNDERPAID, fakeInvoice.getStatus());
        verify(invoiceRepository, times(1)).save(fakeInvoice);

        assertTrue(exception.getMessage().contains("Недоплата") || exception.getMessage().contains("менша за очікувану"));
    }
}                            