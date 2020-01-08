package org.com.example.dogal_sepeti;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView Name, Category, Amount, Description;
    private TextView providerEmail;
    private ImageView Image;
    private ArrayList<String> images;
    private Button sendMessageBtn;
    private String productName;
    private String productCategory;
    private String sellerName;
    private String productPricePerUnit;
    private String productDescription;
    private String sellerId;
    private String productId;
    private String sellerImage;
    private String productPhoto;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        Intent intent = getIntent();

        productName = intent.getExtras().getString("Name");
        images = intent.getExtras().getStringArrayList("images_ArrayList");

        productCategory = intent.getExtras().getString("Category");

        productPricePerUnit = intent.getExtras().getString("Amount");
        productDescription = intent.getExtras().getString("Description");

        sellerId = intent.getExtras().getString("sellerId");
        productId = intent.getExtras().getString("productId");
        productPhoto = images.get(0);

        Name = findViewById(R.id.productName);
        Amount = findViewById(R.id.productAmount);
        Description = findViewById(R.id.productDescription);

        sendMessageBtn = findViewById(R.id.sendMessageBtn);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
              if (sellerId.equals(mAuth.getCurrentUser().getUid())) {
                sendMessageBtn.setVisibility(View.INVISIBLE);
            }
        }


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                } else {
                    onSendMessageBtnClicked();
                }
            }
        });

        Name.setText(productName);
        Amount.setText(productPricePerUnit);
        Description.setText(productDescription);

        ViewPager viewPager = findViewById(R.id.imageSlider);
        ImageAdapter adapter = new ImageAdapter(this, images);
        viewPager.setAdapter(adapter);

        getSellerDetails();

    }

    public void getSellerDetails() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + sellerId);
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot productOwner) {
                sellerName = productOwner.child("name").getValue().toString();
                Log.i("sellerNm", sellerName);
                if (productOwner.hasChild("image")) {
                    sellerImage = productOwner.child("image").getValue().toString();
                } else {
                    sellerImage = "https://firebasestorage.googleapis.com/v0/b/dogalsepetifinal.appspot.com/o/products%2Fproducts%2Ffarmer.png?alt=media&token=c7885434-8a93-4574-9c19-759c233f78cd";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ItemDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onSendMessageBtnClicked() {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("message_receiver_id", sellerId);
        intent.putExtra("message_receiver_name", sellerName);
        intent.putExtra("message_receiver_photo", sellerImage);
        intent.putExtra("productId", productId);
        intent.putExtra("productName", productName);
        intent.putExtra("productPrice", productPricePerUnit);
        intent.putExtra("product_photo", productPhoto );

        startActivity(intent);

    }


}
