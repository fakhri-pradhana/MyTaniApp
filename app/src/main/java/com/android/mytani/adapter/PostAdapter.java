package com.android.mytani.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mytani.R;
import com.android.mytani.activity.PostDetailActivity;
import com.android.mytani.models.Post;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Post> mData;
    List<Post> mDataAll;

    public PostAdapter(Context context, List<Post> mData) {
        this.context = context;
        this.mData = mData;
        this.mDataAll = new ArrayList<>(mData);
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
        Glide.with(context)
                .load(mData.get(position).getPicture())
                .placeholder(R.drawable.ic_load_image)
                .into(holder.iv_imgPost);
        Glide.with(context).load(mData.get(position).getUserPhoto()).into(holder.img_postProfile);

        String catPost = mData.get(position).getCategory();
//        Log.d("POST ADAPTER ", "INI CATEGORY " + catPost);

        if (catPost.equals("Buah")){
            Glide.with(context).load(R.drawable.cat_fruit).into(holder.iv_catPost);
        } else if (catPost.equals("Sayur")){
            Glide.with(context).load(R.drawable.cat_veggie).into(holder.iv_catPost);
        } else if (catPost.equals("Biji")){
            Glide.with(context).load(R.drawable.cat_seed).into(holder.iv_catPost);
        }else if (catPost.equals("Pohon")){
            Glide.with(context).load(R.drawable.cat_tree).into(holder.iv_catPost);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return filter;

    }

    Filter filter = new Filter() {
        // run on background thread
        // created otomatis
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Post> filteredPost = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredPost.addAll(mDataAll);
            }else {
                for (Post post : mDataAll){
                    if (post.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredPost.add(post);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredPost;

            return filterResults;
        }

        // runs on a ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            mData.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView iv_imgPost, img_postProfile, iv_catPost;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.row_post_title);
            iv_imgPost = itemView.findViewById(R.id.row_post_img);
            img_postProfile = itemView.findViewById(R.id.row_post_profile_img);
            iv_catPost = itemView.findViewById(R.id.iv_catpost_img);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(context, PostDetailActivity.class);
                    int position = getLayoutPosition();

                    postDetailActivity.putExtra("Post", mData.get(position));

                    /*postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    postDetailActivity.putExtra("userId", mData.get(position).getUserId());*/
                    // todo get username from data post

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    context.startActivity(postDetailActivity);


                }
            });

        }
    }
}
