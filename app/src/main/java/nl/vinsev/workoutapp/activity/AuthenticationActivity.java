package nl.vinsev.workoutapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.firebase.auth.FirebaseUser;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.databinding.ActivityAuthenticationBinding;
import nl.vinsev.workoutapp.fragment.LoginFragment;
import nl.vinsev.workoutapp.service.AuthenticationValidator;
import nl.vinsev.workoutapp.service.NetworkManager;

public class AuthenticationActivity extends AppCompatActivity {

    private ActivityAuthenticationBinding activityAuthenticationBinding;
    private FragmentContainerView fragmentContainerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkManager.getInstance(this);

        activityAuthenticationBinding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        fragmentContainerView = activityAuthenticationBinding.fcMainContent;
        View view = activityAuthenticationBinding.getRoot();
        setContentView(view);

        if (savedInstanceState == null) {
           addFragment(LoginFragment.newInstance());
        }
    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(fragmentContainerView.getId(), fragment, null)
                .commit();
    }

    public boolean isValid(EditText emailEditText, EditText passwordEditText) {
        AuthenticationValidator emailValidator = new AuthenticationValidator(emailEditText);
        if(!emailValidator.isValidEmail()) {
            emailEditText.setError(emailValidator.getErrorMessage());
        }

        AuthenticationValidator passwordValidator = new AuthenticationValidator(emailEditText);
        if(!passwordValidator.isValidPassword()) {
            passwordEditText.setError(passwordValidator.getErrorMessage());
        }

        return emailValidator.isValidEmail() && passwordValidator.isValidPassword();
    }

    public void reload() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void updateUI(FirebaseUser user) {
        if(user != null) {
            reload();
        }
    }

    public FragmentContainerView getFragmentContainerView() {
        return fragmentContainerView;
    }
}
