package com.home.dbykovskyy.instagramclient;

import java.io.Serializable;

/**
 * Created by dbykovskyy on 9/19/15.
 */
public class Comment implements Serializable {

    private String comment;
    private String commenterName;
    private String commenterPic;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private Long timeStamp;


    public String getCommenterPic() {
        return commenterPic;
    }

    public void setCommenterPic(String commenterPic) {
        this.commenterPic = commenterPic;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
