package org.opencallfx;

import javafx.application.Application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class App extends Application {

    private GridPane root = new GridPane();

    private Label titleLabel = new Label("Open Calls Report Downloader");
    private Label regionLabel = new Label("Region");
    private Label countryLabel = new Label("Country");
    private Label branchLabel = new Label("Branch");
    private Label statusLabel = new Label();
    private Label errorLabel = new Label();

    private RadioButton usa = new RadioButton("USA");
    private RadioButton can = new RadioButton("Canada");
    private ToggleGroup namer = new ToggleGroup();

    private CheckBox us106 = new CheckBox("US106");
    private CheckBox us113 = new CheckBox("US113");
    private CheckBox us133 = new CheckBox("US133");
    private CheckBox us143 = new CheckBox("US143");
    private CheckBox us153 = new CheckBox("US153");
    private CheckBox us163 = new CheckBox("US163");

    private CheckBox uki = new CheckBox("United Kingdom");
    private CheckBox france = new CheckBox("France");
    private CheckBox germany = new CheckBox("Germany");
    private CheckBox austria = new CheckBox("Austria");
    private CheckBox switzerland = new CheckBox("Switzerland");
    private CheckBox belgium = new CheckBox("Belgium");
    private CheckBox spain = new CheckBox("Spain");

    private Button download = new Button("Download");
    private Button save = new Button("Save");
    private Button settings = new Button("Setings");

    private ProgressBar progressBar = new ProgressBar();

    private VBox regionsListBox = vBox();
    private VBox countriesBox = vBox();
    private VBox branchesBox = vBox();
    private VBox buttonBox = vButtonBox();
    private HBox titleBox = labelBox();
    private HBox regionTitleBox = labelBox();
    private HBox countriesTitleBox = labelBox();
    private HBox branchesTitleBox = labelBox();
    private HBox statusBox = labelBox();
    private HBox graphBox = graphBox();

    private ObservableList<String> regionsList = FXCollections.observableArrayList("N. America", "Europe", "APAC");
    private ListView<String> regionList = new ListView<>(regionsList);
    private MultipleSelectionModel multipleSM = regionList.getSelectionModel();


    private XSSFWorkbook workbookOut = null;
    private String regionToFilter;
    private Ini ini = new Ini();
    private static ArrayList<String> countriesToFilter = new ArrayList<>();
    private static ArrayList<String> branchesToFilter = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) {
//initializing DownloadAndFilter (report), EditTabs and BarChart classes, new Stage object for EditTabs scene
        DownloadAndFilter report = new DownloadAndFilter();
        CallsBarChart chart = new CallsBarChart();
        EditTabs tabs = new EditTabs();

        Stage stage = new Stage();
        try { tabs.start(stage); } catch (Exception e) {}

//Grid pane layout adjustment, horizontal constraints for even distribution of columns, adding "static" nodes to predefined VBoxes
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setGridLinesVisible(false);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(25);
            root.getColumnConstraints().add(columnConstraints);
        }

        titleBox.getChildren().add(titleLabel);
        regionTitleBox.getChildren().add(regionLabel);
        countriesTitleBox.getChildren().add(countryLabel);
        branchesTitleBox.getChildren().add(branchLabel);

        regionsListBox.getChildren().add(regionList);

        statusBox.getChildren().add(statusLabel);
        buttonBox.getChildren().addAll(download, save, settings);

/* statusLabel, errorLabel fontm wrap. Buttons disable property and width. While the report task is running, buttons are disabled
 fail safe against user error for queuing up multiple tasks ------------------------------------------------------------*/
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 20));

        errorLabel.setPrefSize(errorLabel.getMaxWidth(), errorLabel.getMaxHeight());
        errorLabel.setWrapText(true);
        errorLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));

        download.disableProperty().bind(report.runningProperty());
        save.disableProperty().bind(report.runningProperty());
        settings.disableProperty().bind(report.runningProperty());

        download.setMinWidth(120);
        save.setMinWidth(120);
        settings.setMinWidth(120);

/*regions list selection model, removes nodes from subsequent Vboxes (countriesBox, BranchesBox)
   resets countries checkboxes and radios to false (Resets the countries list) -----------------------------------------*/
        multipleSM.selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                countriesBox.getChildren().removeAll(countriesBox.getChildren());
                branchesBox.getChildren().removeAll(countriesBox.getChildren());

                usa.setSelected(false);
                can.setSelected(false);

                austria.setSelected(false);
                belgium.setSelected(false);
                france.setSelected(false);
                germany.setSelected(false);
                spain.setSelected(false);
                switzerland.setSelected(false);
                uki.setSelected(false);

                us106.setSelected(false);
                us113.setSelected(false);
                us133.setSelected(false);
                us143.setSelected(false);
                us153.setSelected(false);
                us163.setSelected(false);

                if (newValue == "N. America") {
                    countriesBox.getChildren().addAll(usa, can);
                    regionToFilter = "WCS - North America";
                } else if (newValue == "Europe") {
                    countriesBox.getChildren().addAll(austria, belgium, france, germany, spain, switzerland, uki);
                    regionToFilter = "WCS - Europe";
                } else {
                    regionToFilter = "WCS - Asia Pacific";
                }
            }
        });

//countries radio buttons ToggleGroup-----------------------------------------------------------------------------------
        usa.setToggleGroup(namer);
        can.setToggleGroup(namer);
/* NA countries radio buttons listeners, removes nodes from subsequent Vbox (branchesBox)
    resets all branches checkboxes to false (Resets the branches list)--------------------------------------------------*/
        usa.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    branchesBox.getChildren().removeAll(branchesBox.getChildren());
                    countriesToFilter.add(usa.getText());
                    branchesBox.getChildren().addAll(us106, us113, us133, us143, us153, us163);
                } else {
                    if (countriesToFilter.contains(usa.getText())) {
                        countriesToFilter.remove(usa.getText());
                    }
                    branchesBox.getChildren().removeAll(branchesBox.getChildren());
                    us106.setSelected(false);
                    us113.setSelected(false);
                    us133.setSelected(false);
                    us143.setSelected(false);
                    us153.setSelected(false);
                    us163.setSelected(false);
                }
            }
        });
        can.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    countriesToFilter.add(can.getText());
                } else {
                    if (countriesToFilter.contains(can.getText())) {
                        countriesToFilter.remove(can.getText());
                    }
                }
            }
        });

// branches checkBox listeners -----------------------------------------------------------------------------------------
        us106.selectedProperty().addListener(branchesCheckListener(us106));
        us113.selectedProperty().addListener(branchesCheckListener(us113));
        us133.selectedProperty().addListener(branchesCheckListener(us133));
        us143.selectedProperty().addListener(branchesCheckListener(us143));
        us153.selectedProperty().addListener(branchesCheckListener(us153));
        us163.selectedProperty().addListener(branchesCheckListener(us163));

// countries checkBox listeners ----------------------------------------------------------------------------------------
        austria.selectedProperty().addListener(countriesChangeListener(austria));
        belgium.selectedProperty().addListener(countriesChangeListener(belgium));
        france.selectedProperty().addListener(countriesChangeListener(france));
        germany.selectedProperty().addListener(countriesChangeListener(germany));
        spain.selectedProperty().addListener(countriesChangeListener(spain));
        switzerland.selectedProperty().addListener(countriesChangeListener(switzerland));
        uki.selectedProperty().addListener(countriesChangeListener(uki));

/* Download button handler and logic for statusLabel label. Logic also prevents empty lists in reports setter argument,
 for cases where lists are required. Sets the parameters for DownloadAndFilter class and starts the service -----------*/
        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (regionToFilter == null) {
                    statusLabel.setText("Please select a region");
                } else {
                    switch (regionToFilter) {
                        case ("WCS - North America"): {
                            if (countriesToFilter.isEmpty()) statusLabel.setText("Please select a country");
                            else {
                                if (countriesToFilter.get(0) == "USA") {
                                    if (branchesToFilter.isEmpty()) statusLabel.setText("Please select at least one branch");
                                    else {

                                        // call for download US
                                        report.setter("WCS - North America", countriesToFilter, branchesToFilter);
                                        report.start();

                                    }
                                } else if (countriesToFilter.get(0) == "Canada") {
                                    statusLabel.setText("Downloading and filtering report");
                                    // call for download Canada
                                    report.setter("WCS - North America", countriesToFilter, branchesToFilter);
                                    report.start();

                                }
                            }
                            break;
                        }
                        case ("WCS - Europe"): {
                            if (countriesToFilter.isEmpty()) statusLabel.setText("Please select at least one country");
                            else {
                                statusLabel.setText("Downloading and filtering report");
                                //call for download europe
                                report.setter("WCS - Europe", countriesToFilter, branchesToFilter);
                                report.start();
                            }
                            break;
                        }
                        case ("WCS - Asia Pacific"): {
                            statusLabel.setText("Downloading and filtering report");
                            // call for download APAC
                            report.setter("WCS - Asia Pacific", countriesToFilter, branchesToFilter);
                        }
                        break;
                    }
                }
            }
        });

/* Save button handler, initiate FileChooser object, sets extension to .xlsx, sets initial directory fetched from ini section "save",
          saves the workbook variable via FileOutputStream---------------------------------------------------------------------------*/
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (workbookOut == null) {
                    statusLabel.setText("Please download the report");
                } else {
                    try {
                        ini.load(new File("data.ini"));
                        FileChooser chooser = new FileChooser();
                        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx"));
                        String dir = ini.get("save", "value");
                        if (dir != null) {
                            File dirFile = new File(dir);
                            if (dirFile.canRead()) {
                                chooser.setInitialDirectory(dirFile);
                            }
                        }

                        File file = chooser.showSaveDialog(primaryStage);
                        if (file != null) {
                            ini.put("save", "value", file.getParent());
                            ini.store(new File("data.ini"));
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            workbookOut.write(fileOutputStream);
                            workbookOut.close();
                            fileOutputStream.close();
                        }
                        statusLabel.setText("Saved");

                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        });

//settings button handler. Creates a new stage with scene fetched from EditTabs class-----------------------------------
        settings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                stage.setMinHeight(440);
                stage.setMinWidth(520);

                stage.setTitle("Settings");
                stage.setResizable(false);
                stage.setScene(tabs.getScene());
                stage.show();
            }
        });


// Services handlers :
    /* report service handlers, removes nodes from graphBox and adds progress bar while the service is running
        on success, fetches the workbook from DownloadAndFilter class and assigns it to the workbook variable for saving,
        starts the CallsBarChart services and sets the workbook for it
        on fail, adds label with string exception trace from the service------------------------------------------------*/
        report.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                workbookOut = report.getValue();
                chart.setWorkbook(workbookOut);
                chart.start();
                graphBox.getChildren().removeAll(graphBox.getChildren());
                statusLabel.setText("Completed :)");
            }
        });
        report.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                graphBox.getChildren().removeAll(graphBox.getChildren());
                graphBox.getChildren().add(progressBar);
                statusLabel.setText("Downloading and filtering report");
            }
        });
        report.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                graphBox.getChildren().removeAll(graphBox.getChildren());
                statusLabel.setText("Error, Unable to download/filter report");
                errorLabel.setText(report.getException().toString());
                graphBox.getChildren().add(errorLabel);
                System.out.println(report.getException().toString());
            }
        });

    //chart handlers, removes the nodes from graph box and adds chart fetched from the CallsBarChart class on success---
        chart.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                graphBox.getChildren().removeAll(graphBox.getChildren());
                graphBox.getChildren().add(chart.getValue());
            }
        });
        chart.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                graphBox.getChildren().removeAll(graphBox.getChildren());

                statusLabel.setText("Unable to generate calls chart");
                errorLabel.setText(chart.getException().toString());
                graphBox.getChildren().add(errorLabel);
            }
        });

        root.add(titleBox, 0, 0, 4, 1);
        root.add(regionTitleBox, 0, 1);
        root.add(countriesTitleBox, 1, 1);
        root.add(branchesTitleBox, 2, 1);
        root.add(regionsListBox, 0, 2);
        root.add(countriesBox, 1, 2);
        root.add(branchesBox, 2, 2);
        root.add(buttonBox, 3, 2);
        root.add(statusBox, 0, 3, 4, 1);
        root.add(graphBox, 0, 4, 4, 1);

        primaryStage.setTitle("Global Command Center");
        primaryStage.setMinWidth(570);
        primaryStage.setMinHeight(570);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 550, 550));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }


    public static ChangeListener<Boolean> branchesCheckListener(CheckBox box) {
        ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) branchesToFilter.add(box.getText());
                else {
                    if (branchesToFilter.contains(box.getText())) {
                        branchesToFilter.remove(box.getText());
                    }
                }
            }
        };
        return listener;
    }

    public static ChangeListener<Boolean> countriesChangeListener(CheckBox box) {
        ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) countriesToFilter.add(box.getText());
                else {
                    if (countriesToFilter.contains(box.getText())) {
                        countriesToFilter.remove(box.getText());
                    }
                }
            }
        };
        return listener;
    }

    public VBox vBox() {
        VBox vBox = new VBox();
        vBox.setMinHeight(150);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setSpacing(5);
        return vBox;
    }

    public VBox vButtonBox() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.setSpacing(20);
        return vBox;
    }

    public HBox labelBox() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        return hBox;
    }

    public static HBox graphBox () {
        HBox hBox = new HBox();
        hBox.setMinHeight(300);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

}