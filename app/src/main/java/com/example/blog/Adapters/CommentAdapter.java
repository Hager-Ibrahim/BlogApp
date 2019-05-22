package com.example.blog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blog.Models.Comment;
import com.example.blog.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {


    Context mContext ;
    List<Comment> mList ;

    public CommentAdapter(Context context , List<Comment> list){
        mContext = context ;
        mList = list ;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        holder.userName.setText(mList.get(position).getName());
        holder.commentTime.setText(formatDate(mList.get(position).getTimestamp()));
        holder.comment.setText(mList.get(position).getComment());
        Glide.with(mContext).load(mList.get(position).getUserImg()).into(holder.userImg);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder{

        CircleImageView userImg ;
        TextView userName, comment , commentTime ;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment_section);
            commentTime = itemView.findViewById(R.id.comment_time);
            userImg = itemView.findViewById(R.id.comment_user_img);
            userName = itemView.findViewById(R.id.comment_user_name);
        }
    }

    public String formatDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(date);
    }
}
