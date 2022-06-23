package cl.rutasegura.rutaseguracliente.activities.wallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import cl.rutasegura.rutaseguracliente.ConstantValues.ConstantsValues;
import cl.rutasegura.rutaseguracliente.DataProccessor.DataProccessor;
import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.maps.MapsActivity;
import cl.rutasegura.rutaseguracliente.databinding.ActivityCardRegistredBinding;

public class CardRegistredActivity extends AppCompatActivity {
    private ActivityCardRegistredBinding binding;
    private DataProccessor dataProccessor = new DataProccessor(this);
    String cvccifrado ="***";
    private int mCounter = 25;
    private Handler mHandler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCounter = mCounter -1;
            binding.mcounter.setText(String.valueOf(mCounter));
            if (mCounter > 0) {
                initTimer();
                binding.codigoseguridadmask.setVisibility(View.GONE);
                binding.codigoseguridad.setVisibility(View.VISIBLE);
                binding.codigoseguridad.setText(DataProccessor.getStr(ConstantsValues.CVC));
                binding.numerotarjeta.setText(DataProccessor.getStr(ConstantsValues.CARDNUMBERNOMASK));
                binding.mcounter.setVisibility(View.VISIBLE);
            }
            else {
                binding.numerotarjeta.setText(DataProccessor.getStr(ConstantsValues.CARDNUMBER));
                binding.codigoseguridadmask.setVisibility(View.VISIBLE);
                binding.codigoseguridad.setVisibility(View.GONE);
                binding.codigoseguridad.setText(cvccifrado);
                binding.mcounter.setVisibility(View.GONE);
            }
        }
    };

    private void initTimer() {
        mHandler = new Handler();
        mHandler.postDelayed(runnable, 1000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardRegistredBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        binding.nombretarjeta.setText(DataProccessor.getStr(ConstantsValues.CARDHOLDERNAME));
        binding.fechavencimiento.setText(DataProccessor.getStr(ConstantsValues.EXPIRATIONDATE));
        binding.numerotarjeta.setText(DataProccessor.getStr(ConstantsValues.CARDNUMBER));
        binding.codigoseguridadmask.setText(cvccifrado);
        binding.codigoseguridadmask.setVisibility(View.VISIBLE);
        binding.codigoseguridad.setVisibility(View.GONE);
        binding.eliminartarjeta.setOnClickListener(v->{
            MessageAlert();
        });
        binding.actualizartarjeta.setOnClickListener(v->{
            MessageAlertActualizar();
        });
        binding.vercodigocvv.setOnClickListener(v->{
            if (Boolean.parseBoolean(String.valueOf(DataProccessor.getStr(ConstantsValues.CARDNUMBER).isEmpty()))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ver Info Tarjeta");
                builder.setMessage("No tienes tarjeta agregada no se puede ver los datos ");
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
                initTimer();
            }

        });
        binding.arrowBack.setOnClickListener(v->{
            startActivity(new Intent(CardRegistredActivity.this,WalletActivity.class));
        });
    }

    private void MessageAlertActualizar() {
        if (Boolean.parseBoolean(String.valueOf(DataProccessor.getStr(ConstantsValues.CARDNUMBER).isEmpty()))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Actualizar Tarjeta");
            builder.setMessage("No tienes tarjeta agregada no se puede Actualizar ");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            Intent intent = new Intent(CardRegistredActivity.this,AddCreditAndDebitCardActivity.class);
            startActivity(intent);
        }
    }

    private void MessageAlert() {
        if (Boolean.parseBoolean(String.valueOf(DataProccessor.getStr(ConstantsValues.CARDNUMBER).isEmpty()))){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Eliminar Tarjeta");
            builder.setMessage("No tienes tarjeta agregada no se puede Eliminar ");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            AlertDelete();
        }

    }
    public void AlertDelete() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
        alertDialog2.setTitle("estas seguro de eliminar la tarjeta?");
        alertDialog2.setMessage("Al aceptar se eliminara su tarjeta como metodo de pago ");
        alertDialog2.setPositiveButton("Si", (dialog , which) -> {
                    eliminarTarjeta();
        });
        alertDialog2.setNegativeButton("No" ,
                (dialog , which) -> {
                    dialog.dismiss();
                });
        alertDialog2.show();


    }

    private void eliminarTarjeta() {
        DataProccessor.clears();
        startActivity(new Intent(CardRegistredActivity.this, MapsActivity.class));
    }

}