package com.ismailaamassi.androidrealtimeexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddNewStudentActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1021;

    ImageView ivImage;
    FloatingActionButton fabGallery;

    EditText etName, etNo;
    Button btnAdd;

    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);

        ivImage = findViewById(R.id.ivImage);
        fabGallery = findViewById(R.id.fabGallery);
        etName = findViewById(R.id.etName);
        etNo = findViewById(R.id.etNo);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudentToDatabase();
            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                startActivityForResult(getIntent, PICK_IMAGE);
            }
        });

    }

    private void addStudentToDatabase() {
        String name = etName.getText().toString();
        String no = etNo.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Name can't be empty");
        } else if (no.isEmpty()) {
            etNo.setError("No can't be empty");
        } else {
            if (selectedImage != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference ref = storage.getReference("images/" + UUID.randomUUID().toString());

                ref.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String imageUrl = task.getResult().toString();
                                        addToDatabase(name, no, imageUrl);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(AddNewStudentActivity.this, "Upload image failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                addToDatabase(name, no, "");
            }
        }
    }

    public void addToDatabase(String name, String no, String imageUrl) {
        // Open database connection and add values to database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("students");

        String id = ref.push().getKey();
        Student s = new Student(id, name, no, imageUrl);

        ref.child(id).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddNewStudentActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(AddNewStudentActivity.this, "Failed " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            ivImage.setImageURI(selectedImage);
        }
    }
}