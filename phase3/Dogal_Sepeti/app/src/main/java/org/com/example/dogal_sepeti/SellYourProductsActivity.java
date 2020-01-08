package org.com.example.dogal_sepeti;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class SellYourProductsActivity extends AppCompatActivity {
    static ArrayList<Uri> uri_ArrayList;
    private int CAPTURE_IMAGE_RC = 1;
    private int PICK_IMAGE_RC = 2;
    private int tag;
    private ImageView imgOne;
    private ImageView imgTwo;
    private ImageView imgThree;
    private ImageView imgFour;
    private int imageCounter = 0;
    private Spinner category_spinner;
    private Spinner unit_spinner;
    private ArrayList<String> units_ArrayList;
    private ArrayList<String> categories_ArrayList;
    private ArrayAdapter<String> unit_Adapter;
    private Toolbar mToolbar;
    private String productUnit;
    private String productTitle;
    private String productPrice;
    private String productDescription;
    private String productCategory;
    private EditText titleEditTest;
    private EditText priceEditText;
    private EditText descriptionEditText;
    private Button share_btn;
    private Uri capturedImage;
    private FirebaseAuth mAuth;
    private DatabaseReference productsFirebaseRef;
    private StorageReference storageReference;
    private int uploadedImagesCount = 0;
    private String randomItemId;
    private HashMap<String, Object> productDetail_Map;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_your_product);

        initializeFields();
        displayUnits();
        displayCategories();
        setToolBar();

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productTitle = titleEditTest.getText().toString();
                productPrice = priceEditText.getText().toString();
                productDescription = descriptionEditText.getText().toString();

                if (!isEmpty()) {
                    saveDataToFirebase();
                }
            }
        });
    }


    private boolean isEmpty() {
        if (uri_ArrayList.size() == 0) {
            openDialog("Add First Photo!");
            return true;
        } else if (TextUtils.isEmpty(productTitle)) {
            openDialog("Enter title");
            return true;
        } else if (TextUtils.isEmpty(productCategory)) {
            openDialog("Choose Category");
            return true;
        } else if (TextUtils.isEmpty(productPrice)) {
            openDialog("Add Price");
            return true;
        } else if (TextUtils.isEmpty(productUnit)) {
            openDialog("Choose Unit");
            return true;
        } else if (TextUtils.isEmpty(productDescription)) {
            openDialog("Add description");
            return true;
        }

        return false;
    }

    private void displayCategories() {
        categories_ArrayList = getIntent().getStringArrayListExtra("categories");
        Collections.sort(categories_ArrayList);
        categories_ArrayList.add(0, "Kategori Seçiniz");
        category_spinner = findViewById(R.id.category_spinner);
        ArrayAdapter<String> categories_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories_ArrayList);
        category_spinner.setAdapter(categories_adapter);
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    productCategory = categories_ArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.sell_your_product_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Ürünlerinizi Sat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void displayUnits() {

        unit_spinner = findViewById(R.id.unit_spinner);
        defineUnits();
        unit_Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units_ArrayList);
        unit_spinner.setAdapter(unit_Adapter);

        unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    productUnit = units_ArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void CaptureImg(final View view) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a photo by:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (uri_ArrayList.size() <= 4) {
                    if (options[item].equals("Take Photo")) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, CAPTURE_IMAGE_RC);
                            tag = Integer.parseInt(view.getTag().toString());
                        }
                    } else if (options[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE_RC);
                        tag = Integer.parseInt(view.getTag().toString());

                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fotoğraf ekleme sınırına ulaştınız", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_RC || requestCode == CAPTURE_IMAGE_RC) && resultCode == Activity.RESULT_OK) {
            capturedImage = data.getData();

            switch (tag) {
                case 1:
                    imgOne.setImageResource(R.drawable.tick_mark_image);
                    imgOne.setClickable(false);
                    uri_ArrayList.add(capturedImage);
                    imageCounter++;
                    break;
                case 2:
                    imgTwo.setImageResource(R.drawable.tick_mark_image);
                    imgTwo.setClickable(false);
                    uri_ArrayList.add(capturedImage);
                    imageCounter++;
                    break;
                case 3:
                    imgThree.setImageResource(R.drawable.tick_mark_image);
                    imgThree.setClickable(false);
                    uri_ArrayList.add(capturedImage);
                    imageCounter++;
                    break;
                case 4:
                    imgFour.setImageResource(R.drawable.tick_mark_image);
                    imgFour.setClickable(false);
                    uri_ArrayList.add(capturedImage);
                    imageCounter++;
                    break;
            }
        }
    }


    private void defineUnits() {
        units_ArrayList = new ArrayList<>();
        units_ArrayList.add("Gram");
        units_ArrayList.add("Kilogram");
        units_ArrayList.add("Ton");
        units_ArrayList.add("Tane");

        Collections.sort(units_ArrayList);
        units_ArrayList.add(0, "Birim seçiniz");
    }

    private void initializeFields() {
        progressDialog = new ProgressDialog(this);
        productDetail_Map = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
        productsFirebaseRef = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("products");
        randomItemId = productsFirebaseRef.push().getKey();
        uri_ArrayList = new ArrayList<>(4);

        titleEditTest = findViewById(R.id.title_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);

        imgOne = findViewById(R.id.img1);
        imgTwo = findViewById(R.id.img2);
        imgThree = findViewById(R.id.img3);
        imgFour = findViewById(R.id.img4);


        share_btn = findViewById(R.id.share_btn);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void saveDataToFirebase() {
        progressDialog.setTitle("Uploading product data");
        progressDialog.setMessage("Please wait, your product data is uploading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        for (int i = 0; i < uri_ArrayList.size(); i++) {
            uploadImage(i);
        }
    }

    private void uploadImage(final int num) {
        final StorageReference fileReference = storageReference.child("products/" + System.currentTimeMillis()
                );
        fileReference.putFile(uri_ArrayList.get(num))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);

                                productDetail_Map.put("image" + num, url);

                                if ((uploadedImagesCount + 1) == uri_ArrayList.size()) {
                                    SendLink();
                                } else {
                                    uploadedImagesCount++;
                                }
                            }
                        });
                    }

                });

    }

    private void SendLink() {
        //productsFirebaseRef.child(mAuth.getUid()).child(randomItemId).updateChildren(hashMap);
        productDetail_Map.put("state", "Available");
        productDetail_Map.put("amount", productUnit + " | " + productPrice + "TL");
        productDetail_Map.put("category", productCategory);
        productDetail_Map.put("description", productDescription);
        productDetail_Map.put("title", productTitle);
        productDetail_Map.put("sellerId", mAuth.getUid());
        productDetail_Map.put("itemId", randomItemId);

        productsFirebaseRef.child(mAuth.getUid()).child(randomItemId).updateChildren(productDetail_Map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SellYourProductsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    /*//To get the extension of the file: JPEG, ..
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }*/

    public void openDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SellYourProductsActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Got it",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}