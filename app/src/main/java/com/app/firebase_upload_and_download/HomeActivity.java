package com.app.firebase_upload_and_download;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView uploadCard, downloadCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        uploadCard = findViewById(R.id.upload_card);
        downloadCard = findViewById(R.id.download_card);

        uploadCard.setOnClickListener(this);
        downloadCard.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.upload_card:
                i = new Intent(this, UploadActivity.class);
                startActivity(i);
                break;
            case R.id.download_card:
                i = new Intent(this, DownloadActivity.class);
                startActivity(i);
                break;


            default:
                break;
        }

    }
}
