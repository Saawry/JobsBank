package com.example.jobsbankbd;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Environment;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadImage extends AppCompatActivity {

    private TextView TvTitle, TvOrg,TvDeadline,TvVacancy,TvDaysRemain;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 100;
    private Button DwnBtn,ShrBtn,dltBtn;
    private ImageView displayImg;
    private String imgUrl, key,category;

    private TextView toolBarTitle;
    private Toolbar mToolbar;
    private FirebaseStorage imgStorageRef;
    private AlertDialog dialog;
    private StorageReference imgRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);


        key = getIntent().getExtras().get("key").toString();
        category = getIntent().getExtras().get("category").toString();

        mToolbar=findViewById(R.id.job_details_toolbar);
        toolBarTitle=findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Job Details");

        TvDaysRemain = findViewById(R.id.tv_days_remaining);
        TvDeadline = findViewById(R.id.tv_deadline);
        TvOrg = findViewById(R.id.tv_org);
        TvTitle = findViewById(R.id.tv_title);
        TvVacancy = findViewById(R.id.tv_vacancy);


        imgStorageRef = FirebaseStorage.getInstance();
        imgRef = FirebaseStorage.getInstance().getReference();

        DwnBtn = findViewById(R.id.download_btn);
        ShrBtn = findViewById(R.id.share_btn);
        dltBtn = findViewById(R.id.delete_btn);
        displayImg = findViewById(R.id.show_image);


        ShowDetailsAndImage();

        DwnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFile();

            }
        });
        ShrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFile();

            }
        });
        dltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteImage();

            }
        });

    }

    private void ShareFile() {

        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        PackageManager pm = DownloadImage.this.getPackageManager();
        List<ResolveInfo> resInfos = pm.queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            System.out.println("Have package");
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);

                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana")
                        || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.plus")
                        || packageName.contains("com.google.android.talk") || packageName.contains("com.slack")
                        || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.orca")
                        || packageName.contains("com.yahoo.mobile") || packageName.contains("com.skype.raider")
                        || packageName.contains("com.android.mms")|| packageName.contains("com.linkedin.android")
                        || packageName.contains("com.google.android.apps.messaging")) {
                    Intent intent = new Intent();

                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.putExtra("AppName", resInfo.loadLabel(pm).toString());
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, imgUrl);
                    intent.putExtra(Intent.EXTRA_SUBJECT, category+"Job Circular");
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Collections.sort(targetShareIntents, new Comparator<Intent>() {
                    @Override
                    public int compare(Intent o1, Intent o2) {
                        return o1.getStringExtra("AppName").compareTo(o2.getStringExtra("AppName"));
                    }
                });
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            } else {
                Toast.makeText(DownloadImage.this, "No app to share.", Toast.LENGTH_LONG).show();
            }
        }






    }

    private void ShowDetailsAndImage() {
        Toast.makeText(this, "Wait, Loading Image..", Toast.LENGTH_LONG).show();
        DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference().child(category).child(key);

        infoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue().toString();
                String org = dataSnapshot.child("organization").getValue().toString();
                String deadline = dataSnapshot.child("deadline").getValue().toString();
                String vacancy = dataSnapshot.child("vacancy").getValue().toString();
                imgUrl = dataSnapshot.child("imgUrl").getValue().toString();

                int vcn=Integer.parseInt(vacancy);
                vcn=500-vcn;
                vacancy=String.valueOf(vcn);
                //-----------
                DaysConvert daysConvert=new DaysConvert();
                TvDaysRemain.setText(daysConvert.daysRemain(deadline));
                //-----------
                TvTitle.setText(title);
                TvOrg.setText(org);
                TvVacancy.setText(vacancy);
                TvDeadline.setText(deadline);
                Picasso.get().load(imgUrl).placeholder(R.drawable.images_empty).into(displayImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void DeleteImage() {

        showLoadingBar();
        StorageReference photoRef = imgStorageRef.getReferenceFromUrl(imgUrl);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                DatabaseReference deleteJobRef= FirebaseDatabase.getInstance().getReference().child(category);
                deleteJobRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoadingBar();
                        Toast.makeText(DownloadImage.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissLoadingBar();
                        Toast.makeText(DownloadImage.this, "Error.!"+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dismissLoadingBar();
                Toast.makeText(DownloadImage.this, "Error.! "+exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void DownloadFile() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(DownloadImage.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DownloadImage.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
        else
        {
            showLoadingBar();

            //final File outFile = new File(Environment.getExternalStorageDirectory(), key+".jpg");
            final File outFile = new File(Environment.getExternalStoragePublicDirectory("download"), key+".jpeg");
            long FileSize = 10240 * 10240;
            imgRef.child("Image").child(key + ".jpeg").getBytes(FileSize)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            try {
                                FileOutputStream fos = new FileOutputStream(outFile);
                                fos.write(bytes);
                                fos.close();
                                dismissLoadingBar();
                                Toast.makeText(DownloadImage.this, "Downloaded", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                dismissLoadingBar();
                                Toast.makeText(DownloadImage.this, "Error.!" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dismissLoadingBar();
                    Toast.makeText(DownloadImage.this, "Error.!" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showLoadingBar() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("On Progress ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void dismissLoadingBar() {
        dialog.cancel();
    }
}
