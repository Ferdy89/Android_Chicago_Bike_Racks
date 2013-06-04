package edu.iit.sat.fseror_poc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadFile extends AsyncTask<String, Integer, Boolean> {
	
	ProgressDialog progress;
	Context that;
	
	public DownloadFile(Context context) {
		this.progress = new ProgressDialog(context);
		this.that = context;
		Log.i("Downloader status", "Init");
	}
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        
        progress.setMessage("Downloading the data from the Internet. Please wait...");
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();
    }
	
	@Override
    protected Boolean doInBackground(String... params) {	
		Log.i("Background", "Downloading " + params[1]);
		
        try {
            URL url = new URL(params[1]);
            URLConnection connection = url.openConnection();
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            FileOutputStream output = that.openFileOutput(params[0], Context.MODE_PRIVATE);

            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                if (isCancelled()) break;
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
        	return false;
        }
        return true;
    }

	@Override
    protected void onPostExecute(Boolean result) {
    	Log.i("Post Execute", "Terminating the progress dialog... " + result);
        progress.dismiss();
    }
}
