package cl.rutasegura.rutaseguracliente.provider;


import cl.rutasegura.rutaseguracliente.model.FCMBody;
import cl.rutasegura.rutaseguracliente.model.FCMResponse;
import cl.rutasegura.rutaseguracliente.retrofit.IFCMApi;
import cl.rutasegura.rutaseguracliente.retrofit.RetrofitClient;
import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }
    
    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
