package cl.rutasegura.rutaseguracliente.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import cl.rutasegura.rutaseguracliente.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup = null;
    private LayoutInflater inflater = null;
    private HashMap<String, String> images = null;
    private Context ctxt = null;
    private Marker lastMarker = null;

    public PopupAdapter(Context ctxt, LayoutInflater inflater,
                        HashMap<String, String> images) {
        this.ctxt = ctxt;
        this.inflater = inflater;
        this.images = images;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        return (null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(final Marker marker) {
        if (popup == null) {
            popup = inflater.inflate(R.layout.popup, null);
        }

        if (lastMarker == null || !lastMarker.getId().equals(marker.getId())) {
            lastMarker = marker;

            TextView textViewTitle = popup.findViewById(R.id.title);
            CircleImageView circleImageIcon = popup.findViewById(R.id.icon);

            // Nombre del condcutor
            textViewTitle.setText(marker.getTitle());

            String image = images.get(marker.getTag().toString());

            if (Objects.equals(image, "")) {
                circleImageIcon.setImageResource(R.drawable.ic_baseline_person_24);
            }
            else {
                Picasso.with(ctxt).load(image).into(circleImageIcon, new MarkerCallback(marker));
            }

        }

        return (popup);
    }

    class MarkerCallback implements Callback {
        Marker marker = null;

        MarkerCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.showInfoWindow();
            }
        }
    }
}

