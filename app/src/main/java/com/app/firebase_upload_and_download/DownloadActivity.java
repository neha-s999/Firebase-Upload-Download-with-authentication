package com.app.firebase_upload_and_download;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<DownModel> downModelArrayList = new ArrayList<>();
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);


        setUpRV();
        setUpFB();
        dataFromFirebase();
    }


    private void dataFromFirebase() {
        if (downModelArrayList.size() > 0)
            downModelArrayList.clear();

        db = FirebaseFirestore.getInstance();

        db.collection("Uploads" + "" + "").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    //changed
                    DownModel downModel = new DownModel(documentSnapshot.getString("name"),
                            documentSnapshot.getString("link"));
                    downModelArrayList.add(downModel);
                }


                myAdapter = new MyAdapter(DownloadActivity.this, downModelArrayList);
                mRecyclerView.setAdapter(myAdapter);


            }
        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DownloadActivity.this, "Error:-", Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void setUpFB() {
        db = FirebaseFirestore.getInstance();

    }

    private void setUpRV() {
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

}
