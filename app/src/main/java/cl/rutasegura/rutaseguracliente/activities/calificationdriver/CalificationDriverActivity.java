package cl.rutasegura.rutaseguracliente.activities.calificationdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.maps.MapsActivity;
import cl.rutasegura.rutaseguracliente.databinding.ActivityCalificationDriverBinding;
import cl.rutasegura.rutaseguracliente.model.ClientBooking;
import cl.rutasegura.rutaseguracliente.model.HistoryBooking;
import cl.rutasegura.rutaseguracliente.provider.AuthProvider;
import cl.rutasegura.rutaseguracliente.provider.ClientBookingProvider;
import cl.rutasegura.rutaseguracliente.provider.HistoryBookingProvider;

public class CalificationDriverActivity extends AppCompatActivity {
    private ActivityCalificationDriverBinding binding;
    private ClientBookingProvider mClientBookingProvider;
    private AuthProvider mAuthProvider;
    private HistoryBooking mHistoryBooking;
    private HistoryBookingProvider mHistoryBookingProvider;

    private float mCalification = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalificationDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        mClientBookingProvider = new ClientBookingProvider();
        mHistoryBookingProvider = new HistoryBookingProvider();
        mAuthProvider = new AuthProvider();

        binding.ratingbarCalification.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calification, boolean b) {
                mCalification = calification;
            }
        });
        binding.btnCalification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calificate();
            }
        });
        getClientBooking();
    }

    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ClientBooking clientBooking = dataSnapshot.getValue(ClientBooking.class);
                    binding.textViewOriginCalification.setText(clientBooking.getOrigin());
                    binding.textViewPrice.setText(String.format("%.1f", clientBooking.getPrice())  + "$");


                    binding.textViewDestinationCalification.setText(clientBooking.getDestination());
                    mHistoryBooking = new HistoryBooking(
                            clientBooking.getIdHistoryBooking(),
                            clientBooking.getIdClient(),
                            clientBooking.getIdDriver(),
                            clientBooking.getDestination(),
                            clientBooking.getOrigin(),
                            clientBooking.getTime(),
                            clientBooking.getKm(),
                            clientBooking.getStatus(),
                            clientBooking.getOriginLat(),
                            clientBooking.getOriginLng(),
                            clientBooking.getDestinationLat(),
                            clientBooking.getDestinationLng(),
                            Math.round(clientBooking.getPrice())
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calificate() {
        if (mCalification > 0) {
            mHistoryBooking.setCalificationDriver(mCalification);
            mHistoryBooking.setTimestamp(new Date().getTime());
            mHistoryBookingProvider.getHistoryBooking(mHistoryBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mHistoryBookingProvider.updateCalificactionDriver(mHistoryBooking.getIdHistoryBooking(), mCalification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationDriverActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CalificationDriverActivity.this, MapsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        mHistoryBookingProvider.create(mHistoryBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationDriverActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CalificationDriverActivity.this, MapsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            Toast.makeText(this, "Debes ingresar la calificacion", Toast.LENGTH_SHORT).show();
        }
    }
}