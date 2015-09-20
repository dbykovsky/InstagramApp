package com.home.dbykovskyy.instagramclient;

import android.annotation.TargetApi;
import android.content.Context;


import android.content.Intent;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import java.util.Map;


/**
 * Created by dbykovskyy on 9/15/15.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {

    static class ViewHolder  {

        TextView tvUserName;
        TextView tvNumberOfLikes;
        ImageView image;
        ImageView userPic;
        ImageView like;
        ImageView comment;
        TextView userNameTopNav;
        TextView timeStamp;
        TextView commentText;
        LinearLayout list;

    }

    public InstagramPhotoAdapter(Context context, ArrayList<InstagramPhoto> objects) {
        super(context,android.R.layout.simple_list_item_1,objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final InstagramPhoto photo = getItem(position);
        final ViewHolder viewHolder;
        int colorBlue = getContext().getResources().getColor(R.color.facebook_blue);


        if(convertView==null){
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_photo, parent, false);
            viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tv_userName);
            viewHolder.tvNumberOfLikes = (TextView)convertView.findViewById(R.id.tv_numberOfLikes);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.lv_photo);
            viewHolder.userPic = (ImageView)convertView.findViewById(R.id.user_pic);
            viewHolder.userNameTopNav = (TextView)convertView.findViewById(R.id.userNamePic);
            viewHolder.timeStamp = (TextView)convertView.findViewById(R.id.timestamp);
            viewHolder.like = (ImageView)convertView.findViewById(R.id.like);
            viewHolder.comment = (ImageView)convertView.findViewById(R.id.comment);
            //accessing Linear layout for inner listView items
            viewHolder.list = (LinearLayout) convertView.findViewById(R.id.comment_container);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //get latest Comment and userName
        final Map<Integer, Comment> lastComments = getTwoLatestCommentsWithNames(photo.getComments());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        //clean all for recycled views
        viewHolder.list.removeAllViews();
        //populate inner views AKA comments
        for(Map.Entry<Integer, Comment> entry : lastComments.entrySet()){
            //for(int i=1; i<3; i++){
            View child = inflater.inflate(R.layout.comment_row, null);
            //locate TextView inside the comment_row layout
            viewHolder.commentText = (TextView) child.findViewById(R.id.comment_on_single_file);
            //maje user's name blue
            Spannable userNameCommentBlue = new SpannableString(entry.getValue().getCommenterName()+" " +entry.getValue().getComment());
            userNameCommentBlue.setSpan(new ForegroundColorSpan(colorBlue), 0, entry.getValue().getCommenterName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //app is failing on this step since viewHolder.commentText is null
            viewHolder.commentText.setText(userNameCommentBlue);
            viewHolder.list.addView(child);
        }

        //making user name blue
        Spannable userNameBlue = new SpannableString(photo.getUseName()+" " +photo.getCaption());
        userNameBlue.setSpan(new ForegroundColorSpan(colorBlue), 0, photo.getUseName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //setting username and likes count
        viewHolder.tvUserName.setText(userNameBlue);
        viewHolder.userNameTopNav.setText(photo.getUseName());
        viewHolder.timeStamp.setText(convertTimeToSeconds(Long.parseLong(photo.getTimeStamp())));
        //this is to persist like count after scrolling
        if(photo.isLikeStatus()){
            viewHolder.tvNumberOfLikes.setText((Integer.parseInt(photo.getLikesCount())+1) + " likes");
        }else {
            viewHolder.tvNumberOfLikes.setText(Integer.parseInt(photo.getLikesCount()) + " likes");
        }

        //this is to persist red heart icon AKA like after scrolling
        if(photo.isLikeStatus()){
            viewHolder.like.setImageResource(R.drawable.insta_like_red_heart);
            viewHolder.like.setTag("red");
        }else {
            viewHolder.like.setImageResource(R.drawable.insta_like_empty_heart);
            viewHolder.like.setTag("empty");
        }
        viewHolder.comment.setImageResource(R.drawable.insta_comment);

        //getting photo dimentions
        int displayWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        int picWidth = Integer.parseInt(photo.getImageWidth());
        int picHeight = Integer.parseInt(photo.getImageHeight());
        int aspectRatio =picWidth/picHeight;
        int newHeight = displayWidth/aspectRatio;

        //setting photo
        viewHolder.image.setImageResource(0);
        Picasso.with(getContext()).load(photo.getImageUrl()).placeholder(R.drawable.placeholder).resize(displayWidth, newHeight).into(viewHolder.image);

        //making user profile photo oval
        viewHolder.userPic.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(getContext().getResources().getColor(R.color.instagram_grey))
                .borderWidthDp(1)
                .cornerRadiusDp(100)
                .oval(false)
                .build();

        //setting user profile photo
        Picasso.with(getContext())
                .load(photo.getUserPicUrl()).resize(90, 90)
                .transform(transformation)
                .into(viewHolder.userPic);


        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfLikes = 0;
                if (viewHolder.like.getTag().toString().equals("empty")) {
                    viewHolder.like.setImageResource(R.drawable.insta_like_red_heart);
                    viewHolder.like.setTag("red");
                    numberOfLikes = Integer.parseInt(photo.getLikesCount());
                    viewHolder.tvNumberOfLikes.setText((numberOfLikes + 1) + " likes");
                    photo.setLikeStatus(true);
                } else {
                    viewHolder.like.setImageResource(R.drawable.insta_like_empty_heart);
                    viewHolder.like.setTag("empty");
                    numberOfLikes = Integer.parseInt(photo.getLikesCount());
                    viewHolder.tvNumberOfLikes.setText((numberOfLikes) + " likes");
                    photo.setLikeStatus(false);
                }
            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), PostCommentsActivity.class);

                myIntent.putExtra("comments", photo.getComments());

                getContext().startActivity(myIntent);




            }
        });



        return convertView;
    }


    public String convertTimeToSeconds(Long epochTime) {
        String rawResult = null;
        rawResult = DateUtils.getRelativeTimeSpanString(epochTime * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        if(rawResult.contains("second")){
            return rawResult.replaceAll("[^0-9]+", "s");
        }else if(rawResult.contains("minute")) {
            return rawResult.replaceAll("[^0-9]+", "m");
        }else if(rawResult.contains("hour")){
            return rawResult.replaceAll("[^0-9]+", "h");
        }else if(rawResult.contains("day")){
            return rawResult.replaceAll("[^0-9]+", "d");
        }else return rawResult.replaceAll("[^0-9]+", "m");
    }

    public Map<Integer, Comment> getTwoLatestCommentsWithNames(ArrayList<Comment> comments){
        Map<Integer, Comment> twoLatestComments=new HashMap<Integer, Comment>();

        if(comments.size()==0){
            return twoLatestComments;
        }

        Collections.sort(comments, new Comparator<Comment>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(Comment lhs, Comment rhs) {
                return Long.compare(lhs.getTimeStamp(), rhs.getTimeStamp());
            }
        });

        twoLatestComments.put(1, comments.get(comments.size()-1));

        if(comments.size()>1){
            twoLatestComments.put(2, comments.get(comments.size()-2));
        }

        return twoLatestComments;

    }

}
