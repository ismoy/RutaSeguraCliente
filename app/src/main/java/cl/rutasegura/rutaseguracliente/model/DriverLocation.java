package cl.rutasegura.rutaseguracliente.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ISMOY BELIZAIRE on 15/06/2022.
 */
public class DriverLocation {
    String id;
    LatLng latlng;

    public DriverLocation() {
    }

    public DriverLocation(String id, LatLng latlng) {
        this.id = id;
        this.latlng = latlng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
