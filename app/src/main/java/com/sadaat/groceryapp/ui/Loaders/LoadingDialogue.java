package com.sadaat.groceryapp.ui.Loaders;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.temp.UserLive;

/**
 * Modified Custom Loading Dialogue
 * Sadaat Progress Designed By AHSAN 05-03-2022
 * (AHSAN IQBAL) 04:45pm
 * */

public class LoadingDialogue {

    private Dialog dialog;
    private Context mContext;
    private String username = "";
    private String tag = "Please Wait";
    private String message = "We are processing your request";

    MaterialTextView mTxvUsername;
    MaterialTextView mTxvTag;
    MaterialTextView mTxvMessage;

    public LoadingDialogue(Context context ) {
        dialog = new Dialog(context);
        this.mContext = context;
        dialog.setContentView(R.layout.sadaat_waiting_progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mTxvUsername = dialog.findViewById(R.id.alert_dialogue_username);
        mTxvTag = dialog.findViewById(R.id.alert_dialogue_tag);
        mTxvMessage = dialog.findViewById(R.id.alert_dialogue_message);

        dialog.setCancelable(false);
    }

    public LoadingDialogue(Context context, String username, String tag, String message) {
        this(context);
        this.username = username;
        this.tag = tag;
        this.message = message;

    }

    private void showDialogue(){
        mTxvUsername.setText(username);
        mTxvTag.setText(tag);
        mTxvMessage.setText(message);
        dialog.show();
    }

    public void show(String title, String message) {
        // TODO Implement Username Data
        this.tag = title;
        this.message = message;
        if (UserLive.currentLoggedInUser!=null){
            this.username = "Dear "+UserLive.currentLoggedInUser.getFullName()+"!";
        }
        else{
            this.username = "Dear User!";
        }
        showDialogue();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
