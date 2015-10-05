package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by kavitha on 10/4/15.
 */

@Table(name = "PersistTweets")
public class PersistTweet extends Model {
    // This is the unique id given by the server

    @Column(name = "local_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int localId;

    @Column(name = "remote_id")
    public String remoteId;
    @Column(name = "user_id")
    public long userid;
    @Column(name = "username")
    public String username;
    @Column(name = "profilename")
    public String profilename;
    @Column(name = "profileimageurl")
    public String profileimageurl;
    @Column(name = "body")
    public String body;
    @Column(name = "createdat")
    public String createdat;


    // Make sure to have a default constructor for every ActiveAndroid model
    public PersistTweet(){
        super();
    }

    public PersistTweet(String remoteId, int localId, String username, long userid, String profileName, String imgURL, String body, String createdAt){
        super();
        this.remoteId = remoteId;
        this.localId = localId;
        this.username = username;
        this.profilename = profileName;
        this.profileimageurl = imgURL;
        this.userid = userid;
        this.body = body;
        this.createdat = createdAt;
    }
}

