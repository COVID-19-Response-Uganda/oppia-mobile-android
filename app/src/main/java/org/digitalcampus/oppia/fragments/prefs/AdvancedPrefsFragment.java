package org.digitalcampus.oppia.fragments.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.webkit.URLUtil;

import org.digitalcampus.mobile.learning.R;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.utils.UIUtils;
import org.digitalcampus.oppia.utils.storage.StorageLocationInfo;
import org.digitalcampus.oppia.utils.storage.StorageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

public class AdvancedPrefsFragment extends BasePreferenceFragment implements PreferenceChangedCallback{

    public static final String TAG = PrefsActivity.class.getSimpleName();
    private ListPreference storagePref;
    private EditTextPreference serverPref;

    public static AdvancedPrefsFragment newInstance() {
        return new AdvancedPrefsFragment();
    }

    public AdvancedPrefsFragment(){
        // Required empty public constructor
        this.adminProtectedValues = Arrays.asList(PrefsActivity.PREF_SERVER);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from XML resources
        addPreferencesFromResource(R.xml.prefs_advanced);
    }


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        storagePref = findPreference(PrefsActivity.PREF_STORAGE_OPTION);
        serverPref = findPreference(PrefsActivity.PREF_SERVER);
        serverPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String url = ((String) newValue).trim();
                if (!URLUtil.isNetworkUrl(url) || !Patterns.WEB_URL.matcher(url).matches()){
                    UIUtils.showAlert(getActivity(),
                            R.string.prefServer_errorTitle,
                            R.string.prefServer_errorDescription);
                    return false;
                }

                // If it is correct, we allow the change
                return true;
            }
        });
        protectAdminEditTextPreferences();

        MaxIntOnStringPreferenceListener maxIntListener = new MaxIntOnStringPreferenceListener();
        findPreference(PrefsActivity.PREF_SERVER_TIMEOUT_CONN).setOnPreferenceChangeListener(maxIntListener);
        findPreference(PrefsActivity.PREF_SERVER_TIMEOUT_RESP).setOnPreferenceChangeListener(maxIntListener);

        updateServerPref();
        updateStorageList(this.getActivity());
        liveUpdateSummary(PrefsActivity.PREF_STORAGE_OPTION);
        liveUpdateSummary(PrefsActivity.PREF_SERVER_TIMEOUT_CONN, " ms");
        liveUpdateSummary(PrefsActivity.PREF_SERVER_TIMEOUT_RESP, " ms");
        EditTextPreference username = findPreference(PrefsActivity.PREF_USER_NAME);
        username.setSummary( "".equals(username.getText()) ?
                getString(R.string.about_not_logged_in) :
                getString(R.string.about_logged_in, username.getText()) );

    }

    public void updateServerPref(){

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        String server = prefs.getString(PrefsActivity.PREF_SERVER, "");
        String status;

        boolean checked = prefs.getBoolean(PrefsActivity.PREF_SERVER_CHECKED, false);
        if (!checked){
            status = getString(R.string.prefServer_notChecked);
        }
        else{
            boolean valid = prefs.getBoolean(PrefsActivity.PREF_SERVER_VALID, false);
            if (valid){
                String name = prefs.getString(PrefsActivity.PREF_SERVER_NAME, server);
                String version = prefs.getString(PrefsActivity.PREF_SERVER_VERSION, "");
                status = name + " (" + version + ")";
            }
            else{
                status = getString(R.string.prefServer_errorTitle);
            }
        }

        serverPref.setText(server);
        serverPref.setSummary(server + "\n" + status);
    }

    public void updateStoragePref(String storageOption){
        if (PrefsActivity.STORAGE_OPTION_EXTERNAL.equals(storageOption)){
            storagePref.setValue(storagePref.getEntryValues()[1].toString());
        }
        else{
            storagePref.setValue(storageOption);
        }
    }

    private void updateStorageList(Context ctx){

        List<StorageLocationInfo> storageLocations = StorageUtils.getStorageList(ctx);
        if (storageLocations.size() > 1){
            //If there is more than one storage option, we create a preferences list

            int writableLocations = 0;
            List<String> entries = new ArrayList<>();
            List<String> entryValues = new ArrayList<>();

            String currentLocation =  getPreferenceManager().getSharedPreferences().getString(PrefsActivity.PREF_STORAGE_LOCATION, "");
            String currentPath = "";

            entries.add(getString(R.string.prefStorageOptionInternal));
            entryValues.add(PrefsActivity.STORAGE_OPTION_INTERNAL);

            for (StorageLocationInfo storageLoc : storageLocations){
                //Only add it as an option if it is writable
                if (!storageLoc.readonly){
                    entries.add(storageLoc.getDisplayName(getActivity()));
                    entryValues.add(storageLoc.path);
                    writableLocations++;

                    if (currentLocation.startsWith(storageLoc.path)){
                        currentPath = storageLoc.path;
                    }
                }
            }

            //If there is only one writable location, we'll use the default prefsList
            if (writableLocations > 1){
                storagePref.setEntryValues(entryValues.toArray(new CharSequence[0]));
                storagePref.setEntries(entries.toArray(new CharSequence[0]));
                storagePref.setValue((currentPath.equals(""))? PrefsActivity.STORAGE_OPTION_INTERNAL : currentPath);
            }
        }
    }

    @Override
    public void onPreferenceUpdated(String pref, String newValue) {
        if (pref.equals(PrefsActivity.PREF_SERVER)){
            updateServerPref();
        }
        else if(pref.equals(PrefsActivity.PREF_STORAGE_OPTION) && (newValue!=null)){
            updateStoragePref(newValue);
        }
    }

    public class MaxIntOnStringPreferenceListener implements Preference.OnPreferenceChangeListener{
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            boolean valid;
            try{
                String intValue = (String) newValue;
                valid = (intValue.length() <= 9); //it'll be bigger than int's max value
            }
            catch (NumberFormatException e){
                valid = false;
            }

            if (!valid){
                UIUtils.showAlert(getActivity(),
                        R.string.prefInt_errorTitle,
                        R.string.prefInt_errorDescription);
            }
            return valid;
        }
    }


}
