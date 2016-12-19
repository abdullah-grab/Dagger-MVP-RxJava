package com.oneous.currencyconverter.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Network Service using volley
 */
@Singleton
public class ConversionService {
    private static final String TAG = ConversionService.class.getName();
    private static final String BASE_URL = "http://api.fixer.io/latest";

    RequestQueue requestQueue;

    @Inject
    public ConversionService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public Observable<Double> getValue() {
        Log.d(TAG, "getValue() called from network");
        return Observable
                .create(subscriber -> {
                    requestQueue.add(new JsonObjectRequest
                            (Request.Method.GET, BASE_URL, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, "onSuccess for network request");
                                    try {
                                        String date = response.getString("date");
                                        JSONObject rates = response.getJSONObject("rates");
                                        double currencyRate = rates.getDouble("SGD");
                                        Log.d(TAG, "onSuccess: date=" + date + " currencyRate=" + currencyRate);
                                        subscriber.onNext(currencyRate);
                                        subscriber.onCompleted();
                                    } catch (JSONException e) {
                                        Log.e(TAG, "JSONException: ", e);
                                        subscriber.onError(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "onErrorResponse: ", error);
                                    subscriber.onError(error);

                                }
                            }));
                });
    }
}
