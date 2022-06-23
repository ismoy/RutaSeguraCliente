package cl.rutasegura.rutaseguracliente.activities.wallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;

import cl.rutasegura.rutaseguracliente.ConstantValues.ConstantsValues;
import cl.rutasegura.rutaseguracliente.DataProccessor.DataProccessor;
import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.databinding.ActivityAddCreditAndDebitCardBinding;

public class AddCreditAndDebitCardActivity extends AppCompatActivity {
    private ActivityAddCreditAndDebitCardBinding binding;
    private AlertDialog.Builder builder;
    DataProccessor dataProccessor = new DataProccessor(this);
    String cifrado = "**** **** ****";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCreditAndDebitCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
    }

    private void initialization() {
        showCardForm();
        binding.btnagregar.setOnClickListener(v->{
            SaveCard();
        });
        binding.arrowBack.setOnClickListener(v->{
            startActivity(new Intent(AddCreditAndDebitCardActivity.this,WalletActivity.class));
        });
    }

    private void SaveCard() {
        if (binding.cardForm.isValid()){
            builder = new AlertDialog.Builder(AddCreditAndDebitCardActivity.this);
            builder.setTitle("Confirmar antes de agregar");
            builder.setMessage("Numero de la Tarjeta: " + binding.cardForm.getCardNumber() +"\n"+
                    "Nombre del Titular: " + binding.cardForm.getCardholderName() +"\"+"+
                    "Fecha Exp: " + binding.cardForm.getExpirationDateEditText().getText().toString() + "\" +"+
                    "CVV: " + binding.cardForm.getCvv());
            builder.setPositiveButton("Confirmar", (dialog, which) -> {
                dialog.dismiss();
                DataProccessor.setStr(ConstantsValues.CARDHOLDERNAME,binding.cardForm.getCardholderName());
                DataProccessor.setStr(ConstantsValues.CARDNUMBER,cifrado+binding.cardForm.getCardNumber().substring(binding.cardForm.getCardNumber().length() -4));
                DataProccessor.setStr(ConstantsValues.CARDNUMBERNOMASK,binding.cardForm.getCardNumber());
                DataProccessor.setStr(ConstantsValues.EXPIRATIONDATE,binding.cardForm.getExpirationMonth()+ "/"+binding.cardForm.getExpirationYear());
                DataProccessor.setStr(ConstantsValues.CVC,binding.cardForm.getCvv());
                Intent intent = new Intent(AddCreditAndDebitCardActivity.this,WalletActivity.class);
                startActivity(intent);
                Toast.makeText(AddCreditAndDebitCardActivity.this, "Tu Tarjeta fue Agregado Con Exito", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {
            SaveFailedCard();
        }
    }

    private void SaveFailedCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error!");
        builder.setMessage("Error al guardar su tarjeta");
        builder.setPositiveButton("OK",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCardForm() {
        binding.cardForm.cardRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .expirationRequired(true)
                .cvvRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .setup(AddCreditAndDebitCardActivity.this);
        binding.cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

    }
}