package org.melayjaire.boimela.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;
import android.widget.TextView;

import org.melayjaire.boimela.NotificationResultActivity;
import org.melayjaire.boimela.R;
import org.melayjaire.boimela.bangla.AndroidCustomFontSupport;
import org.melayjaire.boimela.bangla.TypefaceSpan;
import org.melayjaire.boimela.model.Book;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Utilities {

    public static final String MAX_BOOK_INDEX = "max_index";
    public static final String GPS_TRACKING = "gps_tracking";

    public static Typeface getBanglaFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.font_solaimanlipi));
    }

    public static SpannableString getBanglaSpannableString(String banglaText, Context context) {
        if (banglaText == null) {
            return new SpannableString(new String(""));
        }
        if (isBuildAboveHoneyComb()) {
            SpannableString spannableString = new SpannableString(banglaText);
            if (isBanglaAvailable()) {
                TypefaceSpan span = new TypefaceSpan(getBanglaFont(context));
                spannableString.setSpan(span, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannableString;
        }
        return AndroidCustomFontSupport.getCorrectedBengaliFormat(banglaText, getBanglaFont(context), -1);
    }

    public static boolean isBuildAboveHoneyComb() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    public static boolean isBanglaAvailable() {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (locale.getDisplayName().toLowerCase().contains("bengali")) {
                return true;
            }
        }
        return false;
    }

    public static void storeMaxBookIndex(Context context, List<Long> bookIndexes) {
        Long maxBookIndex = Collections.max(bookIndexes);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(MAX_BOOK_INDEX, maxBookIndex);
        editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showListLoadProgress(Context context,
                                            final RecyclerView rclView, final View progressView, final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            progressView.setVisibility(View.VISIBLE);
            progressView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            rclView.setVisibility(View.VISIBLE);
            rclView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rclView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            rclView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void overrideFont(View v, Typeface newFont) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFont(child, newFont);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(newFont);
            }
        } catch (Exception e) {
        }
    }

    public static void vibrateDevice(Context context) {
        Vibrator v = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        // for 3 seconds
        long milliseconds = 1000;
        long pattern[] = {0, milliseconds, 200, 300, 500};
        v.vibrate(pattern, -1);
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void saveGpsSetting(Context context,
                                      boolean isTrackingServiceOn) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(GPS_TRACKING, isTrackingServiceOn);
        editor.commit();
    }

    public static void showCustomNotification(Context context, List<Book> books) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.logo).setContentTitle(
                context.getString(R.string.available_books_bylocation));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = null;
        if (books != null) {
            events = new String[books.size()];
            int index = 0;
            for (Book book : books) {
                events[index] = book.getPublisher() + " -> " + book.getTitle();
                inboxStyle.addLine(events[index]);
                index++;
            }
        }
        mBuilder.setStyle(inboxStyle);
        Intent resultIntent = new Intent(context,
                NotificationResultActivity.class);

        if (events != null) {
            Bundle b = new Bundle();
            b.putStringArray("BOOK_EVENT", events);
            resultIntent.putExtras(b);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationResultActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, mBuilder.build());
    }

    public static View getActionBarView(Context context, Window window) {
        View v = window.getDecorView();
        int resId = context.getResources().getIdentifier(
                "action_bar_container", "id", "android");
        return v.findViewById(resId);
    }

    @SuppressLint("NewApi")
    public static void setAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT < 11) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        } else
            view.setAlpha(alpha);
    }
}
