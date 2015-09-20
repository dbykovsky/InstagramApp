package com.home.dbykovskyy.instagramclient;

import android.content.Context;


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

import java.util.List;

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

    public InstagramPhotoAdapter(Context context, List<InstagramPhoto> objects) {
        super(context,android.R.layout.simple_list_item_1,objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final InstagramPhoto photo = getItem(position);
        final ViewHolder viewHolder;



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

            //accessing Linear layout. My linear layout is nested in to single_photo_item (not sure if it's correct)
            viewHolder.list = (LinearLayout) convertView.findViewById(R.id.comment_container);
            viewHolder.list.removeAllViews();
            //load the xml of a single row
            View child =  inflater.inflate(R.layout.comment_row, parent);
            //locate TextView inside the comment_row layout
            viewHolder.commentText = (TextView) child.findViewById(R.id.comment_on_single_file);
            //app is failing on this step since viewHolder.commentText is null
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

            String comment =  photo.getComments().get(0).getComment();
            viewHolder.list.addView(viewHolder.commentText);
            viewHolder.commentText.setText("Teststststst");
            /* nested list's stuff */





        //to color a piece of text using <>
        int colorBlue = getContext().getResources().getColor(R.color.facebook_blue);
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
                if(viewHolder.like.getTag().toString().equals("empty")){
                    viewHolder.like.setImageResource(R.drawable.insta_like_red_heart);
                    viewHolder.like.setTag("red");
                    numberOfLikes = Integer.parseInt(photo.getLikesCount());
                    viewHolder.tvNumberOfLikes.setText((numberOfLikes + 1) + " likes");
                    photo.setLikeStatus(true);
                }else{
                    viewHolder.like.setImageResource(R.drawable.insta_like_empty_heart);
                    viewHolder.like.setTag("empty");
                    numberOfLikes = Integer.parseInt(photo.getLikesCount());
                    viewHolder.tvNumberOfLikes.setText((numberOfLikes) + " likes");
                    photo.setLikeStatus(false);
                }
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
       /* return rawResult;
    }*/
}
