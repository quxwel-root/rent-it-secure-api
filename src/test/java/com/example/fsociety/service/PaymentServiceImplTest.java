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
        // 1. Створюємо "манекен" бази даних. Вона нічого не зберігає по-справжньому.
        invoiceRepository = mock(InvoiceRepository.class);

        // ObjectMapper залишаємо справжнім, бо це просто парсер тексту в JSON
        objectMapper = new ObjectMapper();

        // Створюємо наш сервіс і підсовуємо йому манекен замість реальної БД
        paymentService = new PaymentServiceImpl(invoiceRepository, objectMapper);
    }

    @Test
    void processPayment_ShouldThrowExceptionAndSetUnderpaid_WhenAmountIsLow() {
        // --- 1. ARRANGE (Підготовка атаки) ---
        UUID testId = UUID.randomUUID();

        // Хакерський JSON: очікуємо 100, а він прислав 50
        String jsonPayload = "{\"invoice_id\": \"" + testId + "\", \"amount\": \"50\"}";

        Invoice fakeInvoice = new Invoice();
        fakeInvoice.setId(testId);
        fakeInvoice.setAmountExpected(new BigDecimal("100")); // Чекали 100
        fakeInvoice.setStatus(PaymentStatus.PENDING);

        // Навчаємо манекен: "Коли тебе попросять знайти цей ID, віддай fakeInvoice"
        when(invoiceRepository.findById(testId)).thenReturn(Optional.of(fakeInvoice));


        // --- 2. ACT & ASSERT (Б'ємо по системі і перевіряємо броню) ---

        // Перевіряємо, чи метод викинув PaymentException, як ми його вчили
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(jsonPayload);
        });

        // Перевіряємо, чи бекенд правильно зреагував на недоплату (змінив статус)
        assertEquals(PaymentStatus.UNDERPAID, fakeInvoice.getStatus());

        // Перевіряємо, чи викликався метод save() в базі рівно 1 раз, щоб зберегти цей статус
        verify(invoiceRepository, times(1)).save(fakeInvoice);

        // Додатково перевіримо, чи правильне повідомлення у винятку
        assertTrue(exception.getMessage().contains("Недоплата") || exception.getMessage().contains("менша за очікувану"));
    }
}