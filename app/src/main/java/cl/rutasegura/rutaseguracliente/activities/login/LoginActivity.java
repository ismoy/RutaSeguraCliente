package cl.rutasegura.rutaseguracliente.activities.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.maps.MapsActivity;
import cl.rutasegura.rutaseguracliente.activities.register.RegisterActivity;
import cl.rutasegura.rutaseguracliente.databinding.ActivityLoginBinding;
import cl.rutasegura.rutaseguracliente.provider.AuthProvider;
import cl.rutasegura.rutaseguracliente.provider.ClientProvider;
import cl.rutasegura.rutaseguracliente.utils.ValidateGeneral;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ValidateGeneral mValidateGeneral;
    private ProgressDialog mDialog;
    private AuthProvider mAuthProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gotoRegister();
        validateRealTime();
        mValidateGeneral = new ValidateGeneral();
        binding.btnLogin.setOnClickListener(view -> {
            validateOnclikButton();
        });
        mDialog = new ProgressDialog(this);
        mAuthProvider = new AuthProvider();
    }

    private void validateOnclikButton() {
        if (binding.emaillogin.getText().toString().isEmpty()){
            binding.layoutemaillogin.setHelperText(getString(R.string.error_empty));
            binding.btnLogin.setEnabled(false);
        }else if (!mValidateGeneral.validaremail(binding.emaillogin.getText().toString())){
            binding.layoutemaillogin.setHelperText(getString(R.string.invalid_email));
            binding.btnLogin.setEnabled(false);
        }else  if (binding.passwordlogin.getText().toString().isEmpty()){
            binding.layoutpasswordlogin.setHelperText(getString(R.string.error_empty));
            binding.btnLogin.setEnabled(false);
        }else if (binding.passwordlogin.getText().toString().length()<6){
            binding.layoutpasswordlogin.setHelperText(getString(R.string.short_passord));
            binding.btnLogin.setEnabled(false);
        }else {
            binding.layoutpasswordlogin.setHelperText("");
            binding.layoutemaillogin.setHelperText("");
            binding.btnLogin.setEnabled(true);
            LoginDriver(binding.emaillogin.getText().toString(),binding.passwordlogin.getText().toString());
        }
    }

    private void LoginDriver(String email,String password) {
        mDialog.setMessage(LoginActivity.this.getString(R.string.Loading___));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
        mAuthProvider.login(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (mAuthProvider.isVerified()){
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mDialog.dismiss();
                }else {
                    mDialog.dismiss();
                    Toast.makeText(this, "Por Favor verifica su correo para poder ingresar", Toast.LENGTH_SHORT).show();
                }
            }else {
                mDialog.dismiss();
                Toast.makeText(this, "Correo electronico o contraseña no se existe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateRealTime() {
        binding.emaillogin.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             if (binding.emaillogin.getText().toString().isEmpty()){
                 binding.layoutemaillogin.setHelperText(getString(R.string.error_empty));
                 binding.btnLogin.setEnabled(false);
             }else if (!mValidateGeneral.validaremail(binding.emaillogin.getText().toString())){
                 binding.layoutemaillogin.setHelperText(getString(R.string.invalid_email));
                 binding.btnLogin.setEnabled(false);
             }else {
                 binding.layoutemaillogin.setHelperText("");
                 binding.btnLogin.setEnabled(true);
             }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.passwordlogin.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.passwordlogin.getText().toString().isEmpty()){
                    binding.layoutpasswordlogin.setHelperText(getString(R.string.error_empty));
                    binding.btnLogin.setEnabled(false);
                }else if (binding.passwordlogin.getText().toString().length()<6){
                    binding.layoutpasswordlogin.setHelperText(getString(R.string.short_passord));
                    binding.btnLogin.setEnabled(false);
                }
                else {
                    binding.layoutpasswordlogin.setHelperText("");
                    binding.btnLogin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void gotoRegister() {
        binding.gotoregister.setOnClickListener(view -> {
         startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}