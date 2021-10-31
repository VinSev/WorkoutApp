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
import nl.vinsev.workoutapp.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding fragmentRegisterBinding;
    private Button signUpButton;
    private EditText email;
    private EditText password;
    private TextView clickHere;
    private AuthenticationActivity authenticationActivity;

    private FirebaseAuth firebaseAuth;

    public synchronized static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        //fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(getLayoutInflater());
        signUpButton = fragmentRegisterBinding.bSignUp;
        email = fragmentRegisterBinding.email;
        password = fragmentRegisterBinding.password;
        clickHere = fragmentRegisterBinding.tvClickHereSignIn;

        setupSignInButton();
        setupClickHere();

        return fragmentRegisterBinding.getRoot();
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
        getActivity().setTitle(R.string.title_fragment_register);
    }

    private void setupSignInButton() {
        signUpButton.setOnClickListener(onClick -> {
            boolean isValidInput = authenticationActivity.isValid(email, password);
            if(isValidInput) {
                signUp(email.getText().toString(), password.getText().toString());
            }
        });
    }

    private void setupClickHere() {
        clickHere.setOnClickListener(onClick -> {
            authenticationActivity.getFragmentContainerView().startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade));
            authenticationActivity.addFragment(LoginFragment.newInstance());
        });
    }

    private void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
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