package cl.rutasegura.rutaseguracliente.activities.historybooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.databinding.ActivityHistoryBookingDetailClientBinding;
import cl.rutasegura.rutaseguracliente.model.HistoryBooking;
import cl.rutasegura.rutaseguracliente.provider.DriverProvider;
import cl.rutasegura.rutaseguracliente.provider.HistoryBookingProvider;

public class HistoryBookingDetailClientActivity extends AppCompatActivity {
    private ActivityHistoryBookingDetailClientBinding binding;
    private String mExtraId;
    private HistoryBookingProvider mHistoryBookingProvider;
    private DriverProvider mDriverProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBookingDetailClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        mDriverProvider = new DriverProvider();
        mExtraId = getIntent().getStringExtra("idHistoryBooking");
        mHistoryBookingProvider = new HistoryBookingProvider();
        getHistoryBooking();

        binding.circleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getHistoryBooking() {
        mHistoryBookingProvider.getHistoryBooking(mExtraId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HistoryBooking historyBooking = dataSnapshot.getValue(HistoryBooking.class);
                    binding.textViewOriginHistoryBookingDetail.setText(historyBooking.getOrigin());
                    binding.textViewDestinationHistoryBookingDetail.setText(historyBooking.getDestination());
                    binding.textViewCalificationHistoryBookingDetail.setText("Tu calificacion: " + historyBooking.getCalificationDriver());
                    if (dataSnapshot.hasChild("calificationClient")) {
                        binding.ratingBarHistoryBookingDetail.setRating(historyBooking.getCalificationClient());
                    }

                    mDriverProvider.getDriver(historyBooking.getIdDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = dataSnapshot.child("firstname").getValue().toString();
                                binding.textViewNameBookingDetail.setText(name.toUpperCase());
                                if (dataSnapshot.hasChild("image")) {
                                    String image = dataSnapshot.child("image").getValue().toString();
                                    Picasso.with(HistoryBookingDetailClientActivity.this).load(image).into(binding.circleImageHistoryBookingDetail);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}