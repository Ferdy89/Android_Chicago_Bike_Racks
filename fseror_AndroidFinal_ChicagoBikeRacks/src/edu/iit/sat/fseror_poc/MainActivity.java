package edu.iit.sat.fseror_poc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	Context that = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    that = this;
	    
	    ImageButton address = (ImageButton) findViewById(R.id.address);
	    ImageButton map = (ImageButton) findViewById(R.id.position);
	    
	    OnClickListener addressListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String address = ((EditText)findViewById(R.id.addressText)).getText().toString();
				createIntentMap(address);
			}
		};
		address.setOnClickListener(addressListener);
	    
	    OnClickListener mapListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				createIntentMap("");
			}
		};
		map.setOnClickListener(mapListener);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	 switch (item.getItemId()) {
    	 	case R.id.Update :
    	 		updateRacksDatabase();
    	 		return true;
    
    	 	default:
    	        return super.onOptionsItemSelected(item);
    	 }
	}
    
    private void updateRacksDatabase() {
    	DownloadFile downloadFile = new DownloadFile(this);
    	downloadFile.execute("racks.json", "http://data.cityofchicago.org/api/views/cbyb-69xx/rows.json");
	}
    
    private void createIntentMap(String address) {
		try {
			FileInputStream in = this.openFileInput("racks.json");
			in.close();
			Intent mapIntent = new Intent(that, MapActivity.class);
			mapIntent.putExtra("address", address);
			startActivityForResult(mapIntent, 0);
		} catch (FileNotFoundException e) {
			updateRacksDatabase();
		} catch (IOException e) {
			updateRacksDatabase();
		}
	}
}
