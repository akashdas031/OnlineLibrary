package PaymentService.Services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import PaymentService.DTOs.PaymentRequestDTO;
import PaymentService.DTOs.PaymentResponseDTO;
import PaymentService.Entities.Payment;
import PaymentService.Repositories.PaymentRepository;

import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorPayService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    public RazorPayService(@Value("${razorpay.keyId}") String keyId,
                           @Value("${razorpay.keySecret}") String keySecret,
                           PaymentRepository paymentRepository) throws Exception {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponseDTO createOrder(PaymentRequestDTO request) throws Exception {
        // Prepare Razorpay Order Request
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", request.getAmount() * 100); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        // Create Razorpay Order
        Order order = razorpayClient.orders.create(orderRequest);

        // Save payment details in the database
        Payment payment = new Payment();
       String id= UUID.randomUUID().toString().substring(15).replace('-', ' ');
        payment.setId(id);
        payment.setTransactionId(order.get("receipt"));
        payment.setOrderId(order.get("id"));
        payment.setStatus("PENDING");
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        paymentRepository.save(payment);

        // Prepare Response
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId(order.get("id"));
        response.setPaymentLink("https://checkout.razorpay.com/v1/checkout.js?order_id=" + order.get("id"));
        response.setStatus("PENDING");

        return response;
    } 

    public void updatePayment(String paymentId, String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        payment.setPaymentId(paymentId);
        payment.setStatus(status);
        paymentRepository.save(payment);
    }
    /**
     * Captures a payment for a given payment ID and verifies it.
     */
    public PaymentResponseDTO capturePayment(String paymentId, double amount) throws Exception {
        // Capture the payment (amount in paise)
        JSONObject captureRequest = new JSONObject();
        captureRequest.put("amount", (int) (amount * 100));
        com.razorpay.Payment razorpayPayment = razorpayClient.payments.capture(paymentId, captureRequest);

        Payment capturedPayment = this.paymentRepository.findByPaymentId(paymentId).orElseThrow(()-> new RuntimeException("Invalid Payment Id..."));
        
        capturedPayment.setStatus("CAPTURED");
        this.paymentRepository.save(capturedPayment);
        
        // Fetch the payment details
        String status = razorpayPayment.get("status");
        String orderId = razorpayPayment.get("order_id");

        // Update the database
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        payment.setPaymentId(paymentId);
        payment.setStatus(status.toUpperCase());
        paymentRepository.save(payment);

        // Prepare Response
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId(orderId);
        response.setPaymentId(paymentId);
        response.setStatus(status.toUpperCase());

        return response;
    }
}
