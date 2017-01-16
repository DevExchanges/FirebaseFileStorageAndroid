package info.devexchanges.firebasefilestorage;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadActivity extends RootActivity {

    private StorageReference storageReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://filestorage-d5afb.appspot.com").child("firebase.png");

        View btnUploadImage = findViewById(R.id.btn_upload_image);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AssetManager assetManager = UploadActivity.this.getAssets();
                InputStream istr;
                Bitmap bitmap;
                try {
                    //get bitmap from PNG file in assets folder
                    istr = assetManager.open("firebase.png");
                    bitmap = BitmapFactory.decodeStream(istr);

                    //decode to byte output stream
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] data = outputStream.toByteArray();

                    //Upload to firebase
                    showProgressDialog("Upload Bitmap", "Uploading...");
                    UploadTask uploadTask = storageReference.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                            dismissProgressDialog();
                            Toast.makeText(UploadActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dismissProgressDialog();
                            Toast.makeText(UploadActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        View btnUploadFile = findViewById(R.id.btn_upload_file);
        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReference = storage.getReferenceFromUrl("gs://filestorage-d5afb.appspot.com").child("test_upload.txt");

                //Upload input stream to Firebase
                showProgressDialog("Upload File", "Uploading text file...");
                InputStream stream = getResources().openRawResource(R.raw.test);
                UploadTask uploadTask = storageReference.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        dismissProgressDialog();
                        Toast.makeText(UploadActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dismissProgressDialog();
                        Toast.makeText(UploadActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        View btnDownload = findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });
    }
}