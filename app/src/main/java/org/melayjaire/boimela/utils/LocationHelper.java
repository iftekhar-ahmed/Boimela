package org.melayjaire.boimela.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.search.SearchCategory;
import org.melayjaire.boimela.search.SearchFilter;

import java.util.ArrayList;
import java.util.List;

public class LocationHelper implements LocationListener {

    public static final long FILTER_DISTANCE = 10;

    private Context context;
    private LocationManager locationManager;
    private BookDataSource dataSource;
    private List<Book> favoriteBooks;
    private List<Long> bIdList;
    private ArrayList<Book> oldList;

    public LocationHelper(Context context) {
        this.context = context;
        Utilities.saveGpsSetting(context, true);
        dataSource = new BookDataSource(context);
        dataSource.open();

        if (favoriteBooks == null) {
            favoriteBooks = new ArrayList<>();
        }

        if (bIdList == null) {
            bIdList = new ArrayList<>();
        }

        if (oldList == null) {
            oldList = new ArrayList<>();
        }

        initialSetUp();
    }

    public void initialSetUp() {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location gpsLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null) {
            updateUserByLocation(gpsLocation);
        } else if (networkLocation != null) {
            updateUserByLocation(networkLocation);
        }
    }

    public void stopGpsTracking() {
        locationManager.removeUpdates(this);
    }

    private Location requestUpdateFromProvider(String provider) {
        Location location = null;
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, 3000, 2, this);
            location = locationManager.getLastKnownLocation(provider);
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateUserByLocation(location);
    }

    private void updateUserByLocation(Location bestLocation) {
        if (dataSource.getAllBooks(SearchCategory.Favorites).size() <= 0) {
            Toast.makeText(context, Utilities.getBanglaSpannableString(context.getString(R.string.no_favbook_added), context), Toast.LENGTH_SHORT).show();
            return;
        }
        float[] distanceArray = new float[1];
        getFavBooks(bestLocation, distanceArray);
    }

    private void getFavBooks(Location location, float[] distanceArray) {
        favoriteBooks.clear();
        bIdList.clear();
        List<Book> bList = new ArrayList<>();
        bList.addAll(dataSource.getAllBooks(SearchCategory.Favorites));

        for (Book book : bList) {
            if (isBookWithinDistance(distanceArray, book, location)
                    && !favoriteBooks.contains(book)) {

                List<Book> favoriteBooksByPublisher = dataSource.getAllBooks(SearchCategory.Favorites
                        , SearchFilter.Publisher.withQuery(book.getPublisherInEnglish(), true));

                for (Book b : favoriteBooksByPublisher) {
                    if (!bIdList.contains(b.getId())) {
                        favoriteBooks.add(b);
                        bIdList.add(b.getId());
                    }
                }
            }
        }
        notify(favoriteBooks);
    }

    private void notify(List<Book> books) {
        if (books.size() > oldList.size()) {
            Utilities.vibrateDevice(context);
            replace(books);
        } else if (oldList.size() > 0 & books.size() == 0) {
            replace(books);
        } else if (books.size() <= oldList.size()) {

            if (books.size() == oldList.size()) {
                if (books.containsAll(oldList)) {

                } else {
                    Utilities.vibrateDevice(context);
                    replace(books);
                }
            } else {
                if (oldList.containsAll(books)) {

                } else {
                    Utilities.vibrateDevice(context);
                    replace(books);
                }
            }
        }

    }

    private void replace(List<Book> books) {
        if (oldList != null)
            oldList.clear();
        if (books != null)
            oldList.addAll(books);
        Utilities.showCustomNotification(context, oldList);
    }

    public void closeDatabase() {
        dataSource.close();
    }

    private boolean isBookWithinDistance(float[] distanceArray, Book book,
                                         Location location) {
        Location.distanceBetween(location.getLatitude(),
                location.getLongitude(), book.getStallLatitude(),
                book.getStallLongitude(), distanceArray);
        return (distanceArray[0] < FILTER_DISTANCE);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

}
