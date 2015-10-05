package com.codepath.apps.mysimpletweets.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.helpers.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kavitha on 9/30/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter <Tweet> {

    private static class ViewHolder {
        ImageView ivUserProfilePicture;
        TextView tvUsername;
        TextView tvBody;
        TextView tvCreatedAt;
    }
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivUserProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivUserProfilePicture.setImageResource(android.R.color.transparent);
        if (tweet == null) {
            return convertView;
        }
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivUserProfilePicture);
        viewHolder.tvUsername.setText(Html.fromHtml("<font color=\"#000000\">" + tweet.getUser().getName() + "</font> <small><font color=\"#BEBEBE\">@" + tweet.getUser().getScreenName() + "</font></small>"));
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvCreatedAt.setText(Utils.cleanupTheTimeStr(tweet.getCreatedAt()));
        return convertView;
    }
}
