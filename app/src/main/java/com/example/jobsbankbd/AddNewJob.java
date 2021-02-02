package com.example.jobsbankbd;

import android.app.DatePickerDialog;
import android.graphics.Color;
//import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewJob extends AppCompatActivity {



    private CheckBox chkbx;
    private ImageView selectImage;
    private EditText etTitle;
    private AlertDialog dialog;////no private
    private EditText etOrganization;
    private EditText etVacancy;
    private TextView etDeadline;
    private Spinner spCategory;
    private Button btnCreate;

    private Toolbar mToolbar;
    private TextView toolBarTitle;
    private int GalleryPick=1;
    private Uri uri;
    private String title,vacancy,deadline,category,organization;

    private boolean imgSelected;
    private boolean hotJob;
    private DatabaseReference hotJobRef,CurrentJobRef,govtJobRef,privateJobRef;
    private StorageReference imgStorageRef;

    final Calendar myCalendar = Calendar.getInstance();
    public AddNewJob() { }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_job);

        mToolbar = findViewById(R.id.add_job_toolbar);
        toolBarTitle=findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Add New Job");


        imgSelected=false;


        privateJobRef= FirebaseDatabase.getInstance().getReference().child("Private");
        hotJobRef= FirebaseDatabase.getInstance().getReference().child("Hot");
        govtJobRef= FirebaseDatabase.getInstance().getReference().child("Govt");
        imgStorageRef= FirebaseStorage.getInstance().getReference().child("Image");

        chkbx=findViewById(R.id.chk_box);

        selectImage=findViewById(R.id.select_image_img);
        etTitle = findViewById(R.id.new_job_title);
        etOrganization = findViewById(R.id.new_job_organization);
        etVacancy = findViewById(R.id.new_job_vacancy);
        etDeadline = findViewById(R.id.new_job_deadline);
        spCategory = findViewById(R.id.spinner_category);
        btnCreate = findViewById(R.id.create_post_button);




        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        etDeadline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddNewJob.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostNewJov();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etDeadline.setText(sdf.format(myCalendar.getTime()));
    }

    private void PostNewJov() {

        GetData();

        if (!title.equals("") && !organization.equals("") && !vacancy.equals("") && !deadline.equals("") && !category.equals("Category") && imgSelected)
        {
            showProgressBar();
            UploadImageAndDetails();
        }else{
            if (title.equals("")){
                Toast.makeText(this, "Input Title", Toast.LENGTH_SHORT).show();
            }
            if (organization.equals("")){
                Toast.makeText(this, "Input Organization", Toast.LENGTH_SHORT).show();
            }
            if (vacancy.equals("")){
                Toast.makeText(this, "Input Vacancy", Toast.LENGTH_SHORT).show();
            }
            if (deadline.equals("")){
                Toast.makeText(this, "Input Deadline", Toast.LENGTH_SHORT).show();
            }
            if (category.equals("Category")){
                Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            }
            if (!imgSelected){
                Toast.makeText(this, "Select image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void GetData() {
        if (chkbx.isChecked()){
            hotJob=true;
        }
        title=etTitle.getText().toString();
        vacancy=etVacancy.getText().toString();
        deadline=etDeadline.getText().toString();
        organization=etOrganization.getText().toString();
        category=spCategory.getSelectedItem().toString();
        int vcny=Integer.parseInt(vacancy);
        int Rvcny=500-vcny;
        vacancy=String.valueOf(Rvcny);
    }

    private void updateDatabase(final String currentJobKey, final String imgDownloadUrl) {

        CurrentJobRef=FirebaseDatabase.getInstance().getReference().child(category).child(currentJobKey);

        JobInfo jobinfo=new JobInfo(title, organization, deadline, vacancy, category,imgDownloadUrl);
        CurrentJobRef.setValue(jobinfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if (hotJob){
                        JobInfo hotJonInfo=new JobInfo(title, organization, deadline, vacancy, category,imgDownloadUrl);
                        hotJobRef.child(currentJobKey).setValue(hotJonInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewJob.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    etDeadline.setText("");
                    etOrganization.setText("");
                    etTitle.setText("");
                    etVacancy.setText("");
                    imgSelected=false;
                    uri=null;//--------------
                    selectImage.setImageResource(R.drawable.add_img_transparent);//----------
                    spCategory.setSelection(0);//----------------
                    chkbx.setChecked(false);//----------------
                    dismissLoadingBar();
                    Toast.makeText(AddNewJob.this, "Job Posted...", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddNewJob.this, "Posting Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dismissLoadingBar() {
        dialog.cancel();
    }


    private void UploadImageAndDetails() {

        if (category.equals("Govt")){
            final String jobKey = govtJobRef.push().getKey();

            final StorageReference filePath = imgStorageRef.child(jobKey + ".jpeg");

            UploadTask uploadTask=null;
            uploadTask=filePath.putFile(uri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imgStorageRef.child(jobKey + ".jpeg").getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    //Toast.makeText(AddNewJob.this, "Govt, Image uploaded", Toast.LENGTH_SHORT).show();
                                    updateDatabase(jobKey,task.getResult().toString());//to upload to database
                                }
                            });
                }
            });
        }

        else if (category.equals("Private")){
            final String jobKey = privateJobRef.push().getKey();
            final StorageReference filePath = imgStorageRef.child(jobKey + ".jpeg");

            UploadTask uploadTask=null;
            uploadTask=filePath.putFile(uri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imgStorageRef.child(jobKey + ".jpeg").getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    updateDatabase(jobKey,task.getResult().toString());//to upload to database
                                }
                            });
                }
            });
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && null != data) {
            uri = data.getData();//Uri type variable
            selectImage.setImageURI(uri);
            imgSelected=true;
        }
    }

    private void showProgressBar() {

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
        tvText.setText("Posting New Job ...");
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

}