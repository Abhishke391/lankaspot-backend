package com.lankaspot.server.repository;

import com.lankaspot.server.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByPaymentRegistry_Id(Long registryId);
    Optional<Invoice> findByOrderIdAndAdvertiserId(String orderId, UUID advertiserId);
    Optional<Invoice> findByInvoiceNumberAndAdvertiserId(String invoiceNumber, UUID advertiserId);
    List<Invoice> findByAdvertiserIdOrderByIssuedAtDesc(UUID advertiserId);
}
