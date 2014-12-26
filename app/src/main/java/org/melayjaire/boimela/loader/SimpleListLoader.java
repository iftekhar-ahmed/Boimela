package org.melayjaire.boimela.loader;

import java.util.List;

import org.melayjaire.boimela.model.Book;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class SimpleListLoader extends AsyncTaskLoader<List<Book>> {

	private List<Book> mList;
	
	public SimpleListLoader(Context context) {
		super(context);
	}

	@Override
	public abstract List<Book> loadInBackground();
	
	@Override
	public void deliverResult(List<Book> data) {
		
		if(isReset())
		{
			if(!data.isEmpty())
				data.clear();
		}
		
		List<Book> oldList = mList;
		mList = data;
		
		if(isStarted()){
			super.deliverResult(data);
		}
		
		if(oldList != null && oldList != data){
			oldList.clear();
		}		
	}
	
	@Override
	protected void onStartLoading() {
		
		if(mList != null){
			deliverResult(mList);
		}
		
		if(takeContentChanged() || mList == null){
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
		
		if(mList != null)
		{
			if(!mList.isEmpty())
				mList.clear();
			mList = null;
		}
	}
	
	@Override
	public void onCanceled(List<Book> data) {
		super.onCanceled(data);
		
		if(!data.isEmpty())
			data.clear();
	}

}
