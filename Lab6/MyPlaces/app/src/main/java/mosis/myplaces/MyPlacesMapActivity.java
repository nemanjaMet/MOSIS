package mosis.myplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPlacesMapActivity extends AppCompatActivity {

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;

    private int state = 0;
    private boolean selCoorsEnabled = false;
    private  LatLng placeLoc;

    private GoogleMap map;

    private HashMap<Marker, Integer> markerPlaceIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Intent mapIntent = getIntent();
            Bundle mapBundle = mapIntent.getExtras();
            if (mapBundle != null)
            {
                state = mapBundle.getInt("state");
                if (state == CENTER_PLACE_ON_MAP)
                {
                    String placeLat = mapBundle.getString("lat");
                    String placeLon = mapBundle.getString("lon");
                    placeLoc = new LatLng(Double.parseDouble(placeLat), Double.parseDouble(placeLon));
                }
            }
        }
        catch (Exception ec)
        {
            Log.d("Error", "Error reading state");
        }

        setContentView(R.layout.activity_my_places_map);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (state == SHOW_MAP)
            map.setMyLocationEnabled(true);
        else if (state == SELECT_COORDINATES)
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (state == SELECT_COORDINATES && selCoorsEnabled)
                    {
                        String lon = Double.toString(latLng.longitude);
                        String lat = Double.toString(latLng.latitude);
                        Intent locationIntent = new Intent();
                        locationIntent.putExtra("lon", lon);
                        locationIntent.putExtra("lat", lat);
                        setResult(Activity.RESULT_OK, locationIntent);
                        finish();
                    }
                }
            });
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLoc, 15));

        addMyPlacesMarkers();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker){
                Intent intent= new Intent(MyPlacesMapActivity.this, ViewMyPlaceActivity.class);
                int i = markerPlaceIdMap.get(marker);
                intent.putExtra("positon", i);
                startActivity(intent);
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addMyPlacesMarkers()
    {
        ArrayList<MyPlace> places = MyPlacesData.getIntance().getMyPlaces();
        markerPlaceIdMap = new HashMap<Marker, Integer>((int)((double)places.size()*1.2));
        for (int i=0; i < places.size(); i++)
        {
            MyPlace place = places.get(i);
            String lat = place.getLatitude();
            String lon = place.getLongitude();
            LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_marker));
            markerOptions.title(place.getName());
            Marker marker = map.addMarker(markerOptions);
            markerPlaceIdMap.put(marker, i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (state == SELECT_COORDINATES && !selCoorsEnabled)
        {
            menu.add(0, 1, 1, "Select Coordinates");
            menu.add(0, 2, 2, "Cancel");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                selCoorsEnabled = true;
                Toast.makeText(this, "Select coordinates", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
