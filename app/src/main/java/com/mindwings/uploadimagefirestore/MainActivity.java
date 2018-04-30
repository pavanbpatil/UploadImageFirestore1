package com.mindwings.uploadimagefirestore;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ImageView imageview;
    Button buttonupload;
    TextView text;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    Uri imageuri;
    ProgressBar progressbar;
    EditText imagename;
        String abc;
        int p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonupload=(Button) findViewById(R.id.buttonupload);
        imageview=(ImageView) findViewById(R.id.imageview);
        progressbar=(ProgressBar) findViewById(R.id.progressbar);
        imagename=(EditText) findViewById(R.id.imagename);
        text=(TextView) findViewById(R.id.text);
        progressbar.setVisibility(View.GONE);
        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (permission != PackageManager.PERMISSION_GRANTED ) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 1);
            }
        }
        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });
        buttonupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    public void fileChooser()
    {
        Intent intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK && data !=null && data.getData()!=null)
        {
            imageuri=data.getData();
            Picasso.with(this).load(imageuri).into(imageview);
        }
    }

    public String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void uploadFile()
    {
        if(imageuri!=null)
        {
            progressbar.setVisibility(View.VISIBLE);
            StorageReference filereference=mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
            filereference.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressbar.setProgress(100);
                                }
                            }, 300);
                            Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                            Upload upload=new Upload(imagename.getText().toString(),taskSnapshot.getDownloadUrl().toString());
                            String uploadId= mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0 *taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressbar.setProgress((int)progress);
                            int p=(int)progress;
                            text.setText(String.valueOf(p)+" % Uploaded");
                        }
                    });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Mo Image Selected",Toast.LENGTH_SHORT).show();
        }
    }
}

