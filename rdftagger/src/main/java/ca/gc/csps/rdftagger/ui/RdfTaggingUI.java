package ca.gc.csps.rdftagger.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.application.Platform;
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
        primaryStage.setTitle("RDF Tagger");
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(new Image(getClass().getResource("triple.png").toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.show();
        RdfTaggingUIController controller = loader.getController();
        Platform.runLater(() -> {
            Parameters cliParms = this.getParameters();
            if (!cliParms.getUnnamed().isEmpty()) {
                controller.loadSubjectsFile(Arrays.asList(new File[]{new File(cliParms.getUnnamed().get(0))}));
                if (cliParms.getUnnamed().size() > 1) {
                    controller.loadPredicatesFile(Arrays.asList(new File[]{new File(cliParms.getUnnamed().get(1))}));
                }
                if (cliParms.getUnnamed().size() > 2) {
                    controller.loadObjectsFile(Arrays.asList(new File[]{new File(cliParms.getUnnamed().get(2))}));
                }
            }
        });
    }

    @Override
    public void stop() throws Exception {
    }

}
