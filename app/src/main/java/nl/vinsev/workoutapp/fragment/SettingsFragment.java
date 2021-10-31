package nl.vinsev.workoutapp.fragment;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import nl.vinsev.workoutapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        etpSetInputType("etp_key_exercise_interval");
        etpSetInputType("etp_key_rest_interval");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_settings);
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private void etpSetInputType(String key) {
        EditTextPreference editTextPreference = getPreferenceManager().findPreference(key);

        editTextPreference.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        });
    }
}
