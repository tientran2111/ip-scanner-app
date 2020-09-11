package app.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.util.SubnetUtils;

import app.config.MessageConstants;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * IP Scanner Service. <br>
 * Started as the target of a thread with {@link Thread#start()}
 *
 * @author Si Tran
 *
 */
public class IpScannerService extends AbstractScannerService {

    private static final String DEFAULT_SCAN = "default";
    private static final String SINGLE_SCAN = "single";
    private static final String RANGE_SCAN = "range";
    private static final String ADMIN_IP = "192.168.1.1";

    private static final String ZERO_TO_255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEX = ZERO_TO_255 + "\\." + ZERO_TO_255 + "\\." + ZERO_TO_255 + "\\." + ZERO_TO_255;

    /** Declare global variable */
    private String ip = "";
    private String startIp = "";
    private String endIp = "";
    private String localIp = "";
    private String scanType;

    /** Addresses list to scan */
    private List<String> scanAddresses = new ArrayList<String>();

    /** Validate using Regex */
    private Pattern pattern;
    private Matcher matcher;

    /**
     * Constructor for the default scan.
     */
    public IpScannerService() {
        initIpProperties();
        scanType = DEFAULT_SCAN;
    }

    /**
     * Constructor for the single scan.
     *
     * @param ip
     */
    public IpScannerService(String ip) {
        this.ip = ip;
        initIpProperties();
        scanType = SINGLE_SCAN;
    }

    /**
     * Constructor for the range scan.
     *
     * @param startIp
     * @param endIp
     */
    public IpScannerService(String startIp, String endIp) {
        this.startIp = startIp;
        this.endIp = endIp;
        initIpProperties();
        scanType = RANGE_SCAN;
    }

    @Override
    public void run() {
        if (checkScanType()) {
            scanListIpAddresses(scanAddresses);
        }
    }

    /**
     * Initialize properties value, set local IP Address and subnet mask in CIDR notation.
     */
    private void initIpProperties() {
        processProps = new SimpleDoubleProperty(0);
        txtAreaProps = new SimpleStringProperty("");
        runningProps = new SimpleBooleanProperty(true);
        setIpSubnetmask();
    }

    /**
     * Sets the localIp variable to the local IP address including the subnet mask
     * in CIDR notation.
     */
    private void setIpSubnetmask() {
        localIp = getLocalIpAddress() + "/" + getSubnetmask();
    }

    /**
     * Check the conditions, which depend on the type of scan, for completeness and
     * correctness.
     *
     * @return true if all conditions for processing the IP Addresses are fulfilled
     */
    private boolean checkScanType() {
        // Check network connections
        if (checkNetworkConnections()) {

            // Get all addresses from local IP
            SubnetUtils subnetUtils = new SubnetUtils(localIp);
            String[] subnetAddresses = subnetUtils.getInfo().getAllAddresses();

            // Switch case by scan type
            switch (scanType) {
            case DEFAULT_SCAN:
                scanAddresses = Arrays.asList(subnetAddresses);
                break;
            case SINGLE_SCAN:
                if (ipAddressValidate(ip)) {
                    List<String> addresses = Arrays.asList(subnetAddresses);
                    if (!addresses.contains(ip)) {
                        StringBuffer result = new StringBuffer();
                        result.setLength(0);
                        result.append("[");
                        result.append(currentDateTime());
                        result.append("]    ");
                        result.append(MessageConstants.E004);
                        result.append("\n");
                        result.append("[");
                        result.append(currentDateTime());
                        result.append("]    ");
                        result.append("The Subnet includes ");
                        result.append(addresses.get(0));
                        result.append(" to ");
                        result.append(addresses.get(addresses.size() - 1));

                        txtAreaProps.set(result.toString());
                        return false;
                    } else {
                        scanAddresses.add(ip);
                    }
                } else {
                    txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E003);
                    return false;
                }
                break;
            case RANGE_SCAN:
                if (ipAddressValidate(startIp) && ipAddressValidate(endIp)) {
                    List<String> addresses = Arrays.asList(subnetAddresses);
                    if (!checkListAddresses(addresses, startIp, endIp)) {
                        StringBuffer result = new StringBuffer();
                        result.setLength(0);
                        result.append("[");
                        result.append(currentDateTime());
                        result.append("]    ");
                        result.append(MessageConstants.E005);
                        result.append("\n");
                        result.append("[");
                        result.append(currentDateTime());
                        result.append("]    ");
                        result.append("The Subnet includes ");
                        result.append(addresses.get(0));
                        result.append(" to ");
                        result.append(addresses.get(addresses.size() - 1));

                        txtAreaProps.set(result.toString());
                        return false;
                    } else {
                        int i = addresses.indexOf(startIp);
                        do {
                            // Add an element to the Addresses list to scan
                            scanAddresses.add(addresses.get(i));
                            i++;
                        } while (!(addresses.get(i - 1).equals(endIp)));
                    }
                } else {
                    // IP Address invalid
                    txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E003);
                    return false;
                }
                break;
            default:
                // 500 Internal Server Error
                txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E002);
                return false;
            }
            return true;
        } else {
            // No network connection found
            txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E006);
            return false;
        }
    }

    /**
     * Validate IP address using Regular Expression.
     * @param ip the IP address
     * @return true/false
     */
    private boolean ipAddressValidate(String ip) {
        pattern = Pattern.compile(IP_REGEX);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * Check order in the list.
     *
     * @param addresses
     * @param startIp
     * @param endIp
     * @return true/false
     */
    private boolean checkListAddresses(List<String> addresses, String startIp, String endIp) {
        boolean result = false;
        if (addresses.contains(startIp)
                && addresses.contains(endIp)
                && (addresses.indexOf(startIp) < addresses.indexOf(endIp))) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the local IP address.
     *
     * @return the local IP address
     */
    private String getLocalIpAddress() {
        String localIp = "";
        try {
            localIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException unknownHostException) {
            txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E007);
            printStackTraceLog(unknownHostException);
        }
        return localIp;
    }

    /**
     * Returns the subnet mask of the network in CIDR notation.
     *
     * @return the subnet mask in CIDR notation
     */
    private short getSubnetmask() {
        short cidr = 0;
        try {
            InetAddress localhost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);
            cidr = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
        } catch (Exception exception) {
            printStackTraceLog(exception);
        }
        return cidr;
    }

    /**
     * The list of IPs to be scanned is iterated over and a ping is sent to this IP.
     * If a response is received within 1000ms, this address counts as available.
     *
     * @param scanAddresses list string
     */
    private void scanListIpAddresses(List<String> scanAddresses) {
        try {
            // Init text result
            txtAreaProps.set("");
            txtAreaProps.set(txtAreaProps.get() + "[" + currentDateTime() + "]    " + MessageConstants.S003 + "\n");
            txtAreaProps.set(txtAreaProps.get() + "[" + currentDateTime() + "]    " + MessageConstants.S002 + "\n");
            runningProps.set(false);

            String hostName = null;
            StringBuffer result = new StringBuffer();
            for (String currentAddress : scanAddresses) {
                InetAddress address = InetAddress.getByName(currentAddress);
                if (address.isReachable(1000)) {
                    // If IP Address = 192.168.1.1
                    if (ADMIN_IP.equals(address.getHostAddress())) {
                        hostName = "Admin";
                    } else if (!address.getHostAddress().equals(address.getHostName())) {
                        hostName = address.getHostName();
                    } else {
                        hostName = "Unknown";
                    }

                    // Create text result
                    result.setLength(0);
                    result.append(txtAreaProps.get());
                    result.append("[");
                    result.append(currentDateTime());
                    result.append("]    ");
                    result.append("The IP address ");
                    result.append(address.getHostAddress());
                    result.append(" (");
                    result.append(hostName);
                    result.append(") ");
                    result.append("is available in the network\n");
                    txtAreaProps.set(result.toString());
                }
                processProps.set((double) scanAddresses.indexOf(currentAddress) / (double) (scanAddresses.size() - 1));

                // If stop requested
                if (isStopRequested()) {
                    processProps.set(0);
                    txtAreaProps.set(txtAreaProps.get() + "[" + currentDateTime() + "]    " + MessageConstants.S001 + "\n");
                    txtAreaProps.set(txtAreaProps.get() + "[" + currentDateTime() + "]    " + "IP Scan stopped at " + currentAddress + "\n");
                    runningProps.set(true);

                    // Exit the loop
                    break;
                }
            }

            result.setLength(0);
            result.append(txtAreaProps.get());
            result.append("[");
            result.append(currentDateTime());
            result.append("]    ");
            result.append("IP Scan from ");
            result.append(scanAddresses.get(0));
            result.append(" to ");
            result.append(scanAddresses.get(scanAddresses.size() - 1));
            result.append(" finished.\n");
            txtAreaProps.set(result.toString());
            runningProps.set(true);
        } catch (UnknownHostException unknownHostException) {
            txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E003);
            printStackTraceLog(unknownHostException);
        } catch (Exception exception) {
            txtAreaProps.set("[" + currentDateTime() + "]    " + MessageConstants.E008);
            printStackTraceLog(exception);
        }
    }

    /**
     * Get current time.
     *
     * @return time HH:mm:ss
     */
    private String currentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    }

    /**
     * Check all network. It is checked whether or not there is a network connection.
     *
     * @return true if there is at least one network connection
     */
    private boolean checkNetworkConnections() {
        try {
            for (Enumeration<NetworkInterface> enumNetwork = NetworkInterface.getNetworkInterfaces(); enumNetwork.hasMoreElements();) {
                NetworkInterface networkInterface = enumNetwork.nextElement();
                for (Enumeration<InetAddress> enumIp = networkInterface.getInetAddresses(); enumIp.hasMoreElements();) {
                    InetAddress inetAddress = enumIp.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return true;
                    }
                }
            }
        } catch (SocketException socketException) {
            printStackTraceLog(socketException);
        } catch (Exception exception) {
            printStackTraceLog(exception);
        }
        return false;
    }

    /**
     * Writes the start and end IP of the subnet into an array.
     *
     * @return string array contains the start and end IP Address of the subnet
     */
    public String[] getSubnetRange() {
        String[] subnetRange = new String[2];
        if (localIp == "") {
            setIpSubnetmask();
        }
        SubnetUtils subnetUtils = new SubnetUtils(localIp);
        String[] arrs = subnetUtils.getInfo().getAllAddresses();
        subnetRange[0] = arrs[0];
        subnetRange[1] = arrs[arrs.length - 1];

        return subnetRange;
    }

    /**
     * Handle exception.
     *
     * @param exception
     */
    private void printStackTraceLog(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        StringBuffer result = new StringBuffer();
        result.setLength(0);
        result.append("[");
        result.append(currentDateTime());
        result.append("]    ");
        result.append(txtAreaProps.get());
        result.append("\n");
        result.append(sw.toString());

        txtAreaProps.set(result.toString());
    }
}
