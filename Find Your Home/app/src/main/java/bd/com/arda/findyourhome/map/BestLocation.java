package bd.com.arda.findyourhome.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


//Get GPS location
public class BestLocation {

	private LocationManager locationManager;
	private double lat, lon;
	private Context context;

	public BestLocation(Context context) {
		this.context = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		setup();
	}

	private void setup() {
		Location gpsLocation = null;
		Location networkLocation = null;

		locationManager.removeUpdates(listener);
		gpsLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
		networkLocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

		if (gpsLocation != null && networkLocation != null) {
			Location myLocation = getBetterLocation(gpsLocation,
					networkLocation);
			setLocation(myLocation);
		} else if (gpsLocation != null) {
			setLocation(gpsLocation);
		} else if (networkLocation != null) {
			setLocation(networkLocation);
		} else {

		}
	}

	private Location getBetterLocation(Location newLocation,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > 60000;
		boolean isSignificantlyOlder = timeDelta < 60000;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		}
		return currentBestLocation;
	}

	private Location requestUpdateFromProvider(String provider) {
		Location location = null;
		if (locationManager.isProviderEnabled(provider)) {
			locationManager
					.requestLocationUpdates(provider, 1000, 1, listener);
			location = locationManager.getLastKnownLocation(provider);

		}
		if (locationManager.isProviderEnabled(provider) || location != null) {
			Log.d("GPS", "gps is enable");
		} else {
			Toast.makeText(context, provider + " is disable",
					Toast.LENGTH_SHORT).show();
		}
		return location;
	}

	LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			setLocation(location);
		}
	};

	private void setLocation(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
	}

	public double getlat() {
		return this.lat;
	}

	public double getLon() {
		return this.lon;
	}
}
