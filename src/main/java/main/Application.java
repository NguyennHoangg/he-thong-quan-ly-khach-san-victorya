package main;

/**
 * Main launcher - only responsible for starting the chosen JavaFX Application.
 */
public class Application {
    public static void main(String[] args) {
        // Launch the Management JavaFX Application (TrangQuanLy)
        // Changed from TrangDangNhap to TrangQuanLy so running the jar starts the management view.
        javafx.application.Application.launch(view.TrangDangNhap.class, args);
    }
}
