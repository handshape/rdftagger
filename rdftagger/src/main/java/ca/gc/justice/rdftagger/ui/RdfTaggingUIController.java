package ca.gc.justice.rdftagger.ui;

import ca.gc.justice.rdftagger.triplestore.JenaTripleStore;
import ca.gc.justice.rdftagger.triplestore.RDFTriple;
import ca.gc.justice.rdftagger.triplestore.RDFTripleException;
import com.jfoenix.controls.JFXButton;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static String NULL_PREDICATE = "http://justice.gc.ca/ext/predicate/0-null";

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

    private final TreeMap<String, JFXButton> objectButtonMap = new TreeMap<>();

    private NavigableSet<String> predicateSet;
    private Map<String, String> currentObjectPredicates = new HashMap<>();

    private JenaTripleStore model = new JenaTripleStore();

    @FXML
    void loadObjectsAction(ActionEvent event) {
        loadObjectList(new TreeSet<String>(Arrays.asList(
                "http://justice.gc.ca/ext/object/naics/12345",
                "http://justice.gc.ca/ext/object/naics/54321",
                "http://justice.gc.ca/ext/object/naics/31415"
        )));
    }

    @FXML
    void loadPredicatesAction(ActionEvent event) {
        //TODO: Pop a file chooser and load the list of predicates.
        loadPredicateList(new TreeSet<String>(Arrays.asList(
                "http://justice.gc.ca/ext/relationship/applies-to",
                "http://justice.gc.ca/ext/relationship/not-applies-to"
        )));
    }

    @FXML
    void loadSubjectsAction(ActionEvent event) {
        //TODO: Pop a file chooser and load the list of subjects into the combobox.
        loadSubjectList(new TreeSet<String>(Arrays.asList(
                "https://laws-lois.justice.gc.ca/eng/regulations/SOR-86-304/",
                "https://laws-lois.justice.gc.ca/eng/acts/A-1/",
                "https://laws-lois.justice.gc.ca/eng/acts/C-46/"
        )));
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
        model.save(Paths.get("./testsave.ttl"));
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
            //TODO: Pop something when an exception like this happens.
            Logger.getLogger(RdfTaggingUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TODO: load whatever the set of states in the triple store are for the subject.
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
        predicateSet.add(NULL_PREDICATE);
        this.predicateSet = predicateSet;
    }

    private void loadObjectList(Set<String> objects) {
        TreeSet<String> objectSet = new TreeSet<String>(objects);
        this.objectButtonMap.clear();
        objectButtonContainer.getChildren().clear();
        objectSet.stream().forEach(objectString -> {
            JFXButton objectButton = new JFXButton(objectString);
            objectButton.setPrefHeight(50);
            objectButton.setMinWidth(300);
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
            oldPredicate = NULL_PREDICATE;
        }
        newPredicate = predicateSet.higher(oldPredicate);
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
        //TODO: Clear matching subject/object declarations in the store, and put the new triple in the triple store. 
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
            if (predicate == null || predicate.equals(NULL_PREDICATE)) {
                affectedButton.setText(Aliases.getAlias(objectString));
                affectedButton.setStyle("");
            } else {
                affectedButton.setText(Aliases.getAlias(predicate) + "\n" + Aliases.getAlias(objectString));
                affectedButton.setStyle("-fx-background-color:" + StringColourizer.hexColorForString(predicate));
            }
        }
    }
}
