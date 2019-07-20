package android.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout email,password;
    TextView create;

    private Button submit;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        submit = findViewById(R.id.login);
        create = findViewById(R.id.login_create);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    Toast.makeText(LoginActivity.this, "VALID", Toast.LENGTH_SHORT).show();
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
