package org.melayjaire.boimela;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.ui.BookListFragment;
import org.melayjaire.boimela.ui.NearbyBookListFragment;
import org.melayjaire.boimela.utils.Constants;
import org.melayjaire.boimela.utils.Utilities;

import java.util.ArrayList;

public class NotificationResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_result);

        ArrayList<Book> books = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            books = extras.getParcelableArrayList(Constants.EXTRA_BOOKS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        // toolbar.setTitle(Utilities.getBanglaSpannableString(getString(R.string.nearby_favorite_books), this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BookListFragment nearbyBookListFragment = NearbyBookListFragment.newInstance(books);
        fragmentTransaction.add(R.id.fragment_container, nearbyBookListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
