package com.app.firebase_upload_and_download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


    DownloadActivity downloadActivity;
    ArrayList<DownModel> downModels;


    public MyAdapter(DownloadActivity main2Activity, ArrayList<DownModel> downModels) {
        this.downloadActivity = main2Activity;
        this.downModels = downModels;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(downloadActivity.getBaseContext());

        View view = layoutInflater.inflate(R.layout.elements, null, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {


        myViewHolder.mName.setText(downModels.get(i).getName());
        myViewHolder.mLink.setText(downModels.get(i).getLink());
        myViewHolder.mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = downModels.get(i).getName();
                String filenameArray[] = filename.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];


                downloadFile(myViewHolder.mName.getContext(), downModels.get(i).getName(), "." + extension, DIRECTORY_DOWNLOADS, downModels.get(i).getLink());

            }
        });

    }

    public void downloadFile(Context context, String FileName, String FileExtension, String destinationDirectory, String url) {


        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, FileName + FileExtension);

        downloadManager.enqueue(request);

    }

    @Override
    public int getItemCount() {
        return downModels.size();
    }


}
