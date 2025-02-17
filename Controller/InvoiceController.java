package com.freelancer.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.freelancer.Entity.Invoice;
import com.freelancer.Entity.Project;
import com.freelancer.Service.InvoiceService;
import com.freelancer.Service.ProjectService;
import com.razorpay.RazorpayException;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class InvoiceController {
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private ProjectService projectService;

	@GetMapping("/initPayment")//@RequestParam String param
	public String invoicePage () {
		return "payment";
	}
	//check what can be done in produces
	@PostMapping(value = "/createOrder", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Invoice> createOrder  (@RequestBody Invoice invoice) throws RazorpayException {
	 
		Invoice razorpayOrder = invoiceService.createInvoice(invoice);
		
		return new ResponseEntity<Invoice>(razorpayOrder, HttpStatus.CREATED);
	}
	
//	@PostMapping("/paymentSuccess")
//	@ResponseBody
//	public ResponseEntity<String> paymentSuccess(@RequestBody Map<String, Object> data) {
//	    try {
//	        System.out.println("Payment Success Data: " + data);
//
//	        // Extract necessary details from webhook payload
//	        Map<String, Object> paymentPayload = (Map<String, Object>) ((Map<String, Object>) data.get("payload")).get("payment");
//	        Map<String, Object> entity = (Map<String, Object>) paymentPayload.get("entity");
//
//	        long projectId = Long.parseLong(entity.get("order_id").toString().split("_")[1]); // Extract projectId from orderId
//
//	        // Fetch the project by ID
//	        Project project = projectService.findById(projectId);
//	        if (project == null) {
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
//	        }
//
//	        // Update payment and project status
//	        project.setPaymentStatus("PAYMENT_COMPLETED");
//	        project.setProjectStatus("DELIVERED_SUCCESSFULLY");
//
//	        // Save updated project
//	        projectService.save(project);
//	        
//	        return ResponseEntity.ok("Payment verified successfully and project status updated");
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment success");
//	    }
//	}
	@PostMapping("/paymentSuccess")
	@ResponseBody
	public ResponseEntity<String> paymentSuccess(@RequestBody Map<String, Object> data) {
	    try {
	        System.out.println("Payment Success Data: " + data);

	        // Extract Razorpay payment details
	        Map<String, Object> paymentPayload = (Map<String, Object>) ((Map<String, Object>) data.get("payload")).get("payment");
	        Map<String, Object> entity = (Map<String, Object>) paymentPayload.get("entity");

	        // ✅ Get projectId from Razorpay "notes"
	        Map<String, Object> notes = (Map<String, Object>) entity.get("notes");
	        if (notes == null || !notes.containsKey("projectId")) {
	            return ResponseEntity.badRequest().body("Project ID missing in Razorpay order notes");
	        }

	        long projectId = Long.parseLong(notes.get("projectId").toString());

	        // Fetch project from DB
	        Project project = projectService.findById(projectId);
	        if (project == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
	        }

	        // ✅ Update payment status
	        project.setPaymentStatus("PAYMENT_COMPLETED");
	        project.setProjectStatus("DELIVERED_SUCCESSFULLY");
	        projectService.save(project);

	        return ResponseEntity.ok("Payment verified successfully and project status updated");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment success: " + e.getMessage());
	    }
	}


	
	
	
}
