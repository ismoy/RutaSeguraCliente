package cl.rutasegura.rutaseguracliente.activities.historybooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.maps.MapsActivity;
import cl.rutasegura.rutaseguracliente.adapters.HistoryBookingClientAdapter;
import cl.rutasegura.rutaseguracliente.databinding.ActivityHistoryBookingClientBinding;
import cl.rutasegura.rutaseguracliente.model.HistoryBooking;
import cl.rutasegura.rutaseguracliente.provider.AuthProvider;

public class HistoryBookingClientActivity extends AppCompatActivity {
    private ActivityHistoryBookingClientBinding binding;
    private HistoryBookingClientAdapter mAdapter;
    private AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBookingClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewHistoryBooking.setLayoutManager(linearLayoutManager);
        binding.arrowBack.setOnClickListener(v->{
            startActivity(new Intent(HistoryBookingClientActivity.this, MapsActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuthProvider = new AuthProvider();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("HistoryBooking")
                .orderByChild("idClient")
                .equalTo(mAuthProvider.getId());
        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();
        mAdapter = new HistoryBookingClientAdapter(options, HistoryBookingClientActivity.this);

        binding.recyclerViewHistoryBooking.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}