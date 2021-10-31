package nl.vinsev.workoutapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.activity.AuthenticationActivity;
import nl.vinsev.workoutapp.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding fragmentLoginBinding;
    private Button signInButton;
    private EditText email;
    private EditText password;
    private TextView clickHere;
    private AuthenticationActivity authenticationActivity;

    private FirebaseAuth firebaseAuth;

    public synchronized static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        //fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        fragmentLoginBinding = FragmentLoginBinding.inflate(getLayoutInflater());
        signInButton = fragmentLoginBinding.bSignIn;
        email = fragmentLoginBinding.email;
        password = fragmentLoginBinding.password;
        clickHere = fragmentLoginBinding.tvClickHereSignUp;

        setupSignInButton();
        setupClickHere();

        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        authenticationActivity = (AuthenticationActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        authenticationActivity.updateUI(user);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_login);
    }

    private void setupSignInButton() {
        signInButton.setOnClickListener(onClick -> {
            boolean isValidInput = authenticationActivity.isValid(email, password);
            if(isValidInput) {
                signIn(email.getText().toString(), password.getText().toString());
            }
        });
    }

    private void setupClickHere() {
        clickHere.setOnClickListener(onClick -> {
            authenticationActivity.getFragmentContainerView().startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade));
            authenticationActivity.addFragment(RegisterFragment.newInstance());
        });
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authenticationActivity, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this.getContext(), R.string.auth_success, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        authenticationActivity.updateUI(user);
                    } else {
                        Toast.makeText(this.getContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        authenticationActivity.updateUI(null);
                    }
                });
    }
}