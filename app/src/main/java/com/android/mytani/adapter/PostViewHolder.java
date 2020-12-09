package com.android.mytani.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mytani.R;
import com.android.mytani.activity.PostDetailActivity;
import com.android.mytani.models.Post;

import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_title;
    public ImageView iv_imgPost, img_postProfile;
    public Context context;
    public List<Post> mData;
    public String mPostKey;

    public View v;


    public PostViewHolder(@NonNull View itemView, List<Post> mData, Context context, String postKey) {
        super(itemView);

        tv_title = itemView.findViewById(R.id.row_post_title);
        iv_imgPost = itemView.findViewById(R.id.row_post_img);
        img_postProfile = itemView.findViewById(R.id.row_post_profile_img);
        this.mData = mData;
        this.context = context;
        this.mPostKey = postKey;
        v=itemView;

//        showLog(mPostKey);



        /*itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent postDetailActivity = new Intent(context, PostDetailActivity.class);
                int position = getAdapterPosition();

                postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());

                postDetailActivity.putExtra("title", mData.get(position).getTitle());
                postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                postDetailActivity.putExtra("description", mData.get(position).getDescription());

                postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                postDetailActivity.putExtra("userId", mData.get(position).getUserId());
                // todo get username from data post

                long timestamp = (long) mData.get(position).getTimeStamp();
                postDetailActivity.putExtra("postDate", timestamp);
                context.startActivity(postDetailActivity);


            }
        });*/

    }

    private void showLog(String msg) {
        Log.d("POST VIEW HOLDER", msg);
    }
}
