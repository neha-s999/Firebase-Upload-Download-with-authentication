package com.app.firebase_upload_and_download;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class MyViewHolder extends RecyclerView.ViewHolder {


    TextView mName;
    TextView mLink;
    ImageButton mDownload;

    public MyViewHolder(@NonNull View itemView) {

        super(itemView);

        mName = itemView.findViewById(R.id.name);
        mLink = itemView.findViewById(R.id.link);
        mDownload = itemView.findViewById(R.id.button);


    }
}
