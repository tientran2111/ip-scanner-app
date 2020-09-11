package app.view;

import java.io.IOException;

import app.config.ApplicationProperties;
import app.controller.ScannerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Structure of the GUI of the IP Scanner Application.
 *
 * @author Si Tran
 *
 */
public class IpScannerApplication extends Application {

    private static final String SCANNER_CONTROLLER = "/app/controller/fxml/ScannerController.fxml";
    private static final String IP_SCANNER_CONTROLLER = "/app/controller/fxml/IpScannerController.fxml";

    private VBox scannerApp;

    /** IP Scanner tab */
    private AnchorPane ipScannerTab;

    /** Scanner Controller */
    private ScannerController scannerController;

    @Override
    public void start(Stage primaryStage) {
        try {
            loadModules();
            initTabs();

            Scene scene = new Scene(scannerApp);

            // Load internal CSS file
            scannerApp.getStylesheets().add(this.getClass().getResource("/css/style.css").toExternalForm());
            // Set the title of the application
            String title = ApplicationProperties.APP_NAME + ApplicationProperties.SPACE_1
                    + ApplicationProperties.APP_VERSION + ApplicationProperties.SPACE_1
                    + ApplicationProperties.APP_RELEASE_DATE;
            primaryStage.setTitle(title);
            // Set the icon of the application
            primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("img/logo.png")));
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all required modules for the GUI.
     */
    private void loadModules() {
        // Load Scanner (Main)
        loadScanner();
        // Load IP Scanner (Tab)
        loadIpScanner();
    }

    /**
     * Init all tabs.
     */
    private void initTabs() {
        // Init IP Scanner tab
        scannerController.setIpScannerTab(ipScannerTab);
    }

    /**
     * Load the GUI of the general from FXML file.
     */
    private void loadScanner() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(IpScannerApplication.class.getResource(SCANNER_CONTROLLER));
            scannerApp = loader.load();
            scannerController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the GUI of the IP scanner through the corresponding FXML file.
     */
    private void loadIpScanner() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(IpScannerApplication.class.getResource(IP_SCANNER_CONTROLLER));
            ipScannerTab = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
