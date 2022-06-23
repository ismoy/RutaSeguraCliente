package cl.rutasegura.rutaseguracliente.activities.detailrequestactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cl.rutasegura.rutaseguracliente.ConstantValues.ConstantsValues;
import cl.rutasegura.rutaseguracliente.DataProccessor.DataProccessor;
import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.requestdriver.RequestDriverActivity;
import cl.rutasegura.rutaseguracliente.activities.requestdriver.RequestDriverByIdActivity;
import cl.rutasegura.rutaseguracliente.activities.wallet.AddCreditAndDebitCardActivity;
import cl.rutasegura.rutaseguracliente.databinding.ActivityDetailRequestBinding;
import cl.rutasegura.rutaseguracliente.model.Info;
import cl.rutasegura.rutaseguracliente.provider.GoogleApiProvider;
import cl.rutasegura.rutaseguracliente.provider.InfoProvider;
import cl.rutasegura.rutaseguracliente.utils.DecodePoints;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityDetailRequestBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private String mExtraOrigin;
    private String mExtraDestination;
    private double mExtraDriverLat;
    private double mExtraDriverLng;
    private String mExtraDriverId;
    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;

    private GoogleApiProvider mGoogleApiProvider;
    private InfoProvider mInfoProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;
    private DataProccessor dataProccessor = new DataProccessor(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraDriverId = getIntent().getStringExtra("idDriver");
        mExtraDriverLat = getIntent().getDoubleExtra("driver_lat", 0);
        mExtraDriverLng = getIntent().getDoubleExtra("driver_lng", 0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGoogleApiProvider = new GoogleApiProvider(DetailRequestActivity.this);
        mInfoProvider = new InfoProvider();
        binding.textViewOrigin.setText(mExtraOrigin);
        binding.textViewDestination.setText(mExtraDestination);
        binding.btnRequestNow.setOnClickListener(view -> {
            // QUEREMOS ENVIARLE LA NOTIFICACION A UN CONDUCTOR ESPECIFICO
            if (Boolean.parseBoolean(String.valueOf(DataProccessor.getStr(ConstantsValues.CARDNUMBER).isEmpty()))){
                AlertMetodopago();
            }else {
                if (mExtraDriverId != null) {
                    goToRequestDriverById();
                }
                else {
                    goToRequestDriver();
                }
            }

        });
        binding.circleImageBack.setOnClickListener(view -> finish());
    }

    private void goToRequestDriverById() {
        Intent intent = new Intent(DetailRequestActivity.this, RequestDriverByIdActivity.class);
        intent.putExtra("origin_lat", mOriginLatLng.latitude);
        intent.putExtra("origin_lng", mOriginLatLng.longitude);
        intent.putExtra("origin", mExtraOrigin);
        intent.putExtra("destination", mExtraDestination);
        intent.putExtra("destination_lat", mDestinationLatLng.latitude);
        intent.putExtra("destination_lng", mDestinationLatLng.longitude);
        intent.putExtra("idDriver", mExtraDriverId);
        intent.putExtra("driver_lat", mExtraDriverLat);
        intent.putExtra("driver_lng", mExtraDriverLng);
        startActivity(intent);
        finish();
    }


    private void goToRequestDriver() {
        Intent intent = new Intent(DetailRequestActivity.this, RequestDriverActivity.class);
        intent.putExtra("origin_lat", mOriginLatLng.latitude);
        intent.putExtra("origin_lng", mOriginLatLng.longitude);
        intent.putExtra("origin", mExtraOrigin);
        intent.putExtra("destination", mExtraDestination);
        intent.putExtra("destination_lat", mDestinationLatLng.latitude);
        intent.putExtra("destination_lng", mDestinationLatLng.longitude);
        startActivity(intent);
        finish();
    }

    private void AlertMetodopago() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error!");
        builder.setMessage("Debes Agregar un metodo de pago");
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("Agregar Tarjeta", (dialog, which) -> {
            Intent intent = new Intent(DetailRequestActivity.this, AddCreditAndDebitCardActivity.class);
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void drawRoute() {
        mGoogleApiProvider.getDirections(mOriginLatLng, mDestinationLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);
                    JSONArray legs =  route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    binding.textViewTime.setText(durationText + " " +distanceText);
                    String [] distanceAndKm = distanceText.split(" ");
                    double distancevalue = Double.parseDouble(distanceAndKm[0]);
                    String [] durationAndMins = durationText.split(" ");
                    double durationvalue = Double.parseDouble(durationAndMins[0]);
                    calculatePrice(distancevalue,durationvalue);

                } catch(Exception e) {
                    Log.d("Error", "mOriginLat " + mOriginLatLng.latitude);
                    Log.d("Error", "mOriginLng " + mOriginLatLng.longitude);
                    Log.d("Error", "mDestinationLat " + mDestinationLatLng.latitude);
                    Log.d("Error", "mDestinationLng " + mDestinationLatLng.longitude);
                    Log.d("Error", "Error encontrado " + e.getMessage());
                    Toast.makeText(DetailRequestActivity.this, "Error encontrado: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void calculatePrice(final double distanceValue, final double durationValue) {
        mInfoProvider.getInfo().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Info info = dataSnapshot.getValue(Info.class);
                    double totalDistance = distanceValue * info.getKm();
                    double totalDuration = durationValue * info.getMin();
                    double total = totalDistance + totalDuration;
                    double minTotal = total - 500;
                    double maxTotal = total + 500;
                    binding.textViewPrice.setText(minTotal + " - " + maxTotal + " CLP");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mOriginLatLng)
                        .zoom(15f)
                        .build()
        ));

        drawRoute();
    }
}