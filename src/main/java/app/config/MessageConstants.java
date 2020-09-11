package app.config;

public final class MessageConstants {

    // Error message
    public static final String E001 = "A scan is already running. This must first be stopped or canceled before another can be started.";
    public static final String E002 = "500 Internal Server Error.";
    public static final String E003 = "IP Address invalid.";
    public static final String E004 = "IP Address is not in the Subnet";
    public static final String E005 = "The IP's are not in the Subnet or invalid order.";
    public static final String E006 = "No network connection found.";
    public static final String E007 = "Local IP Address could not be found.";
    public static final String E008 = "IP Scan has an Internal Error. Please try again.";

    // Success message
    public static final String S001 = "Status change detected: stopped";
    public static final String S002 = "Status change detected: running";
    public static final String S003 = "IP Scanner App Ready!";
    public static final String S004 = "Status change detected: stopped";
    public static final String S005 = "Status change detected: stopped";
}
