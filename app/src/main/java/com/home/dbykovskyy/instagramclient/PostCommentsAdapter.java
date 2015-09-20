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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbykovskyy on 9/20/15.
 */
public class PostCommentsAdapter extends ArrayAdapter<Comment> {

    static class ViewHolder  {

        ImageView userPic;
        TextView commentText;
        TextView timeStamp;
    }


    public PostCommentsAdapter(Context context, ArrayList<Comment> objects) {
        super(context,android.R.layout.simple_list_item_1,objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final  Comment comment = getItem(position);
        final ViewHolder viewHolder;
        int colorBlue = getContext().getResources().getColor(R.color.facebook_blue);

        if(convertView==null){
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.single_comment_view, parent, false);
            viewHolder.userPic = (ImageView)convertView.findViewById(R.id.iv_userPic);
            viewHolder.commentText = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.timeStamp = (TextView)convertView.findViewById(R.id.tv_time_stamp);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Spannable userNameBlue = new SpannableString(comment.getCommenterName()+" " +comment.getComment());
        userNameBlue.setSpan(new ForegroundColorSpan(colorBlue), 0, comment.getCommenterName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.commentText.setText(userNameBlue);
        viewHolder.timeStamp.setText(convertTimeToSeconds(comment.getTimeStamp()));


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
                .load(comment.getCommenterPic()).resize(55, 55)
                .transform(transformation)
                .into(viewHolder.userPic);



        return convertView;

    }


    public String convertTimeToSeconds(Long epochTime) {
        String rawResult = null;
        rawResult = DateUtils.getRelativeTimeSpanString(epochTime * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        if(rawResult.startsWith("in")){

            return rawResult=rawResult.replaceAll("in","").trim()+" ago";
        }
        /*if(rawResult.contains("seconds")){
            return rawResult.replaceAll("[^0-9]+", " seconds ago");
        }else if(rawResult.contains("minutes")) {
            return rawResult.replaceAll("[^0-9]+", " minutes ago");
        }else if(rawResult.contains("hour")){
            return rawResult.replaceAll("[^0-9]+", " hours ago");
        }else if(rawResult.contains("day")){
            return rawResult.replaceAll("[^0-9]+", " days ago");
        }else return rawResult.replaceAll("[^0-9]+", " months ago");*/
        return rawResult;
    }



}
