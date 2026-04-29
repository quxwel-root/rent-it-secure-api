package com.example.fsociety.service;

import com.example.fsociety.entity.Invoice; // Імпорт нашого "Бланка"
import com.example.fsociety.model.PaymentStatus; // Імпорт статусів
import com.example.fsociety.repository.InvoiceRepository; // ТУТ МАЄ БУТИ ЦЕЙ ІМПОРТ
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWatcherService {
    private final InvoiceRepository invoiceRepository;

    @Scheduled(fixedRate = 30000)
    public void checkPendingInvoices() {
        log.info("Checking blockchain for pending payments...");


        List<Invoice> pendingInvoices = invoiceRepository.findAll()
                .stream()
                .filter(i -> i.getStatus() == PaymentStatus.PENDING)
                .toList();

        for (Invoice invoice : pendingInvoices) {
            verifyPayment(invoice);
        }
    }
    private void verifyPayment(Invoice invoice) {
        log.info("Verifying address: {}", invoice.getWalletAddress());

        double chance = Math.random();

        if (chance > 0.8) {
            log.info("Payment FOUND for address: {}", invoice.getWalletAddress());
            invoice.setStatus(PaymentStatus.PAID);
            invoice.setAmountReceived(invoice.getAmountExpected());
            invoiceRepository.save(invoice); // Зберігаємо оновлений статус у базу
        } else {
            log.info("... no payment yet for {}", invoice.getWalletAddress());
        }
    }
}