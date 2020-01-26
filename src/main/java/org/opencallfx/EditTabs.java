package org.opencallfx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EditTabs extends Application {

    private static String sectionPath;
    private static String value = "value";
    private String selected;


    private Config config = new Config();
    private static Ini ini = new Wini();
    private static Ini.Section section;
    private static List<String> valueList;

    private static VBox topVBox = new VBox();
    private HBox hBox = new HBox();
    private VBox inerVbox1 = new VBox();
    private VBox inerVbox2 = new VBox();
    private VBox inerVbox3 = new VBox();
    private static Label label = new Label();
    private static TextField inputField = new TextField();
    private Button add = new Button("->");
    private Button remove = new Button("<-");
    private static ObservableList<String> list = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(list);

    private VBox urlVbox = new VBox();
    private Label urlLabel = new Label("Edit URL Link");
    private static TextField urltextField = new TextField();
    private Button urlSave = new Button("Save");

    private Tab urlTab = new Tab("URL", urlVbox);
    private Tab lobTab = new Tab ("LOB", new VBox());
    private Tab woTypesTab = new Tab("WO Types", new VBox());
    private Tab ageBucketTab = new Tab("Age Bucket", new VBox());
    private Tab usTab = new Tab("United States", new VBox());
    private Tab canTab = new Tab("Canada", new VBox());
    private Tab austriaTab = new Tab("Austria", new VBox());
    private Tab belgiumTab = new Tab("Belgium", new VBox());
    private Tab franceTab = new Tab("France", new VBox());
    private Tab germanyTab = new Tab("Germany", new VBox());
    private Tab switzerlandTab = new Tab("Switzerland", new VBox());
    private Tab ukiTab = new Tab("UK&I", new VBox());
    private Tab euCustomers = new Tab("EU Customers", new VBox());

    private Tab spainBranch = new Tab("Branch", new VBox());
    private Tab spainTerritory = new Tab("Territory", new VBox());
    private Tab spainCsrCode = new Tab("SCR Code", new VBox());
    private TabPane spainPane = new TabPane();

    private Tab spainTab = new Tab("Spain", spainPane);

    private Tab apacCountries = new Tab("Countries", new VBox());
    private Tab apacBranch = new Tab("Branches", new VBox());
    private Tab apacCSR = new Tab("CSR Codes", new VBox());

    private TabPane settingsPane = new TabPane();
    private TabPane namerPane = new TabPane();
    private TabPane europePane = new TabPane();
    private TabPane apacPane = new TabPane();


    private Tab settingsTab = new Tab ("General Settings", settingsPane);
    private Tab namer = new Tab("NAMER", namerPane);
    private Tab europe = new Tab("Europe", europePane);
    private Tab apac = new Tab("APAC", apacPane);


    private TabPane root = new TabPane();
    private Scene scene = new Scene(root, 500, 380);


    public Scene getScene() {
        return scene;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

//ini file initialization
        config.setMultiOption(true);
        ini.setConfig(config);
        ini.load(new File("data.ini"));

// no close policy for TabPanes
        root.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        settingsPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        namerPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        europePane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        spainPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        apacPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

// URL Tab layout
        urlVbox.setAlignment(Pos.TOP_CENTER);
        urlVbox.setSpacing(20);
        urlVbox.setPadding(new Insets(10, 0, 10, 0));
        urlSave.setMinWidth(urlSave.getMaxWidth());
        urlVbox.getChildren().addAll(urlLabel, urltextField, urlSave);

// layout for rest of the tabs
        inputField.setMinWidth(100);
        add.setMinWidth(50);
        remove.setMinWidth(50);
        listView.setPrefSize(120, 250);

        inerVbox1.setSpacing(10);
        inerVbox1.setAlignment(Pos.CENTER);
        inerVbox1.getChildren().add(inputField);
        inerVbox2.setSpacing(10);
        inerVbox2.setAlignment(Pos.CENTER);
        inerVbox2.getChildren().addAll(add, remove);
        inerVbox3.setSpacing(10);
        inerVbox3.setAlignment(Pos.CENTER);
        inerVbox3.getChildren().add(listView);

        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(inerVbox1, inerVbox2, inerVbox3);

        topVBox.setSpacing(10);
        topVBox.setPadding(new Insets(10, 0, 10, 0));
        topVBox.setAlignment(Pos.TOP_CENTER);
        topVBox.getChildren().addAll(label, hBox);

        ((VBox) usTab.getContent()).getChildren().add(topVBox);

// TabPane arrangements and selection change handler set up for other tabs

        settingsPane.getTabs().addAll(urlTab, ageBucketTab, woTypesTab, lobTab);
        settingsTab.setOnSelectionChanged(urlHandler());
        urlTab.setOnSelectionChanged(urlHandler());
        ageBucketTab.setOnSelectionChanged(tabHandler(ageBucketTab, "age", "Calls Age Bucket we monitor"));
        woTypesTab.setOnSelectionChanged(tabHandler(woTypesTab, "wot", "Work Order types we monitor"));
        lobTab.setOnSelectionChanged(tabHandler(lobTab, "lob", "Calls Line Of Business we monitor"));

        namerPane.getTabs().addAll(usTab, canTab);
        namer.setOnSelectionChanged(tabHandler(usTab, "usa", "Master Customer Numbers to exclude from US Report"));
        usTab.setOnSelectionChanged(tabHandler(usTab, "usa", "Master Customer Numbers to exclude from US Report"));
        canTab.setOnSelectionChanged(tabHandler(canTab, "can", "Branches we monitor in Canada"));

        europePane.getTabs().addAll(austriaTab, belgiumTab, franceTab, germanyTab, switzerlandTab, spainTab, ukiTab, euCustomers);
        europe.setOnSelectionChanged(tabHandler(austriaTab, "aus", "Branches we monitor in Austria"));
        austriaTab.setOnSelectionChanged(tabHandler(austriaTab, "aus", "Branches we monitor in Austria"));
        belgiumTab.setOnSelectionChanged(tabHandler(belgiumTab, "bel", "Branches we monitor in Belgium"));
        franceTab.setOnSelectionChanged(tabHandler(franceTab, "fra", "Branches we monitor in France"));
        germanyTab.setOnSelectionChanged(tabHandler(germanyTab, "ger", "Branches we monitor in Germany"));
        switzerlandTab.setOnSelectionChanged(tabHandler(switzerlandTab, "swi", "Branches we monitor in Switzerland"));
        spainTab.setOnSelectionChanged(tabHandler(spainBranch, "spbr", "Branches we monitor in Spain"));
        ukiTab.setOnSelectionChanged(tabHandler(ukiTab, "uki", "Branches we monitor in UK&I"));
        euCustomers.setOnSelectionChanged(tabHandler(euCustomers, "euc", "Master Customer Numbers to exclude from EU Report"));

        spainPane.getTabs().addAll(spainBranch, spainTerritory, spainCsrCode);
        spainBranch.setOnSelectionChanged(tabHandler(spainBranch, "spbr", "Branches we monitor in Spain"));
        spainTerritory.setOnSelectionChanged(tabHandler(spainTerritory, "sptr", "Territories to exclude from Spain Report"));
        spainCsrCode.setOnSelectionChanged(tabHandler(spainCsrCode, "spcs", "CSR Codes to exclude from Spain Report"));

        apacPane.getTabs().addAll(apacCountries ,apacBranch, apacCSR);
        apac.setOnSelectionChanged(tabHandler(apacCountries, "apco", "Countries we monitor in APAC"));
        apacCountries.setOnSelectionChanged(tabHandler(apacCountries, "apco", "Countries we monitor in APAC"));
        apacBranch.setOnSelectionChanged(tabHandler(apacBranch, "apbr", "Branches to exclude from APAC Report"));
        apacCSR.setOnSelectionChanged(tabHandler(apacCSR, "apcs", "CSR Codes to exclude from APAC Report"));

// selection model for the list view, sets up the variable select used for adding and removing items from the observable list for ListView
        MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selected = newValue;
            }
        });

// Add/remove buttons handlers
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String input = inputField.getText();
                if (input.equals(null) || input.equals("")) {
                } else {
                    if (!list.contains(input)) {
                        list.add(input);
                    }
                }
                inputField.setText("");
                writeListToIni(list);
                refreshListFromIni();
            }
        });

        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(selected);
                writeListToIni(list);
                refreshListFromIni();
            }
        });

// url tab save button handler

        urlSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ini.put("url", value, urltextField.getText());
                try {
                    ini.store(new File("data.ini"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        root.setPadding(new Insets(10));
        root.getTabs().addAll(settingsTab, namer, europe, apac);
    }

//Statics methods for reading and writing list from and to ini file
    private static void writeListToIni(ObservableList<String> list) {
        section = (Profile.Section) ini.get(sectionPath);
        List<String> size = section.getAll(value);
        int num;
        if (size != null) {
            num = size.size();
        } else num = 0;
        for (int i = 0; i <= num; i++) {
            section.remove(value, 0);
        }
        try {
            ini.store(new File("data.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            section.add(value, list.get(i), i);
        }
        try {
            ini.store(new File("data.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void refreshListFromIni() {
        if (!list.isEmpty()) {
            list.removeAll(list);
        }
        section = (Profile.Section) ini.get(sectionPath);
        if (!section.isEmpty()) {
            valueList = section.getAll(value);
            for (String line : valueList) {
                list.add(line);
            }
        }
    }

// statics method that returns handler for tab selection
// adds layout to the tab(1argument)
//sets variable sectionPath used for determening the section in ini file to read/write (2nd argument)
// cleans TextField and sets text to label in the tab layout
    private static EventHandler<Event> tabHandler(Tab tab, String select, String labelText) {
        EventHandler<Event> handler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                inputField.setText("");
                sectionPath = select;
                label.setText(labelText);
                if (((VBox) topVBox.getParent()).getChildren() != null)
                    ((VBox) topVBox.getParent()).getChildren().removeAll(((VBox) topVBox.getParent()).getChildren());


                ((VBox) tab.getContent()).getChildren().add(topVBox);
                refreshListFromIni();
            }
        };
        return handler;
    }

// URL Tab selection handler

    private static EventHandler<Event> urlHandler () {
       EventHandler<Event> event = new EventHandler<Event>() {
           @Override
           public void handle(Event event) {
               sectionPath = "url";
               urltextField.setText(ini.get("url", value));
           }
       };
        return event;
    }


}
