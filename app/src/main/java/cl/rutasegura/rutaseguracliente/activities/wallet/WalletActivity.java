package cl.rutasegura.rutaseguracliente.activities.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cl.rutasegura.rutaseguracliente.ConstantValues.ConstantsValues;
import cl.rutasegura.rutaseguracliente.DataProccessor.DataProccessor;
import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.maps.MapsActivity;
import cl.rutasegura.rutaseguracliente.databinding.ActivityWalletBinding;

public class WalletActivity extends AppCompatActivity {
 private ActivityWalletBinding binding;
 private TextView cardadded;
 DataProccessor dataProccessor = new DataProccessor(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityWalletBinding.inflate( getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        binding.getRoot().findViewById(R.id.layoutvisaclient).setOnClickListener(v->{
        startActivity(new Intent(WalletActivity.this,CardRegistredActivity.class));
        });

        binding.getRoot().findViewById(R.id.layoutagregartarjetas).setOnClickListener(v->{
            startActivity(new Intent(WalletActivity.this,AddCreditAndDebitCardActivity.class));
        });

      cardadded =  binding.getRoot().findViewById(R.id.tarjetayaagregada);
      cardadded.setText(DataProccessor.getStr(ConstantsValues.CARDNUMBER));
      binding.getRoot().findViewById(R.id.arrow_back).setOnClickListener(v->{
          startActivity(new Intent(WalletActivity.this, MapsActivity.class));
      });
    }
}