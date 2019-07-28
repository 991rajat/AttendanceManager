package android.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView already;
    private TextInputLayout email,password,confirm,naam;
    private Button submit;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.register_toolbar);
        already = findViewById(R.id.register_login);
        naam = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirm = findViewById(R.id.register_confirm_password);

        submit = findViewById(R.id.register);
        mAuth  = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate())
                {
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Hold it ryt there sparky!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final String name = email.getEditText().getText().toString();
                    final String pass = password.getEditText().getText().toString();
                    mAuth.createUserWithEmailAndPassword(name,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                SharedPreferences pref = getSharedPreferences("firsttime",MODE_PRIVATE);
                                pref.edit().putBoolean("firsttimeronly",true).commit();
                                Mytask mytask = new Mytask();
                                mytask.execute(naam.getEditText().getText().toString().trim());
                                Toast.makeText(RegisterActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                Log.d("register",name+pass);
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Log.d("registernot",name+pass);
                                Toast.makeText(RegisterActivity.this, "Something got wrong! Try again. Or User Already Present", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
            password.getEditText().requestFocus();
            password.getEditText().setError("Password must be 6 character long.");
            vali = false;
        }
        if(!confirm.getEditText().getText().toString().equals(password.getEditText().getText().toString()))
        {
            password.getEditText().requestFocus();
            confirm.getEditText().setError("Password didn't match.");
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

    public class Mytask extends AsyncTask<String,Void,Boolean> {

        private FirebaseDatabase database;
        private DatabaseReference myref;
        private String UID;

        @Override
        protected Boolean doInBackground(String... strings) {
            database = FirebaseDatabase.getInstance();
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            Log.d("UID",UID);
            myref = database.getReference();
            HashMap<String,Object> map = new HashMap<>();
            map.put("name",strings[0]);
            map.put("goal",0);
            map.put("overall",0);

            myref.child("Users").child(UID).setValue(map);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        }
    }
}
