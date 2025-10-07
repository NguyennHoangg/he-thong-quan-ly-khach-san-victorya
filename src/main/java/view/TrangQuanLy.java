package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TrangQuanLy extends Application{
   @Override
    public void start(Stage arg0) throws Exception {
        final double LEFT_W = 985;
        final double RIGHT_W = 920;
        final double PANEL_H = 950;
        final double OVERLAY_SIZE = 200;

        StackPane base = new StackPane();
        base.setStyle("-fx-backgruond-color: #f8f9fa");
        StackPane.setAlignment(base, Pos.CENTER);
    }

    public StackPane createSideBar(){
        StackPane sideBar = new StackPane();
        return sideBar;
    }

    
}

    
