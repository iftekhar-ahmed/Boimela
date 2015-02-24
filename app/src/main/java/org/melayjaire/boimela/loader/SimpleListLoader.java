package org.melayjaire.boimela.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public abstract class SimpleListLoader<T> extends AsyncTaskLoader<List<T>> {

    private List<T> mList;

    public SimpleListLoader(Context context) {
        super(context);
    }

    @Override
    public abstract List<T> loadInBackground();

    @Override
    public void deliverResult(List<T> data) {

        if (isReset()) {
            if (!data.isEmpty())
                data.clear();
        }

        List<T> oldList = mList;
        mList = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldList != null && oldList != data) {
            oldList.clear();
        }
    }

    @Override
    protected void onStartLoading() {

        if (mList != null) {
            deliverResult(mList);
        }

        if (takeContentChanged() || mList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mList != null) {
            if (!mList.isEmpty())
                mList.clear();
            mList = null;
        }
    }

    @Override
    public void onCanceled(List<T> data) {
        super.onCanceled(data);

        if (!data.isEmpty())
            data.clear();
    }

}
