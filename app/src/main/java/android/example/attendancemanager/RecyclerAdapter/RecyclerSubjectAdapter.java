package android.example.attendancemanager.RecyclerAdapter;

import android.content.Context;
import android.example.attendancemanager.Model.UsersSubject;
import android.example.attendancemanager.R;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerSubjectAdapter extends RecyclerView.Adapter<RecyclerSubjectAdapter.MyViewHolder> {

    Context context;
    ArrayList<UsersSubject> arrayList;

    public RecyclerSubjectAdapter(Context context,ArrayList<UsersSubject> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.subject.setText(arrayList.get(position).getSubjectname());
        holder.attendance.setText("  "+String.valueOf(arrayList.get(position).getPresent())+"/"+String.valueOf(arrayList.get(position).getTotal()));
        holder.percentage.setText(String.valueOf(arrayList.get(position).getPercentage())+" %");
        holder.status.setText("  "+arrayList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView attendance, subject, percentage,status;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            attendance = itemView.findViewById(R.id.row_attendance_value);
            subject = itemView.findViewById(R.id.row_subject_name);
            percentage = itemView.findViewById(R.id.row_percentage);
            status = itemView.findViewById(R.id.row_status_value);
        }
    }


}
