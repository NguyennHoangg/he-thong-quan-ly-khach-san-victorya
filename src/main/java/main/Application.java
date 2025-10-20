package main;

/**
 * Main launcher - only responsible for starting the chosen JavaFX Application.
 */
public class Application {
    public static void main(String[] args) {
        // Launch MoMo Business Payment Demo (Với API - Tự động xác nhận)
        javafx.application.Application.launch(payment.controller.MoMoPaymentDemo.class, args);
    }
}
