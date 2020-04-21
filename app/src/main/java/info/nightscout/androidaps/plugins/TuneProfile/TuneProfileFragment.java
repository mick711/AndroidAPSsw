package info.nightscout.androidaps.plugins.TuneProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

//2 unknown imports disabled by philoul to build AAPS
//import butterknife.BindView;
//import butterknife.OnClick;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.data.ProfileStore;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.general.careportal.CareportalFragment;
import info.nightscout.androidaps.plugins.general.careportal.Dialogs.NewNSTreatmentDialog;
import info.nightscout.androidaps.plugins.general.careportal.OptionsToShow;
import info.nightscout.androidaps.plugins.profile.ns.NSProfilePlugin;
import info.nightscout.androidaps.services.Intents;
import info.nightscout.androidaps.utils.OKDialog;
import info.nightscout.androidaps.utils.SP;

/**
 * Created by Rumen Georgiev on 1/29/2018.
 * Rebase with current dev by philoul on 03/02/2020
 */

public class TuneProfileFragment extends Fragment implements View.OnClickListener {
    private static Logger log = LoggerFactory.getLogger(TuneProfileFragment.class);
    public TuneProfileFragment() {super();}
    static public TuneProfilePlugin getPlugin() throws IOException {
        return TuneProfilePlugin.getPlugin();
    }

    Button runTuneNowButton;
// disabled by philoul to build AAPS
//    @BindView(R.id.tune_profileswitch)
    Button tuneProfileSwitch;
    TextView warningView;
    TextView resultView;
    TextView lastRunView;
    EditText tune_days;
    //TuneProfile tuneProfile = new TuneProfile();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.tuneprofile_fragment, container, false);

            warningView = (TextView) view.findViewById(R.id.tune_warning);
            resultView = (TextView) view.findViewById(R.id.tune_result);
            lastRunView = (TextView) view.findViewById(R.id.tune_lastrun);
            runTuneNowButton = (Button) view.findViewById(R.id.tune_run);
            tuneProfileSwitch = (Button) view.findViewById(R.id.tune_profileswitch);
            tune_days = (EditText) view.findViewById(R.id.tune_days);
            runTuneNowButton.setOnClickListener(this);
            tuneProfileSwitch.setVisibility(View.GONE);
            tuneProfileSwitch.setOnClickListener(this);
            warningView.setText("Don't run tune for more than 5 days back! It will cause app crashes and too much data usage! Don't even try to run without WiFi connectivity!");
            resultView.setText(TuneProfilePlugin.result);
            lastRunView.setText(TuneProfilePlugin.lastRuntxt);
            updateGUI();
            return view;
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        return null;
    }

// disabled by philoul to build AAPS
// @OnClick(R.id.nsprofile_profileswitch)
    public void onClickProfileSwitch() {
        String name = MainApp.gs(R.string.tuneprofile_name);
        ProfileStore store = NSProfilePlugin.getPlugin().getProfile();
        if (store != null) {
            Profile profile = store.getSpecificProfile(name);
            if (profile != null) {
// todo Philoul activate profile switch once AT is Ok (to update to be compatible with local profiles)
//                 OKDialog.showConfirmation(getActivity(), MainApp.gs(R.string.activate_profile) + ": " + name + " ?", () ->
//                         NewNSTreatmentDialog.doProfileSwitch(store, name, 0, 100, 0)
//                 );
            }
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tune_run) {

            int daysBack = Integer.parseInt(tune_days.getText().toString());
            if (daysBack > 0)
//            resultView.setText(TuneProfile.bgReadings(daysBack));
                try {
                    TuneProfilePlugin tuneProfile = new TuneProfilePlugin();
                    resultView.setText(tuneProfile.result(daysBack));
                    tuneProfileSwitch.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            else
                resultView.setText("Set days between 1 and 10!!!");
            // lastrun in minutes ???
            warningView.setText("You already pressed RUN - NO WARNING NEEDED!");
            lastRunView.setText(TuneProfilePlugin.lastRuntxt);
        } else if (id == R.id.tune_profileswitch){
            String name = MainApp.gs(R.string.tuneprofile_name);
            ProfileStore profile = null;
            log.debug("ProfileSwitch pressed");

            String profileString = SP.getString("autotuneprofile", null);
            if (profileString != null) {
                if (L.isEnabled(L.PROFILE))
                    log.debug("Loaded profile: " + profileString);
                try {
                    profile = new ProfileStore(new JSONObject(profileString));
                } catch (JSONException e) {
                    log.error("Unhandled exception", e);
                    profile = null;
                }
            } else {
                if (L.isEnabled(L.PROFILE))
                    log.debug("Stored profile not found");
            }

            if (profile != null) {
                final ProfileStore store = profile;
                NewNSTreatmentDialog newDialog = new NewNSTreatmentDialog();
//                final OptionsToShow profileswitch = CareportalFragment.PROFILESWITCH;
//                profileswitch.executeProfileSwitch = true;
//                newDialog.setOptions(profileswitch, R.string.careportal_profileswitch);
//                newDialog.show(getFragmentManager(), "NewNSTreatmentDialog");

// todo Philoul activate profile switch once AT is Ok (to update to be compatible with local profiles)
//                 OKDialog.showConfirmation(getActivity(), MainApp.gs(R.string.activate_profile) + ": " + name + " ?", () ->
//                         NewNSTreatmentDialog.doProfileSwitch(store, name, 0, 100, 0)
//                 );
            } else
                log.debug("ProfileStore is null!");
        }

        //updateGUI();
    }

    // disabled by philoul to build AAPS
    //@Override
    protected void updateGUI() {
        Activity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tune_days.setText(SP.getString("autotune_default_tune_days","5"));

                }
            });
    }

}
