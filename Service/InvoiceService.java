package com.freelancer.Service;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.freelancer.Entity.Invoice;
import com.freelancer.Repository.InvoiceRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepository;
	
	private RazorpayClient razorpayClient;
	
	@Value("${razorpay.key.id}")
	private String razorpayId;
	
	@Value("${razorpay.key_secret}")
	private String razorpaySecret;
	
	@PostConstruct
	public void init() throws RazorpayException {
		this.razorpayClient = new RazorpayClient(razorpayId, razorpaySecret);
	}
	//use project as formal parameters
	/*
	 * public Invoice createInvoice(Invoice invoice) throws RazorpayException {
	 * JSONObject json = new JSONObject(); //here add parameters to this json object
	 * json.put("amount", invoice.getProjectAmount() * 100); json.put("currency",
	 * "INR"); json.put("receipt", invoice.getUniqueID());
	 * 
	 * 
	 * Order razorpayOrder = razorpayClient.orders.create(json);
	 * invoice.setRazorPayOrderId(razorpayOrder.get("id"));
	 * invoice.setBillStatus(razorpayOrder.get("status")); return
	 * invoiceRepository.save(invoice);
	 * 
	 * }
	 */
	
	public Invoice createInvoice(Invoice invoice) throws RazorpayException {
	    System.out.println("Received invoice: " + invoice.toString());  // Debugging Log

	    

	    JSONObject json = new JSONObject();
	    json.put("amount", invoice.getProjectAmount()*100); // Convert to paise
	    json.put("currency", "INR");
	    json.put("receipt", invoice.getFreelancerName()); // Ensure this is a valid string

	    System.out.println("Creating Razorpay Order with JSON: " + json.toString());

	    Order razorpayOrder = razorpayClient.orders.create(json);
	    
	    System.out.println("Order Created Successfully: " + razorpayOrder.toString());

	    invoice.setRazorPayOrderId(razorpayOrder.get("id"));
	    invoice.setBillStatus(razorpayOrder.get("status"));

	    return invoiceRepository.save(invoice);
	}

	
	
	
	
}
