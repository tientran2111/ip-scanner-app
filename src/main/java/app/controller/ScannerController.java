package app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * Scanner Controller.
 *
 * @author Si Tran
 *
 */
public class ScannerController {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab ipScannerTab;

    /**
     * Set IP Scanner tab.
     *
     * @param ipTab
     */
    public void setIpScannerTab(AnchorPane ipTab) {
        ipScannerTab.setContent(ipTab);
    }
}
