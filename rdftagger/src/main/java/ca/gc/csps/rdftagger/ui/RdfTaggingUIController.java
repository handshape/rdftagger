package ca.gc.csps.rdftagger.ui;

import ca.gc.csps.rdftagger.triplestore.JenaTripleStore;
import ca.gc.csps.rdftagger.triplestore.RDFTriple;
import ca.gc.csps.rdftagger.triplestore.RDFTripleException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jturner
 */
public class RdfTaggingUIController {

    @FXML
    private VBox rootContainer;

    @FXML
    private VBox objectButtonContainer;

    @FXML
    private ComboBox<String> subjectComboBox;

    @FXML
    private JFXButton previousButton;

    @FXML
    private JFXButton nextButton;

    @FXML
    private ScrollPane splitPane;

    @FXML
    private WebView webView;

    private JFXSnackbar snackBar = null;

    private final FileChooser sourceDataFileChooser = new FileChooser();
    private final FileChooser outputFileChooser = new FileChooser();

    private final TreeMap<String, JFXButton> objectButtonMap = new TreeMap<>();

    private NavigableSet<String> predicateSet;
    private Map<String, String> currentObjectPredicates = new HashMap<>();

    private JenaTripleStore model = new JenaTripleStore();

    @FXML
    void initialize() {
        // Set up the snackBar
        snackBar = new JFXSnackbar(rootContainer);
    }

    @FXML
    void loadObjectsAction(ActionEvent event) {
        sourceDataFileChooser.setTitle("Open Objects File");
        sourceDataFileChooser.getExtensionFilters().clear();
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Text file", "*.txt"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Comma-Separated Values", "*.csv"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Tab-Delimited Values", "*.tsv"));
        List<File> filesToOpen = sourceDataFileChooser.showOpenMultipleDialog(findWindow());
        if (filesToOpen != null) {
            loadObjectsFile(filesToOpen);
        }
    }

    public void loadObjectsFile(List<File> filesToOpen) {
        try {
            //TODO: do we want a sanity check here?
            TreeSet<String> loadedUris = loadUrisFromFiles(filesToOpen);
            loadObjectList(loadedUris);
            popSnackBar("Loaded " + loadedUris.size() + " objects.");
        } catch (IOException | URISyntaxException ex) {
            handleException(ex);
        }
    }

    @FXML
    void loadPredicatesAction(ActionEvent event) {
        sourceDataFileChooser.setTitle("Open Predicates File");
        sourceDataFileChooser.getExtensionFilters().clear();
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Text file", "*.txt"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Comma-Separated Values", "*.csv"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Tab-Delimited Values", "*.tsv"));
        List<File> filesToOpen = sourceDataFileChooser.showOpenMultipleDialog(findWindow());
        if (filesToOpen != null) {
            loadPredicatesFile(filesToOpen);
        }

    }

    public void loadPredicatesFile(List<File> filesToOpen) {
        try {
            //TODO: do we want a sanity check here?
            TreeSet<String> loadedUris = loadUrisFromFiles(filesToOpen);
            loadPredicateList(loadedUris);
            popSnackBar("Loaded " + loadedUris.size() + " predicates.");
        } catch (IOException | URISyntaxException ex) {
            handleException(ex);
        }
    }

    @FXML
    void loadSubjectsAction(ActionEvent event) {
        sourceDataFileChooser.setTitle("Open Subjects File");
        sourceDataFileChooser.getExtensionFilters().clear();
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("URI and alias files", "*.txt,*.csv*,.tsv"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Text file", "*.txt"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Comma-Separated Values", "*.csv"));
        sourceDataFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("UTF-8 Tab-Delimited Values", "*.tsv"));
        List<File> filesToOpen = sourceDataFileChooser.showOpenMultipleDialog(findWindow());
        if (filesToOpen != null) {
            loadSubjectsFile(filesToOpen);
        }
    }

    public void loadSubjectsFile(List<File> filesToOpen) {
        try {
            loadSubjectList(loadUrisFromFiles(filesToOpen));
        } catch (IOException | URISyntaxException ex) {
            handleException(ex);
        }
    }

    private TreeSet<String> loadUrisFromFiles(List<File> filesToOpen) throws IOException, URISyntaxException {
        TreeSet<String> toLoad = new TreeSet<>();
        for (File file : filesToOpen) {
            if (file.getName().endsWith(".csv")) {
                try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.EXCEL)) {
                    parseLinesAndAliasesFromCsv(parser, toLoad);
                }
            } else if (file.getName().endsWith(".tsv")) {
                try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.TDF)) {
                    parseLinesAndAliasesFromCsv(parser, toLoad);
                }
            } else {
                List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
                for (String line : lines) {
                    line = line.trim();
                    if (!line.startsWith("#") && !line.isEmpty()) {
                        URI uri = new URI(line);
                        toLoad.add(uri.toASCIIString());
                    }
                }
            }
        }
        return toLoad;
    }

    private void parseLinesAndAliasesFromCsv(final CSVParser parser, TreeSet<String> toLoad) throws URISyntaxException {
        for (CSVRecord record : parser) {
            String uriCell = trimAndDequote(record.get(0));
            if (!uriCell.startsWith("#") && !uriCell.isEmpty()) {
                URI uri = new URI(uriCell);
                toLoad.add(uri.toASCIIString());
                if (record.size() > 1) {
                    String alias = trimAndDequote(record.get(1));
                    if (!alias.isEmpty()) {
                        Aliases.putAlias(uri, alias);
                    }
                }
            }
        }
    }

    private String trimAndDequote(String uriCell) {
        uriCell = uriCell.trim();
        if (uriCell.charAt(0) == '\"' && uriCell.charAt(uriCell.length() - 1) == '\"') {
            uriCell = uriCell.substring(1, uriCell.length() - 2);
        }
        return uriCell;
    }

    @FXML
    void nextAction(ActionEvent event) {
        subjectComboBox.getSelectionModel().selectNext();
    }

    @FXML
    void previousAction(ActionEvent event) {
        subjectComboBox.getSelectionModel().selectPrevious();

    }

    @FXML
    void saveAction(ActionEvent event) {
        outputFileChooser.setTitle("Save Triples File");
        outputFileChooser.getExtensionFilters().clear();
        outputFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Turtle RDF", "*.ttl"));
        File saveFile = sourceDataFileChooser.showSaveDialog(findWindow());
        if (saveFile != null) {
            model.save(saveFile.toPath());
            popSnackBar("Saved to " + saveFile.getName());
        }
    }

    @FXML
    void subjectComboBoxAction(ActionEvent event) {
        loadSubject(subjectComboBox.getSelectionModel().getSelectedItem());
    }

    private void loadSubject(String subject) {
        webView.getEngine().load(subject);
        currentObjectPredicates.clear();
        try {
            Collection<RDFTriple> triples = model.get(new URI(subject), null, null);
            triples.forEach((RDFTriple triple) -> {
                currentObjectPredicates.put(String.valueOf(triple.getObjekt()), triple.getPredicate().toASCIIString());
            });

        } catch (URISyntaxException ex) {
            handleException(ex);
        }
        objectButtonMap.keySet().forEach(objectString -> {
            updateButton(objectString, currentObjectPredicates.get(objectString));
        });
    }

    private void loadSubjectList(Set<String> subjects) {
        subjectComboBox.getItems().clear();
        subjectComboBox.getItems().addAll(subjects);
        subjectComboBox.getSelectionModel().selectFirst();
        loadSubject(subjectComboBox.getSelectionModel().getSelectedItem());
    }

    private void loadPredicateList(Set<String> predicates) {
        TreeSet<String> predicateSet = new TreeSet<String>(predicates);
        this.predicateSet = predicateSet;
    }

    private void loadObjectList(Set<String> objects) {
        TreeSet<String> objectSet = new TreeSet<String>(objects);
        this.objectButtonMap.clear();
        objectButtonContainer.getChildren().clear();
        objectSet.stream().forEach(objectString -> {
            JFXButton objectButton = new JFXButton(Aliases.getAlias(objectString));
            objectButton.setMinHeight(50);
            objectButton.setMinWidth(100);
            objectButton.setPrefWidth(1000);
            objectButton.setTextAlignment(TextAlignment.LEFT);
            objectButton.setWrapText(true);
            objectButton.setButtonType(JFXButton.ButtonType.RAISED);
            objectButtonContainer.getChildren().add(objectButton);
            objectButtonMap.put(objectString, objectButton);
            objectButton.setOnAction((event) -> {
                togglePredicateOn(objectString);
            });
        });
    }

    private void togglePredicateOn(String objectString) {
        String newPredicate = null;
        String oldPredicate = currentObjectPredicates.get(objectString);
        if (oldPredicate == null) {
            newPredicate = predicateSet.first();
        } else {
            newPredicate = predicateSet.higher(oldPredicate);
        }
        setTriple(subjectComboBox.getSelectionModel().getSelectedItem(), newPredicate, objectString);
    }

    private void setTriple(String subject, String predicate, String objectString) {
        try {
            model.removeAll(new URI(subject), null, objectString);
            if (predicate != null) {
                model.put(new RDFTriple(new URI(subject), new URI(predicate), objectString));
            }
        } catch (URISyntaxException | RDFTripleException ex) {
            Logger.getLogger(RdfTaggingUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (subject.equals(subjectComboBox.getSelectionModel().getSelectedItem())) {
            // The change affects the displayed buttons.
            currentObjectPredicates.put(objectString, predicate);
            updateButton(objectString, predicate);
        }
    }

    private void updateButton(String objectString, String predicate) {
        JFXButton affectedButton = this.objectButtonMap.get(objectString);
        currentObjectPredicates.put(objectString, predicate);
        if (affectedButton != null) {
            if (predicate == null) {
                affectedButton.setText(Aliases.getAlias(objectString));
                affectedButton.setStyle("");
            } else {
                affectedButton.setText(Aliases.getAlias(predicate) + "\n" + Aliases.getAlias(objectString));
                affectedButton.setStyle("-fx-background-color:" + StringColourizer.hexColorForString(predicate));
            }
        }
    }

    private Window findWindow() {
        return this.rootContainer.getScene().getWindow();
    }

    private void handleException(Throwable ex) {
        Logger.getLogger(RdfTaggingUIController.class.getName()).log(Level.SEVERE, null, ex);
        alert(ex.getClass().getName(), ex.getMessage());
    }

    private void alert(String title, String message) {
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(title);
            alert.setHeaderText(message);
            alert.showAndWait();
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(title);
                alert.setHeaderText(message);
                alert.showAndWait();
            });
        }
    }

    public void popSnackBar(final String message) {
        Platform.runLater(() -> {
            JFXSnackbarLayout snackbarLayout = new JFXSnackbarLayout(message);
            snackbarLayout.setBackground(new Background(new BackgroundFill(new Color(0.8D, 0.8D, 0.8D, 0.5D), new CornerRadii(5D), Insets.EMPTY)));
            snackBar.enqueue(new JFXSnackbar.SnackbarEvent(snackbarLayout));
        });
    }
}
