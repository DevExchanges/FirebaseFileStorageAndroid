package info.devexchanges.firebasefilestorage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class DownloadActivity extends RootActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download);

        final ImageView imageView = (ImageView)findViewById(R.id.image);
        View btnDownloadByteArray = findViewById(R.id.btn_download_byte);
        btnDownloadByteArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://filestorage-d5afb.appspot.com").child("nougat.jpg");
                final long ONE_MEGABYTE = 1024 * 1024;

                //download file as a byte array
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                        showToast("Download successful!");
                    }
                });
            }
        });

        View btnDownloadAsFile = findViewById(R.id.btn_download_file);
        btnDownloadAsFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://filestorage-d5afb.appspot.com").child("nougat.jpg");

                //get download file url
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("Main", "File uri: " + uri.toString());
                    }
                });

                //download the file
                try {
                    showProgressDialog("Download File", "Downloading File...");
                    final File localFile = File.createTempFile("images", "jpg");
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                            dismissProgressDialog();
                            showToast("Download successful!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            dismissProgressDialog();
                            showToast("Download Failed!");
                        }
                    });
                } catch (IOException e ) {
                    e.printStackTrace();
                    Log.e("Main", "IOE Exception");
                }
            }
        });
    }
}
