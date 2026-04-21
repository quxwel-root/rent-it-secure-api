package com.example.fsociety.model;

public enum PaymentStatus {
    PENDING,    // Очікуємо оплату
    PAID,       // Успішно оплачено повністю
    UNDERPAID,  // Оплачено частково (не вистачає)
    EXPIRED     // Час вийшов, оплата не надійшла
}
