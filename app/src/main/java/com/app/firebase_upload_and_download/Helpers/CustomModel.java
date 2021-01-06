package com.app.firebase_upload_and_download.Helpers;

import android.net.Uri;

/*model to show the list of selected files*/
public class CustomModel {
    String fileName;
    Uri fileURI;


    public CustomModel(String fileName, Uri fileURI) {
        this.fileName = fileName;
        this.fileURI = fileURI;
    }

    public String getFileName() {
        return fileName;
    }

    public Uri getFileURI() {
        return fileURI;
    }
}

