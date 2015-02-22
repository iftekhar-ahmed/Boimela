package org.melayjaire.boimela.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.melayjaire.boimela.NotificationResultActivity;
import org.melayjaire.boimela.R;
import org.melayjaire.boimela.bangla.AndroidCustomFontSupport;
import org.melayjaire.boimela.bangla.TypefaceSpan;
import org.melayjaire.boimela.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Utilities {

    public static final boolean isBuildAboveHoneyComb = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2;
    public static final boolean isBanglaAvailable = isBanglaAvailable();

    private static final HashMap<Character, Character> digitsMap = new HashMap<>();

    static {
        digitsMap.put('0', '০');
        digitsMap.put('1', '১');
        digitsMap.put('2', '২');
        digitsMap.put('3', '৩');
        digitsMap.put('4', '৪');
        digitsMap.put('5', '৫');
        digitsMap.put('6', '৬');
        digitsMap.put('7', '৭');
        digitsMap.put('8', '৮');
        digitsMap.put('9', '৯');
    }

    private static Typeface typeface;

    public static Typeface getBanglaFont(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/" + context.getString(R.string.font_solaimanlipi));
        }
        return typeface;
    }

    public static SpannableString getBanglaSpannableString(String banglaText, Context context) {
        if (banglaText == null) {
            return new SpannableString("");
        }
        if (isBuildAboveHoneyComb) {
            SpannableString spannableString = new SpannableString(banglaText);
            if (isBanglaAvailable) {
                TypefaceSpan span = new TypefaceSpan(getBanglaFont(context));
                spannableString.setSpan(span, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannableString;
        }
        return AndroidCustomFontSupport.getCorrectedBengaliFormat(banglaText, getBanglaFont(context), -1);
    }

    private static boolean isBanglaAvailable() {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (locale.getDisplayName().toLowerCase().contains("bengali")) {
                return true;
            }
        }
        return false;
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

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void cancelNotificationForNearbyFavoriteBooks(Context context) {
        int notification_id = context.getResources().getInteger(R.integer.nearby_books_notification_id);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notification_id);
    }

    public static void showNotificationForNearbyFavoriteBooks(Context context, Set<Book> books) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_star_full).setContentTitle(
                context.getString(R.string.nearby_favorite_books));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if (books != null) {
            for (Book book : books) {
                inboxStyle.addLine(book.getPublisher() + " -> " + book.getTitle());
            }
        }
        mBuilder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(context, NotificationResultActivity.class);
        if (books != null) {
            Bundle b = new Bundle();
            b.putParcelableArrayList(Constants.EXTRA_BOOKS, new ArrayList<>(books));
            resultIntent.putExtras(b);
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int notification_id = context.getResources().getInteger(R.integer.nearby_books_notification_id);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

    public static String translateCount(long count) {
        count = count > 100 ? (count - count % 100) : count;
        char[] digits = (String.valueOf(count)).toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char digit : digits) {
            sb.append(digitsMap.get(digit));
        }
        return count > 99 ? sb.append("+").toString() : sb.toString();
    }
}
