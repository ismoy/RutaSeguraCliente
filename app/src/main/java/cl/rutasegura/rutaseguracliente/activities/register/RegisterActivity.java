package cl.rutasegura.rutaseguracliente.activities.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import cl.rutasegura.rutaseguracliente.R;
import cl.rutasegura.rutaseguracliente.activities.login.LoginActivity;
import cl.rutasegura.rutaseguracliente.databinding.ActivityRegisterBinding;
import cl.rutasegura.rutaseguracliente.model.Client;
import cl.rutasegura.rutaseguracliente.provider.AuthProvider;
import cl.rutasegura.rutaseguracliente.provider.ClientProvider;
import cl.rutasegura.rutaseguracliente.utils.ValidateGeneral;


public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private ProgressDialog mProgressDialog;
    private AuthProvider mAuthProvider;
    private ValidateGeneral mValidateGeneral;
    private FirebaseAuth fAuth;
    private ClientProvider mClientProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mProgressDialog = new ProgressDialog(this);
        mAuthProvider = new AuthProvider();
        mValidateGeneral = new ValidateGeneral();
        mClientProvider = new ClientProvider();
        binding.btnRegister.setOnClickListener(view -> {
            validateOnclickButton();
        });
        validateRealTime();
        fAuth = FirebaseAuth.getInstance();
    }

    private void validateOnclickButton() {
        if (binding.firstname.getText().toString().isEmpty()) {
            binding.layoutfirstname.setHelperText(getString(R.string.error_empty));
            binding.btnRegister.setEnabled(false);
        }else if (!mValidateGeneral.validarletras(binding.firstname.getText().toString())){
            binding.layoutfirstname.setHelperText(getString(R.string.only_letter));
        }else  if (binding.lastname.getText().toString().isEmpty()) {
            binding.layoutlastname.setHelperText(getString(R.string.error_empty));
            binding.btnRegister.setEnabled(false);
        } else if (!mValidateGeneral.validarletras(binding.lastname.getText().toString())){
            binding.layoutlastname.setHelperText(getString(R.string.only_letter));
        }else if (binding.emaillogin.getText().toString().isEmpty()){
            binding.layoutemail.setHelperText(getString(R.string.error_empty));
            binding.btnRegister.setEnabled(false);
        }else if (!mValidateGeneral.validaremail(binding.emaillogin.getText().toString())){
            binding.layoutemail.setHelperText(getString(R.string.invalid_email));
            binding.btnRegister.setEnabled(false);
        }else if (binding.password.getText().toString().isEmpty()){
            binding.layoutpassword.setHelperText(getString(R.string.error_empty));
            binding.btnRegister.setEnabled(false);
        }else if (binding.password.getText().toString().length()<6){
            binding.layoutpassword.setHelperText(getString(R.string.short_passord));
            binding.btnRegister.setEnabled(false);
        }else  if (binding.confirmpassword.getText().toString().isEmpty()){
            binding.layoutconfirmpassword.setHelperText(getString(R.string.error_empty));
            binding.btnRegister.setEnabled(false);
        }else if (binding.confirmpassword.getText().toString().length()<6){
            binding.layoutconfirmpassword.setHelperText(getString(R.string.short_passord));
        }else if (!binding.confirmpassword.getText().toString().equals(binding.password.getText().toString())){
            binding.layoutconfirmpassword.setHelperText(getString(R.string.no_match_password));
        }else {
            binding.btnRegister.setEnabled(true);
            binding.layoutfirstname.setHelperText("");
            binding.layoutlastname.setHelperText("");
            binding.layoutemail.setHelperText("");
            binding.layoutpassword.setHelperText("");
            binding.layoutconfirmpassword.setHelperText("");
            clickRegister(binding.firstname.getText().toString(),binding.lastname.getText().toString(),
                    binding.emaillogin.getText().toString(),binding.password.getText().toString(), binding.confirmpassword.getText().toString());
        }

    }

    private void validateRealTime() {
        binding.firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.firstname.getText().toString().isEmpty()) {
                    binding.layoutfirstname.setHelperText(getString(R.string.error_empty));
                    binding.btnRegister.setEnabled(false);
                }else if (!mValidateGeneral.validarletras(binding.firstname.getText().toString())){
                    binding.layoutfirstname.setHelperText(getString(R.string.only_letter));
                }
                else {
                    binding.btnRegister.setEnabled(true);
                    binding.layoutfirstname.setHelperText("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.lastname.getText().toString().isEmpty()) {
                    binding.layoutlastname.setHelperText(getString(R.string.error_empty));
                    binding.btnRegister.setEnabled(false);
                } else if (!mValidateGeneral.validarletras(binding.lastname.getText().toString())){
                    binding.layoutlastname.setHelperText(getString(R.string.only_letter));
                }
                else {
                    binding.btnRegister.setEnabled(true);
                    binding.layoutlastname.setHelperText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.emaillogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              if (binding.emaillogin.getText().toString().isEmpty()){
                  binding.layoutemail.setHelperText(getString(R.string.error_empty));
                  binding.btnRegister.setEnabled(false);
              }else if (!mValidateGeneral.validaremail(binding.emaillogin.getText().toString())){
                  binding.layoutemail.setHelperText(getString(R.string.invalid_email));
                  binding.btnRegister.setEnabled(false);
              }
              else {
                  binding.layoutemail.setHelperText("");
                  binding.btnRegister.setEnabled(true);
              }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.password.getText().toString().isEmpty()){
                    binding.layoutpassword.setHelperText(getString(R.string.error_empty));
                    binding.btnRegister.setEnabled(false);
                }else if (binding.password.getText().toString().length()<6){
                    binding.layoutpassword.setHelperText(getString(R.string.short_passord));
                    binding.btnRegister.setEnabled(false);
                }
                else {
                    binding.layoutpassword.setHelperText("");
                    binding.btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.confirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.confirmpassword.getText().toString().isEmpty()){
                    binding.layoutconfirmpassword.setHelperText(getString(R.string.error_empty));
                    binding.btnRegister.setEnabled(false);
                }else if (binding.confirmpassword.getText().toString().length()<6){
                    binding.layoutconfirmpassword.setHelperText(getString(R.string.short_passord));
                }else if (!binding.confirmpassword.getText().toString().equals(binding.password.getText().toString())){
                    binding.layoutconfirmpassword.setHelperText(getString(R.string.no_match_password));
                }else {
                    binding.layoutconfirmpassword.setHelperText("");
                    binding.btnRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //method para registrar
    private void clickRegister(String firstname, String lastname, String email, String password, String confirmpassword) {
        mProgressDialog.setMessage(getString(R.string.Loading___));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        registers(firstname,lastname,email,password,confirmpassword);
     }

    private void registers(String firstname, String lastname, String email, String password, String confirmpassword) {
    mAuthProvider.register(email, password).addOnCompleteListener(task -> {
        mProgressDialog.dismiss();
        if (task.isSuccessful()) {
            String id =(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid();
            Client client = new Client(id,firstname,lastname,email,password,confirmpassword,"",1);
            RegisterClient(client);
            FirebaseUser user = fAuth.getCurrentUser();
            fAuth.setLanguageCode("es");
            assert user!=null;
            user.sendEmailVerification().addOnCompleteListener(task1 -> Toast.makeText(this, getString(R.string.Youraccounthasbeencreatedsuccessfully), Toast.LENGTH_LONG).show()).addOnFailureListener(e -> {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }else {
            Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
    }

    private void RegisterClient(Client client) {
        mClientProvider.create(client).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                mAuthProvider.logout();
            }else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}