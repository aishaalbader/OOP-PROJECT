// رغد
package com.mycompany.chargedparticlesimulatorv;



import com.mycompany.chargedparticlesimulator.gui.SimulationView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        SimulationView view = new SimulationView();

        Scene scene = new Scene(view, 1000, 1000);

        stage.setTitle("Charged Particle Simulator");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(900);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
