package org.melayjaire.boimela;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.melayjaire.boimela.utils.Utilities;

public class GpsSettingScreen {

    private Context context;

    public GpsSettingScreen(Context context) {
        this.context = context;
        showDialog();
    }

    private void showDialog() {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.dialog_gps_alarm, null);

        dialogBuilder.setTitle(R.string.notice);
        dialogBuilder.setView(dialogView);
        ((TextView) dialogView.findViewById(R.id.textView_gps_msg))
                .setText(Utilities.getBanglaSpannableString(context.getString(R.string.gps_alarm_msg), context));

        final AlertDialog alert = dialogBuilder.create();

        alert.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                }
                return true;
            }
        });

        alert.setButton(DialogInterface.BUTTON_POSITIVE,
                context.getString(R.string.yes), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        final Intent gpsIntent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        gpsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ((HomeActivity) context).startActivityForResult(
                                gpsIntent, HomeActivity.GPS_REQUEST_CODE);
                        // context.startActivity(gpsIntent);
                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE,
                context.getString(R.string.no), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert.dismiss();
                    }
                });
        alert.show();
    }
}
