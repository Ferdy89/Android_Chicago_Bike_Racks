package edu.iit.sat.fseror_poc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class getJSON extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... arg0) {
		StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(arg0[0]);
        try {
        	HttpResponse response = client.execute(httpPost);
        	StatusLine statusLine = response.getStatusLine();
        	int statusCode = statusLine.getStatusCode();
        	if (statusCode == 200) {
        		HttpEntity entity = response.getEntity();
        		InputStream content = entity.getContent();
        		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        		String line;
        		while ((line = reader.readLine()) != null) {
        			builder.append(line);
        		}
        	}
        } catch (ClientProtocolException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return builder.toString();
	}
	
}
