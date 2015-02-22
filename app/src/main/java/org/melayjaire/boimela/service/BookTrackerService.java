package org.melayjaire.boimela.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.model.Publisher;
import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;
import org.melayjaire.boimela.utils.Constants;
import org.melayjaire.boimela.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookTrackerService extends Service {

    private List<Publisher> publishersInFavoriteBooks;
    private HashMap<Publisher, List<Book>> publisherBooksMap;
    private Set<Publisher> nearbyPublishers;
    private BookDataSource dataSource;
    private UserLocationListener userLocationListener;
    private BroadcastReceiver bookUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_UPDATE_BOOK)) {
                createPublisherToBooksMap();
            }
        }
    };

    private synchronized void update(Location userLocation) {
        boolean updateAvailable = false;
        boolean vibrate = false;
        for (Publisher publisher : publishersInFavoriteBooks) {
            if (isPublisherStallNearby(userLocation, publisher.getStallLatitude(), publisher.getStallLongitude())) {
                if (!nearbyPublishers.contains(publisher)) {
                    nearbyPublishers.add(publisher);
                    updateAvailable = vibrate = true;
                }
            } else {
                if (nearbyPublishers.contains(publisher)) {
                    nearbyPublishers.remove(publisher);
                    updateAvailable = true;
                }
            }
        }
        if (updateAvailable) {
            Set<Book> booksForNotification = new HashSet<>();
            for (Publisher publisher : nearbyPublishers) {
                booksForNotification.addAll(publisherBooksMap.get(publisher));
            }
            notifyUser(booksForNotification, vibrate);
        }
    }

    private void notifyUser(Set<Book> booksForNotification, boolean vibrate) {
        if (vibrate) {
            Utilities.vibrateDevice(this);
        }
        Utilities.showNotificationForNearbyFavoriteBooks(this, booksForNotification);
    }

    private boolean isPublisherStallNearby(Location userLocation, double stallLatitude, double stallLongitude) {
        float distance[] = new float[1];
        Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude()
                , stallLatitude, stallLongitude, distance);
        return (distance[0] < Constants.FILTER_DISTANCE);
    }

    private void createPublisherToBooksMap() {
        if (publishersInFavoriteBooks == null) {
            publishersInFavoriteBooks = new ArrayList<>();
        } else {
            publishersInFavoriteBooks.clear();
        }
        if (publisherBooksMap == null) {
            publisherBooksMap = new HashMap<>();
        } else {
            publisherBooksMap.clear();
        }
        publishersInFavoriteBooks = dataSource.getAllPublishers(SearchCriteria.Favorites);
        for (Publisher publisher : publishersInFavoriteBooks) {
            List<Book> favoriteBooksByPublisher = dataSource.getAllBooks(SearchCriteria.Favorites
                    , SearchFilter.Publisher.withQuery(publisher.getNameInEnglish(), true, true));
            publisherBooksMap.put(publisher, favoriteBooksByPublisher);
        }
    }

    private Set<Book> getDummyBooks() {
        Set<Book> dummyBooks = new HashSet<>();
        dummyBooks.addAll(dataSource.getAllBooks(SearchCriteria.Favorites, SearchFilter.Title.withQuery("himu", false, true)));
        return dummyBooks;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataSource = new BookDataSource(this);
        dataSource.open();
        LocalBroadcastManager.getInstance(this).registerReceiver(bookUpdateBroadcastReceiver
                , new IntentFilter(Constants.ACTION_UPDATE_BOOK));

        createPublisherToBooksMap();
        nearbyPublishers = new HashSet<>();
        userLocationListener = new UserLocationListener(this);
        notifyUser(getDummyBooks(), true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userLocationListener.start();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        userLocationListener.stop();
        dataSource.close();
        super.onDestroy();
    }

    protected class UserLocationListener implements LocationListener {

        private Context context;
        private LocationManager locationManager;

        public UserLocationListener(Context context) {
            this.context = context;
        }

        public void start() {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            Location gpsLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
            Location networkLocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null) {
                update(gpsLocation);
            } else if (networkLocation != null) {
                update(networkLocation);
            }
        }

        public void stop() {
            locationManager.removeUpdates(this);
        }

        private Location requestUpdateFromProvider(String provider) {
            Location location = null;
            if (locationManager.isProviderEnabled(provider)) {
                locationManager.requestLocationUpdates(provider
                        , Constants.LOCATION_UPDATE_INTERVAL
                        , Constants.LOCATION_UPDATE_DISTANCE, this);
                location = locationManager.getLastKnownLocation(provider);
            }
            return location;
        }

        @Override
        public void onLocationChanged(Location location) {
            update(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            stopSelf();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
