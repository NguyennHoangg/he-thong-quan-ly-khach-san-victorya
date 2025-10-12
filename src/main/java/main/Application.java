package main;

/**
 * Main launcher - only responsible for starting the chosen JavaFX Application.
 */
public class Application {
    public static void main(String[] args) {
        // Launch the Management JavaFX Application (TrangQuanLy)
        javafx.application.Application.launch(view.TrangQuanLy.class, args);
    }
}
