package android.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.attendancemanager.Model.UsersSubject;
import android.example.attendancemanager.RecyclerAdapter.RecyclerSubjectAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity {

    private int answer1=0,answer2=0;
    private ValueEventListener mgoallistner,moveralllistener;
    private TextView name;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences mainpref;
    private ProgressBar contentLoadingProgressBar,cardprogress;
    private int goal=0;
    private Toolbar toolbar;
    private Button addsubject;
    private DatabaseReference mroot;
    private String UID;
    private RecyclerView recyclerView;
    private TextView maingoal,mainoverall;
    private  RecyclerSubjectAdapter recyclerSubjectAdapter;
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
        contentLoadingProgressBar = findViewById(R.id.progress_circular);
        cardprogress = findViewById(R.id.cardprogress);
        recyclerView = findViewById(R.id.recyclersubjects);
        mainpref = PreferenceManager.getDefaultSharedPreferences(this);

        subjectArrayList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");

        DrawerLayout drawer = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        name = navigationView.getHeaderView(0).findViewById(R.id.drawername);
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
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            // Activity will run only one time
            Intent intent = new Intent(MainActivity.this,StartActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private void otherfetch() {
        mroot.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("overalllpr",dataSnapshot.child("overall").getValue().toString());
                mainoverall.setText(dataSnapshot.child("overall").getValue().toString()+" %");
                maingoal.setText(dataSnapshot.child("goal").getValue().toString()+" %");
                goal = Integer.parseInt(dataSnapshot.child("goal").getValue().toString());
                name.setText(dataSnapshot.child("name").getValue().toString());
                cardprogress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Firebase Authentication if current user is not present go to home
    @Override
    protected void onStart() {
        super.onStart();

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Runs only one time for adding goal
        final SharedPreferences prefrence = getSharedPreferences("firsttime", Context.MODE_PRIVATE);
        boolean bool = prefrence.getBoolean("firsttimeronly",false);
        if(bool)
        {
            prefrence.edit().putBoolean("firsttimeronly",false).apply();
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final View dialog = getLayoutInflater().inflate(R.layout.firsttimer,null);
            final TextView seekbartext= dialog.findViewById(R.id.seekbartext);
            SeekBar seekBar = dialog.findViewById(R.id.seekbar);
            Button btn = dialog.findViewById(R.id.seekbarsubmit);
            builder.setView(dialog);
            builder.setCancelable(false);
            final AlertDialog alert = builder.create();
            final HashMap<String,Object> map = new HashMap<>();
            map.put("overall",0.0);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekbartext.setText(String.valueOf(progress)+" %");
                    map.put("goal",(double)progress);
                    goal = progress;

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("goal",String.valueOf(goal));
                    mroot.child("Users").child(UID).updateChildren(map);otherfetch();recyclerfetch();
                    alert.dismiss();

                }
            });alert.show();
        }




        //Event Listners
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("overalllpre",dataSnapshot.getValue().toString());
                mainoverall.setText(dataSnapshot.getValue().toString()+" %");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

//        ValueEventListener eventListener1 = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getKey().equals("goal"))
//                    goal = (int) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

        mroot.child("Users").child(UID).child("overall").addValueEventListener(eventListener);
//        mroot.child("Users").child(UID).addListenerForSingleValueEvent(eventListener1);
//        mgoallistner = eventListener1;
        moveralllistener = eventListener;
        otherfetch();

        recyclerfetch();



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





    //Open Dialog for adding subject
    private void openDialog() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_subject_dialog,null);
        final EditText editText = view.findViewById(R.id.dialog_add_subject);
        Button ok,cancel;
        ok = view.findViewById(R.id.addsubjectsubmmit);
        cancel = view.findViewById(R.id.cancelsubject);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                updateSubject(editText.getText().toString().trim());

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });alert.show();
    }



    //UPDATE SUBJECT
    public void updateSubject(String subject) {
        HashMap<String,Integer> map = new HashMap<>();
        map.put("total",0);
        map.put("present",0);
        mroot.child("Users").child(UID).child(subject).setValue(map);
        UsersSubject usersSubject = new UsersSubject();
        usersSubject.setSubjectname(subject);
        usersSubject.setTotal(0);
        usersSubject.setGoal(goal);
        usersSubject.setPresent(0);
        usersSubject.setPercentage(0);
        usersSubject.setStatus("You are right on track");
        subjectArrayList.add(usersSubject);
        recyclerSubjectAdapter.notifyItemInserted(subjectArrayList.size()-1);
        recyclerSubjectAdapter.notifyDataSetChanged();
        recyclerView.invalidate();

    }


    //Sign Out
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        mroot.child("Users").child(UID).child("overall").removeEventListener(moveralllistener);
        //mroot.child("Users").child(UID).removeEventListener(mgoallistner);
        //mainpref.unregisterOnSharedPreferenceChangeListener(listener);
        super.onStop();

    }
    void recyclerfetch()
    {
        //        Log.d("UserID",UID);
        mroot.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    UsersSubject usersSubject = new UsersSubject();
                    if(dataSnapshot1.getKey().equals("name")||dataSnapshot1.getKey().equals("goal")||dataSnapshot1.getKey().equals("overall"))
                        continue;
                    usersSubject.setSubjectname(dataSnapshot1.getKey());
                    int present = Integer.parseInt(dataSnapshot1.child("present").getValue().toString());
                    int total = Integer.parseInt(dataSnapshot1.child("total").getValue().toString());
                    double values;
                    if(total==0)
                        values=0;
                    else
                        values = ((double)present/total);
                    usersSubject.setPercentage(values*100);
                    usersSubject.setGoal(goal);
                    double comp = (double) goal/100;
                    if(values<comp && total!=0)
                        usersSubject.setStatus("Attend "+ String.valueOf((int)(ceil((double)(comp*total)-present))) +" classes more.");
                    else
                        usersSubject.setStatus("You are right on track");
                    answer1+=present;
                    answer2+=total;
                    Log.d("anser",String.valueOf(answer1));
                    usersSubject.setPresent(present);
                    usersSubject.setTotal(total);
                    subjectArrayList.add(usersSubject);
                }
                if(answer2==0) {
                    Log.d("OY","haan");
                    mainoverall.setText(String.valueOf((double) Math.round(((0) * 100) * 100) / 100) + " %");
                    mainpref.edit().putFloat("overallpercentage",(float) Math.round(((0)*100)* 100) / 100);
                }
                else{
                    Log.d("OY",String.valueOf((double) Math.round((((double)answer1/answer2)*100)* 100) / 100));
                    mainoverall.setText(String.valueOf((double) Math.round((((double)answer1/answer2)*100)* 100) / 100)+" %");
                    mainpref.edit().putFloat("overallpercentage",(float) Math.round(((answer1/answer2)*100)* 100) / 100);
                }
                mainpref.edit().putInt("pres",answer1).apply();
                mainpref.edit().putInt("tot",answer2).apply();
                //mainpref.edit().putFloat("overallpercentage",(float) Math.round(((answer1/answer2)*100)* 100) / 100);
                Log.d("Data fetch","YOOO");
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerSubjectAdapter = new RecyclerSubjectAdapter(MainActivity.this,subjectArrayList);
                recyclerView.setAdapter(recyclerSubjectAdapter);
                contentLoadingProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }

    public void notifyrecyclerview(int pos,final String newsubj,final String prevsubj,final int newpres,final int newtot) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //Substract Previous Values
        int mainpres = pref.getInt("pres",-1);
        int maintot = pref.getInt("tot",-1);
        double overll;
        int present,total;
        present = subjectArrayList.get(pos).getPresent();
        total = subjectArrayList.get(pos).getTotal();
        mainpres-=present;
        maintot-=total;

        // Add new Values
        subjectArrayList.get(pos).setSubjectname(newsubj);
        subjectArrayList.get(pos).setTotal(newtot);
        subjectArrayList.get(pos).setPresent(newpres);
        present = subjectArrayList.get(pos).getPresent();
        total = subjectArrayList.get(pos).getTotal();
        mainpres+=present;
        maintot+=total;
        overll=(double)mainpres/maintot;

        double value  = ((double)present/total);
        double comp = ((double)subjectArrayList.get(pos).getGoal())/100;

        if(value<comp)
            subjectArrayList.get(pos).setStatus("Attend "+ String.valueOf((int)(ceil((double)(comp*total)-present))) +" classes more.");
        else
            subjectArrayList.get(pos).setStatus("You are right on track");

        subjectArrayList.get(pos).setPercentage(((double)present/total)*100);
        pref.edit().putInt("pres",mainpres).apply();
        pref.edit().putInt("tot",maintot).apply();
        recyclerSubjectAdapter.notifyItemChanged(pos);
        mroot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("overall").setValue((double) Math.round(((overll)*100)* 100) / 100);
    }
}
