package cl.rutasegura.rutaseguracliente.provider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import cl.rutasegura.rutaseguracliente.model.Client;

public class ClientProvider {
    DatabaseReference database;

    public ClientProvider() {
        database = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client client){
        return database.child(client.getIdClient()).setValue(client);
    }
    public DatabaseReference getClient(String idClient){
        return database.child(idClient);
    }
   /* public Task<Void> update(Client client){
        Map<String,Object> map = new HashMap<>();
        map.put("name",client.getName());
        map.put("image",client.getImage());
        map.put("password",client.getPassword());
        map.put("password2",client.getPassword2());
        return database.child(client.getId()).updateChildren(map);
    }*/
}



