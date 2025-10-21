package main;

/**
 * Main launcher - only responsible for starting the chosen JavaFX Application.
 */
public class Application {
    public static void main(String[] args) {
        // Enable splash screen
        System.setProperty("javafx.preloader", "view.SplashScreen");
        
        // Launch application
        javafx.application.Application.launch(view.TrangDangNhap.class, args);
    }
}
