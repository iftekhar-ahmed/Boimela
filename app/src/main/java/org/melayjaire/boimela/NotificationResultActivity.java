package org.melayjaire.boimela;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.melayjaire.boimela.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class NotificationResultActivity extends Activity {

    private String[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_result);

        setTitle(Utilities.getBanglaSpannableString(getString(R.string.favorite_books_alarm), this));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            values = extras.getStringArray("BOOK_EVENT");
            ListView listview = (ListView) findViewById(R.id.lv_notification);

            ArrayList<String> list = new ArrayList<String>();
            list.clear();

            for (int i = 0; i < values.length; ++i) {
                list.add(values[i]);
                Log.e("Books:", "" + values[i] + "" + i);
            }
            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, list);
            listview.setAdapter(adapter);
        }

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        List<String> books;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);

            books = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            String notfText = books.get(position);
            View v = super.getView(position, convertView, parent);
            ((TextView) v).setText(Utilities.getBanglaSpannableString(notfText, NotificationResultActivity.this));
            return v;
        }
    }
}
