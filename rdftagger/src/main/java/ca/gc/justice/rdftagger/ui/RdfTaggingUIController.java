package ca.gc.justice.rdftagger.ui;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 *
 * @author jturner
 */
public class RdfTaggingUIController {

    @FXML
    private VBox rootContainer;

    @FXML
    private ComboBox<?> subjectComboBox;

    @FXML
    private JFXButton previousButton;

    @FXML
    private JFXButton nextButton;

    @FXML
    private ScrollPane splitPane;

    @FXML
    private WebView webView;

    @FXML
    void loadObjectsAction(ActionEvent event) {

    }

    @FXML
    void loadPredicatesAction(ActionEvent event) {

    }

    @FXML
    void loadSubjectsAction(ActionEvent event) {

    }

    @FXML
    void nextAction(ActionEvent event) {

    }

    @FXML
    void previousAction(ActionEvent event) {

    }

    @FXML
    void saveAction(ActionEvent event) {

    }

    @FXML
    void subjectComboBoxAction(ActionEvent event) {

    }

}
