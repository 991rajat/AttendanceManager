package android.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //Firebase Authentication if current user is not present go to home
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            // Activity will run only one time
            Intent intent = new Intent(MainActivity.this,StartActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
