package com.ismailaamassi.androidrealtimeexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentHolder> {

    ArrayList<Student> students;

    public StudentAdapter(ArrayList<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_student, parent, false);
        return new StudentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentHolder holder, int position) {
        Student s = students.get(position);

        holder.tvName.setText(s.getName());
        holder.tvNo.setText(s.getNo());
        if (!s.getImage().isEmpty())
            Glide.with(holder.itemView).load(s.getImage()).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class StudentHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvNo;

        public StudentHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvNo = itemView.findViewById(R.id.tvNo);
        }
    }
}
