package android.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout email,password;
    Button submit;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        submit = findViewById(R.id.login);
        setActionBar(toolbar);
        getActionBar().setTitle("Log In");
        //BACK button not working
        getActionBar().setDisplayHomeAsUpEnabled(true);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate())
                {

                }
            }
        });
    }

    boolean validate()
    {
        // Validation Required
        return true;

    }
}
