package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Update_screen extends AppCompatActivity {

    private TextInputLayout update_LAY_storeName;
    private TextInputLayout update_LAY_description;
    private Button update_BTN_chooseImage;
    private Button update_BTN_upload;
    private Button update_BTN_finish;
    private ImageView update_IMG_picture;

    private CheckInputValue checkInputValue;
    private DatabaseReference mDatabase;

    private boolean isChoose = false;

    private String username;
    private boolean isCreatePage;

    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ArrayList<String> imageList;
    private Shop shop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_screen);

        checkInputValue = new CheckInputValue();
        imageList = new ArrayList<>();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        isCreatePage = intent.getBooleanExtra("createPage", false);


        findView();
        initButton();
    }

    private void initButton() {

        update_BTN_chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        update_BTN_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChoose) {
                    storeImage();
                }
            }
        });

        update_BTN_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInformation();
            }
        });
    }

    private void storeImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image");
        pd.show();


        StorageReference riversRef = storageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtention(imageUri));

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_LONG).show();

                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageList.add(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed To Uploaded", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            update_IMG_picture.setImageURI(imageUri);
            isChoose = true;
        }
    }

    private void findView() {
        update_LAY_description = findViewById(R.id.update_LAY_description);
        update_LAY_storeName = findViewById(R.id.update_LAY_storeName);
        update_BTN_chooseImage = findViewById(R.id.update_BTN_chooseImage);
        update_BTN_upload = findViewById(R.id.update_BTN_upload);
        update_BTN_finish = findViewById(R.id.update_BTN_finish);
        update_IMG_picture = findViewById(R.id.update_IMG_picture);

    }

    private void updateInformation() {
        if (!isCreatePage) {
            saveInDB();
        } else {
            updateDB();
        }

    }

    private void updateDB() {

    }

    private void saveInDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        checkInputValue();
        checkIfStoreNameExistAndAddToDB();
    }

    private void addShopToDB() {
        shop = new Shop(update_LAY_storeName.getEditText().getText().toString(),update_LAY_description.getEditText().getText().toString(),username,imageList);
        mDatabase.child("Confectioneries/").child(shop.getShopName()).setValue(shop);

        mDatabase.child("Users/").child(username + "/").child("createPage").setValue(true);

        openNewActivity();
    }

    private void openNewActivity() {
        Intent newIntent = new Intent(getApplicationContext(), Seller_screen.class);
        newIntent.putExtra("shopInfo", shop);
        startActivity(newIntent);
        finish();
    }

    private void checkInputValue() {
        if (!checkInputValue.validateName(update_LAY_storeName) | !checkInputValue.validateName(update_LAY_description)) {
            return;
        }
    }

    private void checkIfStoreNameExistAndAddToDB() {

        Query checkUser = mDatabase.orderByChild("shopName").equalTo(update_LAY_storeName.getEditText().getText().toString());

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    update_LAY_storeName.setError("A SHOP name already exists in the system");
                } else {
                    update_LAY_storeName.setError(null);
                    update_LAY_storeName.setErrorEnabled(false);

                    addShopToDB();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getFileExtention(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}