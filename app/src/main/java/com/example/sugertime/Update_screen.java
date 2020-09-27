package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.location.FusedLocationProviderClient;

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

    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FusedLocationProviderClient fusedLocationProviderClient;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        findView();
        initButton();

        Bundle data = getIntent().getExtras();
        shop = (Shop) data.getSerializable("shopInfo");

        if (shop == null) {
            shop = new Shop();
            findStoreInDB();

            mDatabase.child("Users/").child(username + "/").child("createPage").setValue(true);
        } else {
            showDetilesOnScreen();
        }
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
                if (isChoose) {
                    storeImage();
                }
            }
        });

        update_BTN_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDB();
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
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


    private void findStoreInDB() {
        mDatabase.child("Confectioneries/").child(username + "/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shop.setOwner(snapshot.child("owner").getValue(String.class));
                shop.setDescription(snapshot.child("description").getValue(String.class));
                shop.setShopName(snapshot.child("shopName").getValue(String.class));
                shop.setImageList(new ArrayList<String>());

                showDetilesOnScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateDB() {
        if (!shop.getShopName().equals(update_LAY_storeName.getEditText().getText().toString())) {
            update_LAY_storeName.setError("YOU CAN'T CHANGE THE NAME OF YOUR SHOP");
            update_LAY_storeName.getEditText().setText(shop.getShopName());
        } else {

            if (!shop.getDescription().equals(update_LAY_description.getEditText().getText().toString())) {
                shop.setDescription(update_LAY_description.getEditText().getText().toString());
                checkInputValue();
                mDatabase.child("Confectioneries/").child(shop.getOwner() + "/").child("description/").setValue(update_LAY_description.getEditText().getText().toString());
            }

            if (imageList.size() != 0) {
                for (int i = 0; i < imageList.size(); i++) {
                    shop.getImageList().add(imageList.get(i));
                    mDatabase.child("Confectioneries/").child(shop.getOwner() + "/").child("imageList/")
                            .child("" + (shop.getImageList().size() - 1) + "/").setValue(imageList.get(i));
                }
            }
            openNewActivity();
        }
    }

    private void showDetilesOnScreen() {
        update_LAY_storeName.getEditText().setText(shop.getShopName());
        update_LAY_description.getEditText().setText(shop.getDescription());
    }

    private void openNewActivity() {
        Intent newIntent = new Intent(getApplicationContext(), ShopPage_screen.class);
        newIntent.putExtra("shopInfo", shop);
        startActivity(newIntent);
        finish();
    }

    private void checkInputValue() {
        if (!checkInputValue.validateName(update_LAY_storeName) | !checkInputValue.validateName(update_LAY_description)) {
            return;
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}