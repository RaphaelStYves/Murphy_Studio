package application;

import Controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/main_layout.fxml"));
        Parent root = fxmlLoader.load();

        Rectangle2D bounds = Screen.getPrimary().getBounds();


        MainController mainController = fxmlLoader.getController();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(895);
        primaryStage.setMinHeight(root.minHeight(-1));
        primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) / 4);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> mainController.exit());

        //TODO charger les vues suivantes :
        // mainContainer -> split pane
        // split pane top -> track_layout.fxml
        // track_layout_vbox -> track.fxml
        // split pane bottom -> chords.fxml (apres avoir retire le surperflu)

    }


    public static void main(String[] args) {
        launch(args);
    }
}
