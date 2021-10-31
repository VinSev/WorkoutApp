package nl.vinsev.workoutapp.service;

import android.util.Patterns;
import android.widget.EditText;

import nl.vinsev.workoutapp.R;


public class AuthenticationValidator {

    private final EditText editText;
    private boolean isValid = true;
    private String errorMessage = "";

    public AuthenticationValidator(EditText editText) {
        this.editText = editText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValidEmail() {
        if (editText.getText().toString().isEmpty()) {
            errorMessage = editText.getResources().getString(R.string.email_error_empty);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
            errorMessage = editText.getResources().getString(R.string.email_error_invalid);
            isValid = false;
        }
        return isValid;
    }

    public boolean isValidPassword() {
        if (editText.getText().toString().isEmpty()) {
            errorMessage = editText.getResources().getString(R.string.password_error_empty);
            isValid = false;
        } else if (editText.getText().length() < 6) {
            errorMessage = editText.getResources().getString(R.string.password_error_invalid);
            isValid = false;
        }
        return isValid;
    }
}
