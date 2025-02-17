package com.freelancer.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freelancer.Entity.Invoice;


public interface InvoiceRepository extends JpaRepository<Invoice, Long>{

}
