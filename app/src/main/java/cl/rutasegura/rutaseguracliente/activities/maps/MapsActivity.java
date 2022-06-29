package cl.rutasegura.rutaseguracliente.activities.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cl.rutasegura.rutaseguracliente.ConstantValues.ConstantsValues;
import cl.rutasegura.rutaseguracliente.DataProccessor.DataProccessor;
import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.detailrequestactivity.DetailRequestActivity;
import cl.rutasegura.rutaseguracliente.activities.historybooking.HistoryBookingClientActivity;
import cl.rutasegura.rutaseguracliente.activities.login.LoginActivity;
import cl.rutasegura.rutaseguracliente.activities.wallet.WalletActivity;
import cl.rutasegura.rutaseguracliente.adapters.PopupAdapter;
import cl.rutasegura.rutaseguracliente.databinding.ActivityMapsBinding;
import cl.rutasegura.rutaseguracliente.model.DriverLocation;
import cl.rutasegura.rutaseguracliente.provider.AuthProvider;
import cl.rutasegura.rutaseguracliente.provider.ClientBookingProvider;
import cl.rutasegura.rutaseguracliente.provider.ClientProvider;
import cl.rutasegura.rutaseguracliente.provider.DriverProvider;
import cl.rutasegura.rutaseguracliente.provider.GeofireProvider;
import cl.rutasegura.rutaseguracliente.provider.TokenProvider;
import cl.rutasegura.rutaseguracliente.utils.CarMoveAnim;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityMapsBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    Dialog dialog;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private GeofireProvider mGeofireProvider;
    private TokenProvider mTokenProvider;
    private ClientBookingProvider mClientBookingProvider;
    private DriverProvider mDriverProvider;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;

    private LatLng mCurrentLatLng;

    private List<Marker> mDriversMarkers = new ArrayList<>();

    private boolean mIsFirstTime = true;

    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutocomplete;
    private AutocompleteSupportFragment mAutocompleteDestination;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private Button mButtonRequestDriver;
    private ImageView mImageViewChange;
    LocationManager mLocationManager;

    private boolean mOriginSelect = true;

    private HashMap<String, String> mImagesMarkers = new HashMap<String, String>();
    private int mCounter = 0;
    SharedPreferences mPref;
    Toolbar toolbar;
    private ClientProvider mClientProvider;
    private ArrayList<DriverLocation> mDriversLocation = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private int role;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mIsFirstTime) {
                        mIsFirstTime = false;

                        // COLOCA AQUI EL MOVE CAMERA PARA QUE SOLO SE ACTUALIZE LA POSICION DEL MAPA UNA SOLA VEZ
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(16f)
                                        .build()
                        ));
                        getActiveDrivers();
                        limitSearch();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeofireProvider("active_drivers");
        mTokenProvider = new TokenProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mDriverProvider = new DriverProvider();
        mClientProvider = new ClientProvider();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        dialog = new Dialog(this);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        instanceAutocompleteOrigin();
        instanceAutocompleteDestination();
        onCameraMove();
        deleteClientBooking();
        mGoogleApiClient = getAPIClientInstance();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


        mPref = getApplicationContext().getSharedPreferences("RideStatus", MODE_PRIVATE);
        String status = mPref.getString("status", "");
        String idDriver = mPref.getString("idDriver", "");

        if (status.equals("ride") || status.equals("start")) {
            goToMapDriverBookingActivity(idDriver);
        } else {
            onCameraMove();
            deleteClientBooking();
        }
        binding.getRoot().findViewById(R.id.btnRequestDriver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDriver();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        senDataInDrawable();

        toggle.syncState();
        NavigationView navigationView = binding.navView;

        navigationView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.nav_incio:
                    Toast.makeText(this, "soy hinicio", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.history:
                    startActivity(new Intent(MapsActivity.this, HistoryBookingClientActivity.class));
                    break;
                case R.id.nav_wallet:
                    startActivity(new Intent(MapsActivity.this, WalletActivity.class));
                    break;
                case R.id.nav_logout:
                    logout();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private void goToMapDriverBookingActivity(String idDriver) {
        Intent intent = new Intent(MapsActivity.this, MapsClientBookingActivity.class);
        intent.putExtra("idDriver", idDriver);
        startActivity(intent);
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        return googleApiClient;
    }

    private void deleteClientBooking() {
        mClientBookingProvider.delete(mAuthProvider.getId());
    }

    private void requestDriver() {

        if (mOriginLatLng != null && mDestinationLatLng != null) {
            Intent intent = new Intent(MapsActivity.this, DetailRequestActivity.class);
            intent.putExtra("origin_lat", mOriginLatLng.latitude);
            intent.putExtra("origin_lng", mOriginLatLng.longitude);
            intent.putExtra("destination_lat", mDestinationLatLng.latitude);
            intent.putExtra("destination_lng", mDestinationLatLng.longitude);
            intent.putExtra("origin", mOrigin);
            intent.putExtra("destination", mDestination);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Debe seleccionar el lugar de recogida y el destino", Toast.LENGTH_SHORT).show();
        }

    }

    private void senDataInDrawable() {
        View mHeaderView = binding.navView.getHeaderView(0);
        TextView username = mHeaderView.findViewById(R.id.textviewnamedrwawerclient);
        TextView emails = mHeaderView.findViewById(R.id.textviewcorreodrawerclient);
        mClientProvider.getClient(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstname = snapshot.child("firstname").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    username.setText(firstname);
                    emails.setText(email);
                    generateToken();
                }else {
                    ShowAlertDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void limitSearch() {
        LatLng northSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000, 180);
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
        mAutocompleteDestination.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
    }

    private void onCameraMove() {
        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    if (mOriginSelect) {
                        mOriginLatLng = mMap.getCameraPosition().target;
                        List<Address> addressList = geocoder.getFromLocation(mOriginLatLng.latitude, mOriginLatLng.longitude, 1);
                        String city = addressList.get(0).getLocality();
                        String country = addressList.get(0).getCountryName();
                        String address = addressList.get(0).getAddressLine(0);
                        mOrigin = address + " " + city;
                        mAutocomplete.setText(address + " " + city);
                    } else {
                        mDestinationLatLng = mMap.getCameraPosition().target;
                        List<Address> addressList = geocoder.getFromLocation(mDestinationLatLng.latitude, mDestinationLatLng.longitude, 1);
                        String city = addressList.get(0).getLocality();
                        String country = addressList.get(0).getCountryName();
                        String address = addressList.get(0).getAddressLine(0);
                        mDestination = address + " " + city;
                        mAutocompleteDestination.setText(address + " " + city);
                    }


                } catch (Exception e) {
                    Log.d("Error: ", "Mensaje error: " + e.getMessage());
                }
            }
        };
    }

    private void instanceAutocompleteOrigin() {
        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeautocompleteorigin);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        //mAutocomplete.setTypeFilter(TypeFilter.ADDRESS);
        mAutocomplete.setHint("Lugar de recogida");
        mAutocomplete.setCountry("CL");
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLatLng = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }

    private void instanceAutocompleteDestination() {
        mAutocompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeautocompletedestino);
        mAutocompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        mAutocompleteDestination.setHint("Destino");
        mAutocompleteDestination.setCountry("CL");
        mAutocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestination = place.getName();
                mDestinationLatLng = place.getLatLng();
                Log.d("PLACE", "Name: " + mDestination);
                Log.d("PLACE", "Lat: " + mDestinationLatLng.latitude);
                Log.d("PLACE", "Lng: " + mDestinationLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }

    private void getActiveDrivers() {

        mGeofireProvider.getActiveDrivers(mCurrentLatLng, 10).addGeoQueryEventListener(new GeoQueryEventListener() {
            @SuppressLint("PotentialBehaviorOverride")
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // AÑADIREMOS LOS MARCADORES DE LOS CONDUCTORES QUE SE CONECTEN EN LA APLICACION
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            return;
                        }
                    }
                }

                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                marker.setTag(key);
                mDriversMarkers.add(marker);
                DriverLocation driverLocation = new DriverLocation();
                driverLocation.setId(key);
                mDriversLocation.add(driverLocation);
                getDriversInfo();
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (mOriginLatLng != null && mDestinationLatLng != null) {
                            Intent intent = new Intent(MapsActivity.this, DetailRequestActivity.class);
                            intent.putExtra("origin_lat", mOriginLatLng.latitude);
                            intent.putExtra("origin_lng", mOriginLatLng.longitude);
                            intent.putExtra("destination_lat", mDestinationLatLng.latitude);
                            intent.putExtra("destination_lng", mDestinationLatLng.longitude);
                            intent.putExtra("origin", mOrigin);
                            intent.putExtra("destination", mDestination);
                            intent.putExtra("idDriver", marker.getTag().toString());
                            intent.putExtra("driver_lat", marker.getPosition().latitude);
                            intent.putExtra("driver_lng", marker.getPosition().longitude);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MapsActivity.this, "Debe seleccionar el lugar de recogida y el destino", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            mDriversLocation.remove(getPositionDriver(key));
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // ACTUALIZAR LA POSICION DE CADA CONDUCTOR
                for (Marker marker : mDriversMarkers) {
                    LatLng start = new LatLng(location.latitude,location.longitude);
                    LatLng end = null;
                    int position = getPositionDriver(marker.getTag().toString());
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                        if (mDriversLocation.get(position).getLatlng() !=null){
                            end = mDriversLocation.get(position).getLatlng();
                        }
                        mDriversLocation.get(position).setLatlng(new LatLng(location.latitude,location.longitude));
                        if (end!=null){
                            CarMoveAnim.carAnim(marker,end,start);
                        }
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getDriversInfo() {
        mCounter = 0;

        for (final Marker marker : mDriversMarkers) {
            mDriverProvider.getDriver(marker.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCounter = mCounter + 1;

                    if (snapshot.exists()) {
                        if (snapshot.hasChild("firstname")) {
                            String name = snapshot.child("firstname").getValue().toString();
                            marker.setTitle(name);
                        }
                        if (snapshot.hasChild("image")) {
                            String image = snapshot.child("image").getValue().toString();
                            mImagesMarkers.put(marker.getTag().toString(), image);
                        } else {
                            mImagesMarkers.put(marker.getTag().toString(), null);
                        }
                    }

                    // TERMINO DE TRAER TODA LA INFORMACION DE LOS CONDUCTORES
                    if (mCounter == mDriversMarkers.size()) {
                        mMap.setInfoWindowAdapter(new PopupAdapter(MapsActivity.this, getLayoutInflater(), mImagesMarkers));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private int getPositionDriver(String id){
        int position = 0;
        for (int i = 0;i<mDriversLocation.size();i++){
            if (id.equals(mDriversLocation.get(i).getId())){
                position = i;
                break;
            }
        }
        return position;
    }

    @Override

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraIdleListener(mCameraListener);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        } else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()) {
            showAlertDialogNOGPS();
        }
    }


    private void showAlertDialogNOGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                } else {
                    showAlertDialogNOGPS();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                showAlertDialogNOGPS();
            }
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }


    void logout() {
        // ELIMINAR EL TOKEN
        mTokenProvider.deleteToken(mAuthProvider.getId());
        mAuthProvider.logout();
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    void generateToken() {
        mTokenProvider.create(mAuthProvider.getId());
    }

   private void ShowAlertDialog(){
       dialog.setContentView(R.layout.alert_dialog);
       dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       dialog.setCancelable(false);
       dialog.setCanceledOnTouchOutside(false);
       Button btncontinue = dialog.findViewById(R.id.acceptreport);
       btncontinue.setOnClickListener(v -> {
           startActivity(new Intent(this,LoginActivity.class));
           dialog.dismiss();
           mAuthProvider.logout();
       });
       dialog.show();
   }
   }
