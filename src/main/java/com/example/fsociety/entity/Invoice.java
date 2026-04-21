package com.example.fsociety.entity;

import com.example.fsociety.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal amountExpected;

    @Column(nullable = false, unique = true)
    private String walletAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private BigDecimal amountReceived;

    private String externalId;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id")
   private User user;


    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public BigDecimal getAmountExpected() {
        return this.amountExpected;
    }

    public String getWalletAddress() {
        return this.walletAddress;
    }

    }

