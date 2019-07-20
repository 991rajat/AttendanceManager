package android.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView already;
    private TextInputLayout email,password,confirm;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.register_toolbar);
        already = findViewById(R.id.register_login);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirm = findViewById(R.id.register_confirm_password);
        submit = findViewById(R.id.register);
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
                if(!validate())
                {
                    Log.d("YEAH","YEAH");
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
            password.getEditText().setError("Password must be 6 character long.");
            password.getEditText().requestFocus();
        }
        if(!confirm.getEditText().getText().toString().equals(password.getEditText().getText().toString()))
        {
            confirm.getEditText().setError("Password didn't match.");
            password.getEditText().requestFocus();
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
