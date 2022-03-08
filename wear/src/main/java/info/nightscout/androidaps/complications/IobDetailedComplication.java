package info.nightscout.androidaps.complications;

import android.app.PendingIntent;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationText;

import info.nightscout.androidaps.data.RawDisplayData;
import info.nightscout.androidaps.interaction.utils.Pair;
import info.nightscout.shared.logging.LTag;

/*
 * Created by dlvoy on 2019-11-12
 */
public class IobDetailedComplication extends BaseComplicationProviderService {

    public ComplicationData buildComplicationData(int dataType, RawDisplayData raw, PendingIntent complicationPendingIntent) {

        ComplicationData complicationData = null;

        if (dataType == ComplicationData.TYPE_SHORT_TEXT) {

            Pair<String, String> iob = displayFormat.detailedIob(raw);
            final ComplicationData.Builder builder = new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                    .setShortText(ComplicationText.plainText(iob.first))
                    .setTapAction(complicationPendingIntent);

            if (iob.second.length() > 0) {
                builder.setShortTitle(ComplicationText.plainText(iob.second));
            }

            complicationData = builder.build();
        } else {
            aapsLogger.warn(LTag.WEAR, "Unexpected complication type " + dataType);
        }
        return complicationData;
    }

    @Override
    public String getProviderCanonicalName() {
        return IobDetailedComplication.class.getCanonicalName();
    }

    @Override
    public ComplicationAction getComplicationAction() {
        return ComplicationAction.BOLUS;
    }
}
