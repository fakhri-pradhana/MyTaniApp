package com.android.mytani.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mytani.R;
import com.android.mytani.activity.PostDetailActivity;
import com.android.mytani.models.Post;
import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<Post> mData;

    public PostAdapter(Context context, List<Post> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(context).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(mData.get(position).getTitle());
        Glide.with(context).load(mData.get(position).getPicture()).into(holder.iv_imgPost);
        Glide.with(context).load(mData.get(position).getUserPhoto()).into(holder.img_postProfile);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView iv_imgPost, img_postProfile;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.row_post_title);
            iv_imgPost = itemView.findViewById(R.id.row_post_img);
            img_postProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(context, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    // todo get username from data post

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    context.startActivity(postDetailActivity);


                }
            });

        }
    }
}
