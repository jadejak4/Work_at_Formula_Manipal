package fm.telemetrysoftware;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.hansolo.medusa.skins.*; //Import the jar file of Medusa for gauges.
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jssc.*;
import static jssc.SerialPort.MASK_RXCHAR;
import eu.hansolo.medusa.Gauge;
import java.io.*;

public class Controller extends Application {

    BorderPane layout = new BorderPane(); //Creating a BorderPane as the base pane, on top of which different panes have been placed.

    Button button1 = new Button("Gauges");
    Button button2 = new Button("Line Charts");
    Button button3 = new Button("Extra");

    final int NUM_OF_POINT = 75; //Set the limit of points visible in the line chart at once.
    XYChart.Series series1; //Construct the 1st Series for the 1st line chart.
    XYChart.Series series2; //Construct the 2nd Series for the 2nd line chart.
    XYChart.Series series3;
    XYChart.Series series4;

    Gauge gauge1 = new Gauge(); //Initialize all the gauges.
    Gauge gauge2 = new Gauge();
    Gauge gauge3 = new Gauge();
    Gauge gauge4 = new Gauge();
    Gauge gauge5 = new Gauge();
    Gauge gauge6 = new Gauge();
    Gauge gauge7 = new Gauge();
    Gauge gauge8 = new Gauge();
    Gauge gauge9 = new Gauge();
    Gauge gauge10 = new Gauge();
    Gauge gauge11 = new Gauge();
    Gauge gauge12 = new Gauge();
    Gauge gauge13 = new Gauge();
    Gauge gauge14 = new Gauge();

    Menu File = new Menu("File"); //Creating menu bar ports.
    Menu Save = new Menu("Save");
    Menu Tools = new Menu("Tools");
    Menu Ports = new Menu("Ports");

    SerialPort XBeePort = null; //Initializing the serial port of XBee, and setting it null.
    ObservableList<String> portList; //Initialing the port list.

    private void detectPort() { //Method to detect all serial ports connected.
        portList = FXCollections.observableArrayList();
        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException { //Main method.

        detectPort();
        final ComboBox comboBoxPorts = new ComboBox(portList); //Creating a new ComboBox to display the serial ports.
        comboBoxPorts.setPromptText("Available Ports"); //Adding a prompt text to the ComboBox.
        comboBoxPorts.valueProperty().addListener( //Code to check for active serial ports.
                (ChangeListener<String>) (observable, oldValue, newValue) -> {
                    System.out.println(newValue);
                    disconnectXBee();
                    connectXBee(newValue);
                }
        );

        final NumberAxis xAxis1 = new NumberAxis(); //Initializing the x axis of the first line chart.
        final NumberAxis yAxis1 = new NumberAxis(); //Initializing the y axis of the first line chart.
        final NumberAxis xAxis2 = new NumberAxis(); //Initializing the x axis of the second line chart.
        final NumberAxis yAxis2 = new NumberAxis(); //Initializing the y axis of the second line chart.
        final NumberAxis xAxis3 = new NumberAxis();
        final NumberAxis yAxis3 = new NumberAxis();
        final NumberAxis xAxis4 = new NumberAxis();
        final NumberAxis yAxis4 = new NumberAxis();
        xAxis1.setLabel("Time"); //Setting the name of the x axis of the fist line chart.
        yAxis1.setLabel("RPM"); //Setting the name of the y axis of the fist line chart.
        xAxis2.setLabel("Time"); //Setting the name of the x axis of the second line chart.
        yAxis2.setLabel("OPS"); //Setting the name of the y axis of the second line chart.
        xAxis3.setLabel("Time");
        yAxis3.setLabel("TPS");
        xAxis4.setLabel("Time");
        yAxis4.setLabel("Voltage");
        xAxis1.setTickLabelFill(Color.WHITE);
        xAxis2.setTickLabelFill(Color.WHITE);
        xAxis3.setTickLabelFill(Color.WHITE);
        xAxis4.setTickLabelFill(Color.WHITE);
        yAxis1.setTickLabelFill(Color.WHITE);
        yAxis2.setTickLabelFill(Color.WHITE);
        yAxis3.setTickLabelFill(Color.WHITE);
        yAxis4.setTickLabelFill(Color.WHITE);
        final LineChart<Number,Number> lineChart1 = new LineChart<>(xAxis1,yAxis1); //Initializing the first line chart.
        final LineChart<Number,Number> lineChart2 = new LineChart<>(xAxis2,yAxis2); //Initializing the second line chart.
        final LineChart<Number,Number> lineChart3 = new LineChart<>(xAxis3,yAxis3);
        final LineChart<Number,Number> lineChart4 = new LineChart<>(xAxis4,yAxis4);
        series1 = new XYChart.Series(); //Initialing series for first line chart.
        series1.setName("RPM"); //Naming the series for the first line chart as RPM.

//        Platform.runLater(() -> {
//            Set<Node> nodes = lineChart1.lookupAll(".series" + 0);
//            for (Node n : nodes) {
//                n.setStyle("-fx-background-color: black, white;\n" + "-fx-background-insets: 0, 2;\n" + "-fx-background-radius: 5px;\n" + "-fx-padding: 5px;");
//            }
//            series1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;");
//        });

        series2 = new XYChart.Series(); //Initialing series for second line chart.
        series2.setName("OPS"); //Naming the series for the second line chart as OPS.
        series3 = new XYChart.Series();
        series3.setName("TPS");
        series4 = new XYChart.Series();
        series4.setName("Battery Voltage");
        lineChart1.getData().add(series1); //Adding series1 to the first line chart.
        lineChart2.getData().add(series2); //Adding series2 to the second line chart.
        lineChart3.getData().add(series3);
        lineChart4.getData().add(series4);
        lineChart1.setAnimated(false); //Disabling animation for the first line chart.
        lineChart2.setAnimated(false); //Disabling animation for the second line chart.
        lineChart3.setAnimated(false);
        lineChart4.setAnimated(false);
        lineChart1.setCreateSymbols(false); //Hiding data point symbols in the first line chart.
        lineChart2.setCreateSymbols(false); //Hiding data point symbols in the second line chart.
        lineChart3.setCreateSymbols(false);
        lineChart4.setCreateSymbols(false);


        for(int i=0; i<NUM_OF_POINT; i++) {
            series1.getData().add(new XYChart.Data(i, 0)); //Preload the first line chart with 0.
            series2.getData().add(new XYChart.Data(i, 0)); //Preload the second line chart with 0.
            series3.getData().add(new XYChart.Data(i, 0));
            series4.getData().add(new XYChart.Data(i, 0));
        }

        File.getItems().add(Save); //Adding 'Save' under 'File' in the menu bar.
        Tools.getItems().add(Ports); //Adding 'Ports' under 'Tools' in the menu bar.

        Screen screen = Screen.getPrimary(); //Creating a screen.
        Rectangle2D bounds = screen.getVisualBounds(); //Getting dimensions of the user's screen.

        gauge1.setTitle("RPM"); //Gauge 1 settings
        gauge1.setMinValue(0.00);
        gauge1.setMaxValue(1023.00);
        gauge1.setTickLabelsVisible(false);
        gauge1.setNeedleColor(Color.WHITE);
        gauge1.setUnitColor(Color.WHITE);
        gauge1.setValueColor(Color.WHITE);
        gauge1.setForegroundBaseColor(Color.WHITE);

        gauge2.setTitle("OPS"); //Gauge 2 settings
        gauge2.setMinValue(0.00);
        gauge2.setMaxValue(1023.00);
        gauge2.setSkin(new LinearSkin(gauge2));
        gauge2.setOrientation(Orientation.VERTICAL);
        gauge2.setNeedleColor(Color.WHITE);
        gauge2.setUnitColor(Color.WHITE);
        gauge2.setValueColor(Color.WHITE);
        gauge2.setForegroundBaseColor(Color.WHITE);

        gauge3.setTitle("ECT 1"); //Gauge 3 settings
        gauge3.setMinValue(0.00);
        gauge3.setMaxValue(1023.00);
        gauge3.setSkin(new LinearSkin(gauge3));
        gauge3.setOrientation(Orientation.VERTICAL);
        gauge3.setNeedleColor(Color.WHITE);
        gauge3.setUnitColor(Color.WHITE);
        gauge3.setValueColor(Color.WHITE);
        gauge3.setForegroundBaseColor(Color.WHITE);

        gauge4.setTitle("ECT 2"); //Gauge 4 settings
        gauge4.setMinValue(0.00);
        gauge4.setMaxValue(1023.00);
        gauge4.setSkin(new LinearSkin(gauge4));
        gauge4.setOrientation(Orientation.VERTICAL);
        gauge4.setNeedleColor(Color.WHITE);
        gauge4.setUnitColor(Color.WHITE);
        gauge4.setValueColor(Color.WHITE);
        gauge4.setForegroundBaseColor(Color.WHITE);

        gauge5.setTitle("WS FR"); //Gauge 5 settings
        gauge5.setMinValue(0.00);
        gauge5.setMaxValue(1023.00);
        gauge5.setTickLabelsVisible(false);
        gauge5.setNeedleColor(Color.WHITE);
        gauge5.setUnitColor(Color.WHITE);
        gauge5.setValueColor(Color.WHITE);
        gauge5.setForegroundBaseColor(Color.WHITE);

        gauge6.setTitle("WS FL"); //Gauge 6 settings
        gauge6.setMinValue(0.00);
        gauge6.setMaxValue(1023.00);
        gauge6.setTickLabelsVisible(false);
        gauge6.setNeedleColor(Color.WHITE);
        gauge6.setUnitColor(Color.WHITE);
        gauge6.setValueColor(Color.WHITE);
        gauge6.setForegroundBaseColor(Color.WHITE);

        gauge7.setTitle("WS RR"); //Gauge 7 settings
        gauge7.setMinValue(0.00);
        gauge7.setMaxValue(1023.00);
        gauge7.setTickLabelsVisible(false);
        gauge7.setNeedleColor(Color.WHITE);
        gauge7.setUnitColor(Color.WHITE);
        gauge7.setValueColor(Color.WHITE);
        gauge7.setForegroundBaseColor(Color.WHITE);

        gauge8.setTitle("WS RL"); //Gauge 8 settings
        gauge8.setMinValue(0.00);
        gauge8.setMaxValue(1023.00);
        gauge8.setTickLabelsVisible(false);
        gauge8.setNeedleColor(Color.WHITE);
        gauge8.setUnitColor(Color.WHITE);
        gauge8.setValueColor(Color.WHITE);
        gauge8.setForegroundBaseColor(Color.WHITE);

        gauge9.setTitle("TPS"); //Gauge 9 settings
        gauge9.setMinValue(0.00);
        gauge9.setMaxValue(1023.00);
        gauge9.setSkin(new LinearSkin(gauge9));
        gauge9.setOrientation(Orientation.VERTICAL);
        gauge9.setNeedleColor(Color.WHITE);
        gauge9.setUnitColor(Color.WHITE);
        gauge9.setValueColor(Color.WHITE);
        gauge9.setForegroundBaseColor(Color.WHITE);

        gauge10.setTitle("VOLTAGE"); //Gauge 10 settings
        gauge10.setMinValue(0.00);
        gauge10.setMaxValue(1023.00);
        gauge10.setSkin(new LinearSkin(gauge10));
        gauge10.setOrientation(Orientation.VERTICAL);
        gauge10.setNeedleColor(Color.WHITE);
        gauge10.setUnitColor(Color.WHITE);
        gauge10.setValueColor(Color.WHITE);
        gauge10.setForegroundBaseColor(Color.WHITE);

        gauge11.setTitle("FP"); //Gauge 11 settings
        gauge11.setMinValue(0.00);
        gauge11.setMaxValue(1023.00);
        gauge11.setSkin(new LinearSkin(gauge11));
        gauge11.setOrientation(Orientation.VERTICAL);
        gauge11.setNeedleColor(Color.WHITE);
        gauge11.setUnitColor(Color.WHITE);
        gauge11.setValueColor(Color.WHITE);
        gauge11.setForegroundBaseColor(Color.WHITE);

        gauge12.setTitle("BPS"); //Gauge 12 settings
        gauge12.setMinValue(0.00);
        gauge12.setMaxValue(1023.00);
        gauge12.setSkin(new LinearSkin(gauge12));
        gauge12.setOrientation(Orientation.VERTICAL);
        gauge12.setNeedleColor(Color.WHITE);
        gauge12.setUnitColor(Color.WHITE);
        gauge12.setValueColor(Color.WHITE);
        gauge12.setForegroundBaseColor(Color.WHITE);

        gauge13.setTitle("EXTRA"); //Gauge 13 settings
        gauge13.setMinValue(0.00);
        gauge13.setMaxValue(1023.00);
        gauge13.setTickLabelsVisible(false);
        gauge13.setSkin(new LinearSkin(gauge13));
        gauge13.setOrientation(Orientation.VERTICAL);
        gauge13.setNeedleColor(Color.WHITE);
        gauge13.setUnitColor(Color.WHITE);
        gauge13.setValueColor(Color.WHITE);
        gauge13.setForegroundBaseColor(Color.WHITE);

        gauge14.setTitle("EXTRA"); //Gauge 14 settings
        gauge14.setMinValue(0.00);
        gauge14.setMaxValue(1023.00);
        gauge14.setTickLabelsVisible(false);
        gauge14.setNeedleColor(Color.WHITE);
        gauge14.setUnitColor(Color.WHITE);
        gauge14.setValueColor(Color.WHITE);
        gauge14.setForegroundBaseColor(Color.WHITE);

        GridPane gridpane1 = new GridPane(); //Creating a GridPane for the gauges.
        gridpane1.setPrefHeight(bounds.getHeight());
        //gridpane1.setGridLinesVisible(true);
        gridpane1.setBackground(new Background(new BackgroundFill(Color.valueOf("#1c1d1f"), CornerRadii.EMPTY, Insets.EMPTY))); //Set the background colour as dark grey.

        GridPane gridpane2 = new GridPane(); //Creating a GridPane for the line charts.
        //gridpane2.setGridLinesVisible(true);
        gridpane2.setBackground(new Background(new BackgroundFill(Color.valueOf("#1c1d1f"), CornerRadii.EMPTY, Insets.EMPTY))); //Set the background colour as dark grey.

        GridPane buttonsbar = new GridPane();
        buttonsbar.setPrefHeight(bounds.getHeight()/25);
        buttonsbar.setBackground(new Background(new BackgroundFill(Color.valueOf("#1c1d1f"), CornerRadii.EMPTY, Insets.EMPTY)));

        GridPane gridpane3 = new GridPane();
        buttonsbar.setBackground(new Background(new BackgroundFill(Color.valueOf("#1c1d1f"), CornerRadii.EMPTY, Insets.EMPTY)));
        ColumnConstraints column0 = new ColumnConstraints(bounds.getWidth()/10);
        column0.setHgrow(Priority.ALWAYS);
        ColumnConstraints column1 = new ColumnConstraints(bounds.getWidth()/10);
        column1.setHgrow(Priority.ALWAYS);
        ColumnConstraints column2 = new ColumnConstraints(bounds.getWidth()/10);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(bounds.getWidth()/10);
        column3.setHgrow(Priority.ALWAYS);
        ColumnConstraints column4 = new ColumnConstraints(bounds.getWidth()/10);
        column4.setHgrow(Priority.ALWAYS);
        ColumnConstraints column5 = new ColumnConstraints(bounds.getWidth()/10);
        column5.setHgrow(Priority.ALWAYS);
        ColumnConstraints column6 = new ColumnConstraints(bounds.getWidth()/10);
        column6.setHgrow(Priority.ALWAYS);
        ColumnConstraints column7 = new ColumnConstraints(bounds.getWidth()/10);
        column7.setHgrow(Priority.ALWAYS);
        ColumnConstraints column8 = new ColumnConstraints(bounds.getWidth()/10);
        column8.setHgrow(Priority.ALWAYS);
        ColumnConstraints column9 = new ColumnConstraints(bounds.getWidth()/10);
        column9.setHgrow(Priority.ALWAYS);

        RowConstraints row0 = new RowConstraints(bounds.getHeight()*0.43);
        row0.setVgrow(Priority.ALWAYS);
        RowConstraints row1 = new RowConstraints(bounds.getHeight()*0.43);
        row1.setVgrow(Priority.ALWAYS);
//        RowConstraints row2 = new RowConstraints(bounds.getHeight()/2);
//        row2.setVgrow(Priority.ALWAYS);

        gridpane1.getRowConstraints().addAll(row0, row1);
        gridpane1.getColumnConstraints().addAll(column0, column1, column2, column3, column4, column5, column6, column7, column8, column9);
        gridpane1.add(gauge9, 0, 0);
        gridpane1.add(gauge12, 1, 0);
        gridpane1.add(gauge5, 2, 0, 2, 1);
        gridpane1.add(gauge6, 4, 0, 2, 1);
        gridpane1.add(gauge7, 6, 0, 2, 1);
        gridpane1.add(gauge8, 8, 0, 2, 1);
        gridpane1.add(gauge3, 0, 1);
        gridpane1.add(gauge4, 1, 1);
        gridpane1.add(gauge13, 2, 1);
        gridpane1.add(gauge11, 3, 1);
        gridpane1.add(gauge2, 4, 1);
        gridpane1.add(gauge10, 5, 1);
        gridpane1.add(gauge1, 6, 1, 2, 1);
        gridpane1.add(gauge14, 8, 1, 2, 1);

        RowConstraints row00 = new RowConstraints(bounds.getHeight()*0.22); //Rows for the line chart GridPane.
        row00.setVgrow(Priority.ALWAYS);
        RowConstraints row1i = new RowConstraints(bounds.getHeight()*0.22);
        row1i.setVgrow(Priority.ALWAYS);
        RowConstraints row2i = new RowConstraints(bounds.getHeight()*0.22);
        row2i.setVgrow(Priority.ALWAYS);
        RowConstraints row3i = new RowConstraints(bounds.getHeight()*0.22);
        row3i.setVgrow(Priority.ALWAYS);
        ColumnConstraints column00 = new ColumnConstraints(bounds.getWidth()); //Columns for the line chart GridPane.
        column00.setHgrow(Priority.ALWAYS);

        ColumnConstraints columnb0 = new ColumnConstraints(bounds.getWidth()/7); //Columns for the buttonsbar GridPane.
        columnb0.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnb1 = new ColumnConstraints(bounds.getWidth()/7);
        columnb1.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnb2 = new ColumnConstraints(bounds.getWidth()/7);
        columnb2.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnb3 = new ColumnConstraints(bounds.getWidth()/7);
        columnb3.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnb4 = new ColumnConstraints(bounds.getWidth()/7);
        columnb4.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnb5 = new ColumnConstraints(bounds.getWidth()/7);
        columnb5.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnb6 = new ColumnConstraints(bounds.getWidth()/7);
        columnb6.setHgrow(Priority.ALWAYS);

        gridpane2.getColumnConstraints().addAll(column00);
        gridpane2.getRowConstraints().addAll(row00, row1i, row2i, row3i);
        gridpane2.add(lineChart1, 0, 0);
        gridpane2.add(lineChart2, 0, 1);
        gridpane2.add(lineChart3, 0, 2);
        gridpane2.add(lineChart4, 0, 3);

        buttonsbar.getColumnConstraints().addAll(columnb0, columnb1, columnb2, columnb3, columnb4, columnb5, columnb6);
        buttonsbar.add(comboBoxPorts,0,0);
        buttonsbar.add(button1,2,0);
        buttonsbar.add(button2,3,0);
        buttonsbar.add(button3,4,0);
        buttonsbar.setHalignment(comboBoxPorts, HPos.CENTER);
        buttonsbar.setHalignment(button1, HPos.RIGHT);
        buttonsbar.setHalignment(button2, HPos.CENTER);
        buttonsbar.setHalignment(button3, HPos.LEFT);

       // FileInputStream imageStream = new FileInputStream("/Users/MahicShah/Library/CloudStorage/OneDrive-ManipalAcademyofHigherEducation/Formula Manipal/Software/JavaFX_Arduino_Live/Logo.png");
        //Image image = new Image(imageStream);
        //ImageView imageView = new ImageView(image);
//        imageView.setX(170);
//        imageView.setY(10);
//        imageView.setFitWidth(200);
//        imageView.setPreserveRatio(true);
//        buttonsbar.add(imageView, 6, 0);
//        buttonsbar.setHalignment(imageView, HPos.RIGHT);

        //FileInputStream imageStream1 = new FileInputStream("/Users/MahicShah/Library/CloudStorage/OneDrive-ManipalAcademyofHigherEducation/Formula Manipal/Software/JavaFX_Arduino_Live/Transperant logo.png");
        //Image image1 = new Image(imageStream1);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(File, Tools);

        layout.setTop(buttonsbar);
        layout.setCenter(gridpane1);
        //layout.setBottom(dummy);

        Scene scene = new Scene(layout);

        button1.setOnAction(e -> layout.setCenter(gridpane1));
        button2.setOnAction(e -> layout.setCenter(gridpane2));
        button3.setOnAction(e -> layout.setCenter(gridpane3));

        primaryStage.setTitle("Genesis");
       // primaryStage.getIcons().add(image1);
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setScene(scene);
        //primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void shiftSeriesData1(float newValue) {
        for(int i=0; i<NUM_OF_POINT-1; i++){
            XYChart.Data<String, Number> ShiftDataUp = (XYChart.Data<String, Number>) series1.getData().get(i + 1);
            Number shiftValue = ShiftDataUp.getYValue();
            XYChart.Data<String, Number> ShiftDataDn = (XYChart.Data<String, Number>)series1.getData().get(i);
            ShiftDataDn.setYValue(shiftValue);
        }
        XYChart.Data<String, Number> lastData = (XYChart.Data<String, Number>)series1.getData().get(NUM_OF_POINT-1);
        lastData.setYValue(newValue);
    }

    public void shiftSeriesData2(float newValue2) {
        for(int i=0; i<NUM_OF_POINT-1; i++){
            XYChart.Data<String, Number> ShiftDataUp = (XYChart.Data<String, Number>)series2.getData().get(i+1);
            Number shiftValue = ShiftDataUp.getYValue();
            XYChart.Data<String, Number> ShiftDataDn = (XYChart.Data<String, Number>)series2.getData().get(i);
            ShiftDataDn.setYValue(shiftValue);
        }
        XYChart.Data<String, Number> lastData = (XYChart.Data<String, Number>)series2.getData().get(NUM_OF_POINT-1);
        lastData.setYValue(newValue2);
    }

    public void shiftSeriesData3(float newValue3) {
        for(int i=0; i<NUM_OF_POINT-1; i++){
            XYChart.Data<String, Number> ShiftDataUp = (XYChart.Data<String, Number>)series3.getData().get(i+1);
            Number shiftValue = ShiftDataUp.getYValue();
            XYChart.Data<String, Number> ShiftDataDn = (XYChart.Data<String, Number>)series3.getData().get(i);
            ShiftDataDn.setYValue(shiftValue);
        }
        XYChart.Data<String, Number> lastData = (XYChart.Data<String, Number>)series3.getData().get(NUM_OF_POINT-1);
        lastData.setYValue(newValue3);
    }
    public void shiftSeriesData4(float newValue4) {
        for(int i=0; i<NUM_OF_POINT-1; i++){
            XYChart.Data<String, Number> ShiftDataUp = (XYChart.Data<String, Number>)series4.getData().get(i+1);
            Number shiftValue = ShiftDataUp.getYValue();
            XYChart.Data<String, Number> ShiftDataDn = (XYChart.Data<String, Number>)series4.getData().get(i);
            ShiftDataDn.setYValue(shiftValue);
        }
        XYChart.Data<String, Number> lastData = (XYChart.Data<String, Number>)series4.getData().get(NUM_OF_POINT-1);
        lastData.setYValue(newValue4);
    }

    public void connectXBee(String port){
        System.out.println("XBee is connected.");
        SerialPort serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setEventsMask(MASK_RXCHAR);
            serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
                if (serialPortEvent.isRXCHAR()) {
                    try {
                            byte[] l;

                            while (serialPort.isOpened()) {
                                l = serialPort.readBytes(11);
                                int x = l[0] & 0xFF;
                                int o = l[1] & 0xFF;
                                if (x == 5 & o == 240 || x == 5 & o == 241 || x == 5 & o == 242) {
                                    break;
                                }
                            }

                            byte[] b = serialPort.readBytes(11);

                            int y = b[1] & 0xFF;
                            int id = ((5 * 256) + y);

                            if (id == 1520) {
                                int rpm_1 = b[4] & 0xFF;
                                int rpm_2 = b[3] & 0xFF;
                                int rpm_total = ((rpm_1 * 256) + rpm_2);
                                Platform.runLater(() -> gauge1.setValue(rpm_total));

                                int ops_1 = b[6] & 0xFF;
                                int ops_2 = b[5] & 0xFF;
                                int ops_total = ((ops_1 * 256) + ops_2);
                                Platform.runLater(() -> gauge2.setValue(ops_total));

                                int ect1_1 = b[8] & 0xFF;
                                int ect1_2 = b[7] & 0xFF;
                                int ect1_total = ((ect1_1 * 256) + ect1_2);
                                Platform.runLater(() -> gauge3.setValue(ect1_total));

                                int ect2_1 = b[10] & 0xFF;
                                int ect2_2 = b[9] & 0xFF;
                                int ect2_total = ((ect2_1 * 256) + ect2_2);
                                Platform.runLater(() -> gauge4.setValue(ect2_total));

                                Platform.runLater(() -> {
                                    shiftSeriesData1((float) rpm_total);
                                    shiftSeriesData2((float) ops_total);
                                });
                            }

                            if (id == 1521) {
                                int wsfr_1 = b[4] & 0xFF;
                                int wsfr_2 = b[3] & 0xFF;
                                int wsfr_total = ((wsfr_1 * 256) + wsfr_2);
                                Platform.runLater(() -> gauge5.setValue(wsfr_total));

                                int wsfl_1 = b[6] & 0xFF;
                                int wsfl_2 = b[5] & 0xFF;
                                int wsfl_total = ((wsfl_1 * 256) + wsfl_2);
                                Platform.runLater(() -> gauge6.setValue(wsfl_total));

                                int wsrr_1 = b[8] & 0xFF;
                                int wsrr_2 = b[7] & 0xFF;
                                int wsrr_total = ((wsrr_1 * 256) + wsrr_2);
                                Platform.runLater(() -> gauge7.setValue(wsrr_total));

                                int wsrl_1 = b[10] & 0xFF;
                                int wsrl_2 = b[9] & 0xFF;
                                int wsrl_total = ((wsrl_1 * 256) + wsrl_2);
                                Platform.runLater(() -> gauge8.setValue(wsrl_total));
                            }

                            if (id == 1522) {
                                int tps_1 = b[4] & 0xFF;
                                int tps_2 = b[3] & 0xFF;
                                int tps_total = ((tps_1 * 256) + tps_2);
                                Platform.runLater(() -> gauge9.setValue(tps_total));

                                int vol_1 = b[6] & 0xFF;
                                int vol_2 = b[5] & 0xFF;
                                int vol_total = ((vol_1 * 256) + vol_2);
                                Platform.runLater(() -> gauge10.setValue(vol_total));

                                int fp_1 = b[8] & 0xFF;
                                int fp_2 = b[7] & 0xFF;
                                int fp_total = ((fp_1 * 256) + fp_2);
                                Platform.runLater(() -> gauge11.setValue(fp_total));

                                Platform.runLater(() -> {
                                    shiftSeriesData3((float) tps_total);
                                    shiftSeriesData4((float) vol_total);
                                });
                            }

                            System.out.println("b[0] = " + (b[0] & 0xFF));
                            System.out.println("b[1] = " + (b[1] & 0xFF));
                            System.out.println("b[2] = " + (b[2] & 0xFF));
                            System.out.println("b[3] = " + (b[3] & 0xFF));
                            System.out.println("b[4] = " + (b[4] & 0xFF));
                            System.out.println("b[5] = " + (b[5] & 0xFF));
                            System.out.println("b[6] = " + (b[6] & 0xFF));
                            System.out.println("b[7] = " + (b[7] & 0xFF));
                            System.out.println("b[8] = " + (b[8] & 0xFF));
                            System.out.println("b[9] = " + (b[9] & 0xFF));
                            System.out.println();

                            MenuItem Sub_Ports = new MenuItem(serialPort.getPortName());
                            Ports.getItems().add(Sub_Ports);

                        } catch(SerialPortException ex) {
                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
            });
            XBeePort = serialPort;
        } catch (SerialPortException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SerialPortException: " + ex);
        }
    }

    public void disconnectXBee() {
        System.out.println("XBee has been disconnected.");
        if (XBeePort != null) {
            try {
                XBeePort.removeEventListener();
                if (XBeePort.isOpened()) {
                    XBeePort.closePort();
                }
            } catch (SerialPortException ex) {
                Logger.getLogger(Controller.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        disconnectXBee();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}