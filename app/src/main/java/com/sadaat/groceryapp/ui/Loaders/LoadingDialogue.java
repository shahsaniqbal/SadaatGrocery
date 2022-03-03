package com.sadaat.groceryapp.ui.Loaders;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.sadaat.groceryapp.R;

public class LoadingDialogue extends ProgressDialog {

    public LoadingDialogue(Context context ) {
        super(context);
        setCancelable(false);
    }

    public void show(String title, String message) {
        setTitle(title);
        setMessage(message);

        this.setIcon(R.mipmap.logo);

        super.show();
    }

}
