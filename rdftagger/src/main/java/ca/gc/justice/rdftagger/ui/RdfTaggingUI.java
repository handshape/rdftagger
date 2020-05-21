package ca.gc.justice.rdftagger.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author jturner
 */
public class RdfTaggingUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // Happens before start()
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("RdfTaggingUI.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Justice RDF Tagger");
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(new Image(getClass().getResource("brain.png").toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.show();
        RdfTaggingUIController controller = loader.getController();
//        Platform.runLater(() -> {
//            controller.setEvaluator(evaluator);
//        });
        Parameters cliParms = this.getParameters();
        if (!cliParms.getUnnamed().isEmpty()) {
            // Load the moel requested.
        }
    }

    @Override
    public void stop() throws Exception {
    }

}
