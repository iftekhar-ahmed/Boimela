package org.melayjaire.boimela.utils;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

public class VolleyHelper {
	
	private RequestQueue mRequestQueue;
	private String book_url;
	
	private static VolleyHelper uniqueInstance = new VolleyHelper();
	private static Context context;
	private static JsonTaskCompleteListener<JSONArray> jsonCallBack;
	public static final String TAG = "VolleyTag";
	private static final String BOOK_URL_BASE = "http://appsomehow.com.wbm2.my-hosting-panel.com/api/books/get?lastIndex=";

	public static VolleyHelper getInstance(Context context,
			JsonTaskCompleteListener<JSONArray> jsonCallBack) {
		VolleyHelper.context = context;
		VolleyHelper.jsonCallBack = jsonCallBack;
		return uniqueInstance;
	}

	public void setUpApi(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		long maxBookIndex = preferences.getLong(Utilities.MAX_BOOK_INDEX, 0);
		book_url = BOOK_URL_BASE + "" + maxBookIndex;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public void getJsonArray() {

		JsonArrayRequest req = new JsonArrayRequest(book_url,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							jsonCallBack.onJsonArray(response);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Log.e("response error", "error");
					}
				});
		addToRequestQueue(req);
	}

}
