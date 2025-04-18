package PaymentService.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PaymentService.DTOs.PaymentRequestDTO;
import PaymentService.DTOs.PaymentResponseDTO;
import PaymentService.Entities.UpdatePayment;
import PaymentService.Services.RazorPayService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final RazorPayService razorpayService;

    public PaymentController(RazorPayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponseDTO> createOrder(@RequestBody PaymentRequestDTO request) throws Exception {
        return ResponseEntity.ok(razorpayService.createOrder(request));
    }

    @PostMapping("/update-status")
    public ResponseEntity<String> updatePayment(@RequestBody UpdatePayment updatePayment) {
        razorpayService.updatePayment(updatePayment);
        return ResponseEntity.ok("Payment status updated successfully.");
    }
    
    @GetMapping("/capturePayment")
    public ResponseEntity<PaymentResponseDTO> capturePayment(@RequestParam String paymentId, @RequestParam Double amount) throws Exception{
    	PaymentResponseDTO payment = this.razorpayService.capturePayment(paymentId, amount);
        return new ResponseEntity<PaymentResponseDTO>(payment,HttpStatus.OK);
    }
}
