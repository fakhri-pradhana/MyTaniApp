package com.android.mytani.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mytani.R;
import com.android.mytani.models.Comment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private Context mContext;
    private List<Comment> mData;

    // firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference commentRef;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        // initialize firebase
/*        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();*/
        return new CommentViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position).getUimg()).into(holder.iv_user); // user photo
        holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_time.setText(timeStampToString((long) mData.get(position).getTimestamp()));
//        holder.tv_voteCount.setText(mData.get(position).getUpvote());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_user, iv_vote;
        TextView tv_name, tv_content, tv_time, tv_voteCount;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize layout variable
            iv_user = itemView.findViewById(R.id.comment_user_photo);
            iv_vote = itemView.findViewById(R.id.iv_vote);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_time = itemView.findViewById(R.id.comment_time);
            tv_voteCount = itemView.findViewById(R.id.tv_count_vote);

            /*iv_vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    // todo fitur vote bakal ada disini
                    String uid = currentUser.getUid();
                    commentRef = firebaseDatabase.getReference("comment").child("votedBy").push();

                    commentRef.setValue(uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast("Berhasil vote comment");
                            Glide.with(v).load(R.drawable.ic_heart_selected).into(iv_vote);
                        }
                    });

                }
            });*/
        }
    }

    private void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    private String timeStampToString (long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);

        String date = DateFormat.format("hh:mm", calendar).toString();

        return date;
    }
}
