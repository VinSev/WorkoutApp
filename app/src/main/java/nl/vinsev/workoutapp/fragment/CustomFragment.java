package nl.vinsev.workoutapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.activity.MainActivity;
import nl.vinsev.workoutapp.databinding.FragmentCustomBinding;

public class CustomFragment extends Fragment {

    private FragmentCustomBinding fragmentCustomBinding;
    private MainActivity mainActivity;

    private FirebaseAuth firebaseAuth;

    public static CustomFragment newInstance() {
        return new CustomFragment();
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        fragmentCustomBinding = FragmentCustomBinding.inflate(inflater, container, false);

        return fragmentCustomBinding.getRoot();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = Objects.requireNonNull((MainActivity) getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            mainActivity.reload();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.title_fragment_custom);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCustomBinding = null;
    }
}
