/**************************************************************************
*
* ClimbzRESTService is responsible for perofming RESTful network operations
* 
* It is an IntentService and only active while the operation is being performed
* 
* ClimbzRESTService expects an intent containing:
* 
* - Uri of server
* - HTTP verb as an extra packaged using EXTRA_HTTP_VERB (optional, defaults to GET)
* - ResultReceiver as an extra packaged using EXTRA_RESULT_RECEIVER
* - params as a bundle packaged using EXTRA_PARAMS (optional)
*
***************************************************************************/
package com.dab.climbz;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class ClimbzRESTService extends IntentService {
	
	public static final String LOG_TAG = "ClimbzRESTService";
	
	public static final String EXTRA_HTTP_VERB = "com.dab.climbz.EXTRA_HTTP_VERB";
	public static final String EXTRA_PARAMS = "com.dab.climbz.EXTRA_PARAMS";
	public static final String EXTRA_RESULT_RECEIVER = "com.dab.climbz.EXTRA_RESULT_RECEIVER";
	public static final String REST_RESULT = "com.dab.climbz.REST_RESULT";
	
	public enum HttpVerb {
		GET,
		POST,
		PUT,
		DELETE;
	}

	public ClimbzRESTService() {
		super(ClimbzRESTService.class.getName());
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Uri action = intent.getData();
		Bundle extras = intent.getExtras();
		
		if (null == action || null == extras || !extras.containsKey(EXTRA_RESULT_RECEIVER)) {
			Log.e(LOG_TAG, "Error: starting intent did not contain correct extras");
			return;
		}

		HttpVerb verb = HttpVerb.values()[extras.getInt(EXTRA_HTTP_VERB)];
		Bundle params = extras.getParcelable(EXTRA_PARAMS);
		ResultReceiver receiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);
		
		try {
			HttpRequestBase request = null;
			
			switch (verb) {
				case GET:
					request = new HttpGet();
					attachUriWithQuery(request, action, params);
					break;
					
				case POST:
					request = new HttpPost();
					request.setURI(new URI(action.toString()));
					
					HttpPost postRequest = (HttpPost)request;
					
					if (null != params) {
						UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
						postRequest.setEntity(formEntity);
					}
					break;
					
				case PUT:
					request = new HttpPut();
					request.setURI(new URI(action.toString()));
					
					HttpPut putRequest = (HttpPut)request;
					if (null != params) {
						UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
						putRequest.setEntity(formEntity);
					}
					break;
					
				case DELETE:
					request = new HttpDelete();
					attachUriWithQuery(request, action, params);					
					break;
			}
			
			if (null != request) {
				HttpClient client = new DefaultHttpClient();
				
				Log.i(LOG_TAG, "Attempting to execute request with verb=" + verb + " and action=" + action.toString());
				
				HttpResponse response = client.execute(request);
				HttpEntity responseEntity = response.getEntity();
				StatusLine responseStatus = response.getStatusLine();
				int statusCode;
				if (null != responseStatus) {
					statusCode = responseStatus.getStatusCode();
				} else {
					statusCode = 0;
				}
				
				if (null != responseEntity) {
					Bundle data = new Bundle();
					data.putString(REST_RESULT, EntityUtils.toString(responseEntity));
					receiver.send(statusCode, data);
				} else {
					receiver.send(statusCode, null);
				}				
			}
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error: exception while attempting to execute REST operation", e);
		}		
	}
	
	private static void attachUriWithQuery(HttpRequestBase request, Uri uri, Bundle b) {
		try {
			if (null == b) {
				request.setURI(new URI(uri.toString()));
			} else {
				Uri.Builder uriBuilder = uri.buildUpon();
				
				for (BasicNameValuePair param : paramsToList(b)) {
					uriBuilder.appendQueryParameter(param.getName(), param.getValue());
				}
				
				uri = uriBuilder.build();
				request.setURI(new URI(uri.toString()));
			}
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error: exception thrown in attachUriWithQuery", e);
		}
	}
	
	private static List<BasicNameValuePair> paramsToList(Bundle params) {
		ArrayList<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>(params.size());
		
		for (String key : params.keySet()) {
			Object value = params.get(key);
			
			if (null != value) {
				paramList.add(new BasicNameValuePair(key, value.toString()));
			}
		}
		
		return paramList;
	}
}