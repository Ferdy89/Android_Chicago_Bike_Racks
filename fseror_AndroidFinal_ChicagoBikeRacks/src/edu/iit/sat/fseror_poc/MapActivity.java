package edu.iit.sat.fseror_poc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity {
	
	private GoogleMap mMap;
	JSONArray racks;
	String addressString = "";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Get the address
	 	Bundle b = this.getIntent().getExtras();
	 	if (b != null && b.containsKey("address")) {
	 		addressString = b.getString("address");
	 		Log.i("Map", "Got address " + addressString);
	 	}

        // Get the data for the racks from the internal storage
        JSONArray parsedJSON = null;
        StringBuilder sb = null;
        try {
        	FileInputStream in = this.openFileInput("racks.json");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Read internal storage", "Done");
        
        // Parse the JSON read
        try {
			JSONObject rawJSON = new JSONObject(sb.toString());
			parsedJSON = rawJSON.getJSONArray("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        racks = parsedJSON;
        Log.i("Parse JSON", "Done");
        
        setUpMapIfNeeded();
    }
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
            			.findFragmentById(R.id.map))
                    	.getMap();
            
            if (mMap != null) {
            	mMap.getUiSettings().setMyLocationButtonEnabled(true);
            	mMap.setMyLocationEnabled(true);
            	
            	OnCameraChangeListener cameraChange = new OnCameraChangeListener() {
    				@Override
    				public void onCameraChange(CameraPosition arg0) {
    					loadRackMarkersInBounds();
    				}
    			};
                mMap.setOnCameraChangeListener(cameraChange);
            	
            	if (addressString.length() == 0) {
            		setLocationUpdate();
	            } else {
	            	Geocoder coder = new Geocoder(this);
	                List<Address> addressList;
	                Address location = null;

	                try {
	                	addressList = coder.getFromLocationName(addressString, 5);
	                    if (addressList != null) {
	        	            location = addressList.get(0);
	        	            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
	                    }
	                } catch (Exception e) {
	                	setLocationUpdate();
	                }
	            }
            }
        }
    }

	private void loadRackMarkersInBounds() {
		LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
		int increment = 0;
		
		Log.i("Bounds", bounds.toString());
		
		if (mMap.getCameraPosition().zoom < 15) {
			increment = (15 - (int)Math.floor(mMap.getCameraPosition().zoom)) * 10;
			Log.i("Zoom report", "Zoom " + mMap.getCameraPosition().zoom + " Increment " + increment);
		}
		
		for (int i = 0; i < racks.length(); i++) {
			JSONArray rack;
			try {
				rack = racks.getJSONArray(i);
				LatLng rackLatLng = new LatLng(rack.getDouble(14), rack.getDouble(15));
				
				if (bounds.contains(rackLatLng)) {
					MarkerOptions marker = new MarkerOptions();
					marker.position(rackLatLng);
					marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
					marker.title(rack.getString(9));
					marker.snippet(rack.getString(12));
			    	mMap.addMarker(marker);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i += increment;
		}
	}

	private void setLocationUpdate() {
		Log.i("Mode", "Location enabled");
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.i("Location update", location.toString());
				mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
				locationManager.removeUpdates(this);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
}