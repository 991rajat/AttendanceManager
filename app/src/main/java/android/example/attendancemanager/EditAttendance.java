package android.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.example.attendancemanager.Model.UsersSubject;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditAttendance extends AppCompatActivity {

    private Toolbar toolbar;
    private String UID;
    private ProgressBar pbar;
    private int pos;
    private EditText name,presnt,total;
    private DatabaseReference mroot;
    private Button cancel,submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);
        name = findViewById(R.id.editsubject);
        presnt = findViewById(R.id.editpresent);
        toolbar = findViewById(R.id.edittoolbar);
        total = findViewById(R.id.edittotal);
        cancel = findViewById(R.id.editcancel);
        submit = findViewById(R.id.editok);
        //pbar = findViewById(R.id.editprogress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Subject");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mroot = FirebaseDatabase.getInstance().getReference();
        final String subject = getIntent().getStringExtra("subject");
        pos = getIntent().getIntExtra("pos",-1);
        name.setText(subject);
        total.setText(String.valueOf(getIntent().getIntExtra("total",-1)));
        presnt.setText(String.valueOf(getIntent().getIntExtra("present",-1)));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    HashMap<String,Integer> map =new HashMap<>();
                    map.put("present",Integer.parseInt(presnt.getText().toString()));
                    map.put("total",Integer.parseInt(total.getText().toString()));
                    mroot.child("Users").child(UID).child(subject).removeValue();
                    mroot.child("Users").child(UID).child(name.getText().toString()).setValue(map);

                    MainActivity mainActivity = new MainActivity();
                    if(pos!=-1)
                    mainActivity.notifyrecyclerview(pos,name.getText().toString(),subject,Integer.parseInt(presnt.getText().toString()),Integer.parseInt(total.getText().toString()));
                    startActivity(new Intent(EditAttendance.this,MainActivity.class));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditAttendance.this,MainActivity.class));
            }
        });

    }

    private boolean validate() {
        boolean istrue = true;
        if(name.getText().toString().equals(""))
        {
            istrue=false;
            name.setError("Enter subject name.");
        }
        if(presnt.getText().toString().equals(""))
        {
            istrue=false;
            name.setError("Enter classes attended.");
        }
        if(total.getText().toString().equals(""))
        {
            istrue=false;
            name.setError("Enter total classes attended.");
        }
        return istrue;
    }
}
