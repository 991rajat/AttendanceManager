package android.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout email,password;
    TextView create;
    private ProgressDialog progressDialog;
    private Button submit;
    Toolbar toolbar;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        submit = findViewById(R.id.login);
        create = findViewById(R.id.login_create);
        progressDialog = new ProgressDialog(this);
        mauth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Hold it ryt there sparky!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final String name = email.getEditText().getText().toString();
                    final String pass = password.getEditText().getText().toString();
                    mauth.signInWithEmailAndPassword(name,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Log.d("login",name+pass);
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Log.d("loginnot",name+pass);
                            }
                        }
                    });
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    boolean validate()
    {
        // Validation Required
        boolean vali=true;
        if(!isEmail((TextInputEditText) email.getEditText()))
        {
            email.getEditText().setError("Enter Valid Email Address.");
            email.getEditText().requestFocus();
            vali = false;
        }
        if(isEmpty((TextInputEditText)password.getEditText())||password.getEditText().getText().length()<6)
        {
            password.getEditText().setError("Password must be 6 character long.");
            password.getEditText().requestFocus();
            vali = false;
        }
        return vali;
    }

    boolean isEmail(TextInputEditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(TextInputEditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
