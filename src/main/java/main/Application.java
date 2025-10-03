package main;

/**
 * Main launcher - only responsible for starting the chosen JavaFX Application.
 */
public class Application {
    public static void main(String[] args) {
        // Launch the LoginFrame JavaFX Application
        javafx.application.Application.launch(view.LoginFrame.class, args);
    }
}
