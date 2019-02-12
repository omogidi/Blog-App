package com.example.user.blogapp.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.blogapp.Activities.PostListActivity;
import com.example.user.blogapp.Model.Blog;
import com.example.user.blogapp.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 1/30/2019.
 */

public class BlogRecyclerViewAdapter extends RecyclerView.Adapter<BlogRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerViewAdapter(Context context, List<Blog> blogList)
    {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Blog blog = blogList.get(position);
        String imageurl;

        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());

        //date format
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        holder.timestamp.setText(formattedDate);

        imageurl = blog.getImage();
        //TODO: Use picasso Library to load Image
        Picasso.with(context)
                .load(imageurl)
                .into(holder.image);

    }

    @Override
    public int getItemCount()
    {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title, desc, timestamp;
        public ImageView image;

        String userId;

        public ViewHolder(View view, Context ctx)
        {
            super(view);
            context = ctx;

            title = view.findViewById(R.id.postTitleList);
            desc = view.findViewById(R.id.postTextList);
            timestamp = view.findViewById(R.id.timeStampList);
            image = view.findViewById(R.id.postImageList);

            userId = null;
        }
    }
}
