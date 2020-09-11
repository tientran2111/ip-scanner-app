package app.controller;

import java.net.URL;
import java.util.ResourceBundle;

import app.config.MessageConstants;
import app.service.IpScannerService;
import app.view.PopupWindow;
import app.view.IpTextField;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * IP Scanner Controller.
 *
 * @author Si Tran
 *
 */
public class IpScannerController implements Initializable {

    /** Default scan radio button */
    @FXML
    private RadioButton defaultScanRBtn;

    /** Single scan radio button */
    @FXML
    private RadioButton singleScanRBtn;

    /** Range scan radio button */
    @FXML
    private RadioButton rangeScanRBtn;

    /** Start IP Address text field */
    @FXML
    private IpTextField startIpTxt;

    /** End IP Address text field */
    @FXML
    private IpTextField endIpTxt;

    /** Start scan button */
    @FXML
    private Button startBtn;

    /** Stop scan button */
    @FXML
    private Button stopBtn;

    /** Export file button */
    @FXML
    private Button exportFileBtn;

    /** Help button */
    @FXML
    private Button helpBtn;

    /** Quit button */
    @FXML
    private Button quitBtn;

    /** Result text area */
    @FXML
    private TextArea resultTextArea;

    /** Progress bar */
    @FXML
    private ProgressBar progressBar;

    @FXML
    private final ToggleGroup ipScanBtns = new ToggleGroup();

    private String startIp = "";
    private String endIp = "";

    /** Subnet range array */
    private String[] subnetRange = new String[2];

    /** Thread */
    private Thread thread = null;

    /** IP Scanner Service */
    private IpScannerService ipScannerService = null;

    /**
     * Initialize controller.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init
        startBtn.setGraphic(setIconButton("img/play-16.png"));
        stopBtn.setGraphic(setIconButton("img/stop-16.png"));
        exportFileBtn.setGraphic(setIconButton("img/file-download-16.png"));
        helpBtn.setGraphic(setIconButton("img/help-16.png"));
        quitBtn.setGraphic(setIconButton("img/exit-16.png"));
    }

    /**
     * Handle Radio buttons event.
     */
    @FXML
    private void handleIpRadioBtns() {
        // If scan with Start IP Address
        if (singleScanRBtn.isSelected()) {
            startBtn.disableProperty().unbind();
            startBtn.disableProperty().bind(Bindings.isEmpty(startIpTxt.textProperty()));
            startIpTxt.setDisable(false);
            endIpTxt.setDisable(true);
        } else if (rangeScanRBtn.isSelected()) {
            // If scan with range
            startBtn.disableProperty().unbind();
            startBtn.disableProperty().bind(Bindings.isEmpty(startIpTxt.textProperty()).or(Bindings.isEmpty(endIpTxt.textProperty())));
            startIpTxt.setDisable(false);
            endIpTxt.setDisable(false);
        } else {
            // Default
            startBtn.disableProperty().unbind();
            startBtn.setDisable(false);
            startIpTxt.setDisable(true);
            endIpTxt.setDisable(true);
        }
    }

    /**
     * Handle Start Scan button event.
     */
    @FXML
    private void handleIpStartBtn() {
        // Get value from text field
        if (singleScanRBtn.isSelected()) {
            startIp = startIpTxt.getText();
        } else if (rangeScanRBtn.isSelected()) {
            startIp = startIpTxt.getText();
            endIp = endIpTxt.getText();
        }

        // If the Thread has been initialized
        if (thread != null) {
            // If thread is not alive
            if (!thread.isAlive()) {
                // Start IP Scanner Thread
                startIpThread();
            } else {
                // If thread is alive
                // Show popup warning
                PopupWindow.createPopupWindow("Scan is already running", MessageConstants.E001);
            }
        } else {
            // Start IP Scanner Thread
            startIpThread();
        }
        startIp = "";
        endIp = "";
    }

    /**
     * Handle Stop Scan button event.
     */
    @FXML
    private void handleIpStopBtn() {
        if (thread != null) {
            if (thread.isAlive()) {
                ipScannerService.stop();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle Help button event.
     */
    @FXML
    private void handleHelpBtn() {
        ipScannerService = new IpScannerService();
        subnetRange = ipScannerService.getSubnetRange();

        PopupWindow.createPopupWindow("Subnet range", "The Subnet includes addresses from " + subnetRange[0] + " to " + subnetRange[1]);
    }

    /**
     * Handle Quit button event.
     */
    @FXML
    private void handleQuitBtn() {
        Stage stage = (Stage) quitBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Handle Export file button event.
     */
    @FXML
    private void handleExportFileBtn() {
        PopupWindow.createPopupWindow("Function not available", "Function not available. Coming soon!");
    }

    /**
     * Start Thread.
     */
    private void startIpThread() {
        if (startIp == "" && endIp == "") {
            ipScannerService = new IpScannerService();
        } else if (startIp != "" && endIp != "") {
            ipScannerService = new IpScannerService(startIp, endIp);
        } else {
            ipScannerService = new IpScannerService(startIp);
        }
        thread = new Thread(ipScannerService);
        progressBar.progressProperty().bind(ipScannerService.processProps);
        resultTextArea.textProperty().bind(ipScannerService.txtAreaProps);
        stopBtn.disableProperty().bind(ipScannerService.runningProps);
        thread.start();
    }

    /**
     * Set icon button.
     *
     * @param icon location icon
     * @return ImageView
     */
    private ImageView setIconButton(String icon) {
        Image img = new Image(this.getClass().getClassLoader().getResourceAsStream(icon));
        ImageView view = new ImageView(img);
        view.setFitHeight(16.0);
        view.setFitWidth(16.0);
        view.setPreserveRatio(true);
        view.setPickOnBounds(true);
        return view;
    }
}
