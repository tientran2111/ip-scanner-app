package app.view;

import java.awt.Toolkit;

import javafx.scene.control.TextField;

/**
 * Text field with option to limit user input.
 *
 * @author Si Tran
 *
 */
public class CustomTextField extends TextField {

    /** Max length */
    private int maxLength;

    /** Regex */
    private String regex;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if ("".equals(text)) {
            super.replaceText(start, end, text);
        } else if (text.matches(regex) && getText().length() < maxLength) {
            super.replaceText(start, end, text);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (text == "") {
            super.replaceSelection(text);
        } else if (text.matches(regex) && getText().length() <= maxLength) {
            if (text.length() > maxLength - getText().length()) {
                text = text.substring(0, maxLength - getText().length());
            }
            super.replaceSelection(text);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
