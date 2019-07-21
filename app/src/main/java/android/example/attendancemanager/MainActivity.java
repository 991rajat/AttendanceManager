package android.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.attendancemanager.Model.UsersSubject;
import android.example.attendancemanager.RecyclerAdapter.RecyclerSubjectAdapter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.example.attendancemanager.R.menu.maintoolbar_menu;
import static java.lang.Math.ceil;

public class MainActivity extends AppCompatActivity implements addsubjectDialog.ExampleDialogListener {

    private double answer=0;
    private int goal=75;
    private Toolbar toolbar;
    private Button addsubject;
    private DatabaseReference mroot;
    private String UID;
    private RecyclerView recyclerView;
    private TextView maingoal,mainoverall;
    private ArrayList<UsersSubject> subjectArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.maintoolbar);
        addsubject = findViewById(R.id.main_add_subject);
        mroot = FirebaseDatabase.getInstance().getReference();
        maingoal = findViewById(R.id.maingoal);
        mainoverall = findViewById(R.id.main_overall_attendance);
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recyclersubjects);
        subjectArrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");

        DrawerLayout drawer = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//                return false;
//            }
//        });

        addsubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });
        datafetch();
    }

    private void openDialog() {
        addsubjectDialog addsubjectDialog = new addsubjectDialog();
        addsubjectDialog.show(getSupportFragmentManager(), "example dialog");
        addsubjectDialog.setCancelable(false);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(maintoolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.signout:
                signOut();break;
        }
        return true;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }


    @Override
    public void getTexts(String subject) {
        HashMap<String,Integer> map = new HashMap<>();
        map.put("total",0);
        map.put("present",0);
        mroot.child("Users").child(UID).child(subject).setValue(map);
        Log.d("DATA IN","OKKKKKKK");
    }


    void datafetch()
    {
        mroot.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    UsersSubject usersSubject = new UsersSubject();
                    if(dataSnapshot1.getKey().equals("name")||dataSnapshot1.getKey().equals("goal")||dataSnapshot1.getKey().equals("overall"))
                        continue;
                    if(dataSnapshot1.getKey().equals("goal"))
                        goal = Integer.parseInt(dataSnapshot1.child("goal").getValue().toString());
                    usersSubject.setSubjectname(dataSnapshot1.getKey());
                    int present = Integer.parseInt(dataSnapshot1.child("present").getValue().toString());
                    int total = Integer.parseInt(dataSnapshot1.child("total").getValue().toString());
                    double values = ((double)present/total);
                    usersSubject.setPercentage(((double)present/total)*100);
                    if(values<0.75)
                        usersSubject.setStatus("Attend "+ String.valueOf((int)(ceil((double)(0.75*total)-present))) +" classes more.");
                    else
                        usersSubject.setStatus("You are right on track");
                    answer+=values;
                    usersSubject.setPresent(Integer.parseInt(dataSnapshot1.child("present").getValue().toString()));
                    usersSubject.setTotal(Integer.parseInt(dataSnapshot1.child("total").getValue().toString()));
                    subjectArrayList.add(usersSubject);
                }
                setGoalandOverall();
                Log.d("Data fetch","YOOO");
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                RecyclerSubjectAdapter recyclerSubjectAdapter = new RecyclerSubjectAdapter(MainActivity.this,subjectArrayList);
                recyclerView.setAdapter(recyclerSubjectAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setGoalandOverall()
    {
        mainoverall.setText(String.valueOf((double) Math.round(((answer/subjectArrayList.size())*100)* 100) / 100)+" %");
        maingoal.setText("  "+String.valueOf(goal)+ " %");
    }


}
