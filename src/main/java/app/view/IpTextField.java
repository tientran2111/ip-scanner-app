package app.view;

/**
 * Text field for Start, End IP address.
 *
 * @author Si Tran
 *
 */
public class IpTextField extends CustomTextField {

    private static final int MAX_LENGTH = 15;
    private static final String REGEX = "[0-9]|\\.";

    public IpTextField() {
        super.setMaxLength(MAX_LENGTH);
        super.setRegex(REGEX);
    }
}
