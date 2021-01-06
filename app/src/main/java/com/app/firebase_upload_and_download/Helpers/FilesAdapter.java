package com.app.firebase_upload_and_download.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.firebase_upload_and_download.R;

import java.util.List;


/*This adapter is required only if you are going to show the list of selected files*/
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    Context context;
    List<CustomModel> filesList;

    public FilesAdapter(Context context, List<CustomModel> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picked_file_layout, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, final int position) {
        holder.fileName.setText(filesList.get(position).getFileName());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, filesList.get(position).getFileName() + " removed!", Toast.LENGTH_SHORT).show();
                filesList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageButton btnRemove;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.txtFileName);
            btnRemove = itemView.findViewById(R.id.btnRemoveFile);
        }
    }
}