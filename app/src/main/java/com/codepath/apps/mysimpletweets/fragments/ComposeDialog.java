package com.codepath.apps.mysimpletweets.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by kavitha on 10/3/15.
 */
public class ComposeDialog extends DialogFragment {

    private Listener mListener;
    private TwitterClient client;
    ImageButton ibClose;
    EditText etStatus;
    Button btnTweet;
    TextView tvCharacters;

    public ComposeDialog () {
        ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_compose, container);
        client = TwitterApplication.getRestClient();
        ibClose = (ImageButton) view.findViewById(R.id.ibClose);
        etStatus = (EditText) view.findViewById(R.id.etStatus);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvCharacters = (TextView) view.findViewById(R.id.tvCharacters);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etStatus.setText("");
                getDialog().hide();
            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tweet out
                client.postTweet(String.valueOf(etStatus.getText()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        etStatus.setText("");
                        getDialog().hide();
                        if (mListener != null) {
                            mListener.updateTimeline();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.v("DEBUG", "Failure: "+statusCode+ " AND " + errorResponse.toString(), throwable);
                    }
                });
            }
        });
        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacters.setText(String.valueOf(140 - etStatus.getText().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void updateTimeline();
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }



}
