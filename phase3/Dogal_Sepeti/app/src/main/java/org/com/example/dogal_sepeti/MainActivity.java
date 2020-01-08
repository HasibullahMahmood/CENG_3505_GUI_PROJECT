package org.com.example.dogal_sepeti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {

    private ConstraintLayout main_layout;
    private ImageButton login_image_button;
    private LinearLayout welcome_linear_layout;
    private RecyclerView recyclerView_For_farmersProducts;
    private RecyclerViewAdapter recyclerView_AdapterFor_farmersProducts;
    private ArrayList<Category> categories_ArrayList;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerViewFor_category;
    private LinkedHashMap<String, Integer> categories_LinkedHashMap;
    private List<Product> farmersProducts_list;

    private ImageButton Login_image_button;
    private ImageButton filter_image_button;
    private Button sell_your_product_btn;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference databaseReference;
    private ImageButton ChatsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFields();

        displayCategories();

        displayFarmersProducts();


        ChatsBtn = findViewById(R.id.messages_image_button);
        ChatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(MainActivity.this, ChatsActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            }
        });

        welcome_linear_layout.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    sell_your_product_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendTheUserToSellYourProductsActivity();
                        }
                    });
                } else {
                    sell_your_product_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                        }
                    });
                }
            }
        };
        login_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSignUpActivity();

            }
        });

        updateProducts();
    }

    private void sendUserToSignUpActivity() {
        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }


    private void sendTheUserToSellYourProductsActivity() {
        List list = new ArrayList(categories_LinkedHashMap.keySet());
        ArrayList<String> categories = new ArrayList<String>(list);
        Intent intent = new Intent(getApplicationContext(), SellYourProductsActivity.class);
        intent.putExtra("categories", categories);
        startActivity(intent);
    }

    private void displayFarmersProducts() {

        //Creating list of products
        farmersProducts_list = new ArrayList<>();

        //farmersProducts_list.add(new FarmersProducts(R.drawable.fruit_image, "Muz", "Meyve", 12, "KG", "Çok taze meyve"));

        //Connecting the List to recyclerView of GridLayout
        recyclerView_For_farmersProducts = (RecyclerView) findViewById(R.id.recycler_view_for_sell_items);
        recyclerView_AdapterFor_farmersProducts = new RecyclerViewAdapter(this, farmersProducts_list);

        recyclerView_For_farmersProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView_For_farmersProducts.setAdapter(recyclerView_AdapterFor_farmersProducts);

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(currentUserId)) {
            updateUserStatus("online");
        }
        mAuth.addAuthStateListener(mAuthStateListener);
        CountDownTimer count = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                welcome_linear_layout.setVisibility(View.INVISIBLE);
                main_layout.setVisibility(View.VISIBLE);
            }
        }.start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!TextUtils.isEmpty(currentUserId)) {
            updateUserStatus("offline");
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initializeFields() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        login_image_button = findViewById(R.id.login_image_button);
        welcome_linear_layout = findViewById(R.id.welcome_linear_layout);
        main_layout = findViewById(R.id.constraint_main_layout);

        Login_image_button = findViewById(R.id.login_image_button);
        filter_image_button = findViewById(R.id.filter_image_button);
        sell_your_product_btn = findViewById(R.id.sell_your_product_btn);

        // setting MainActivity Background
        getWindow().setBackgroundDrawableResource(R.drawable.main_activity_bg);
    }


    private void displayCategories() {

        categories_LinkedHashMap = new LinkedHashMap<>();
        categories_LinkedHashMap.put("Meyve", R.drawable.fruit_image);
        categories_LinkedHashMap.put("Sebzeler", R.drawable.salad_image);
        categories_LinkedHashMap.put("Mantarlar", R.drawable.mushroom_image);
        categories_LinkedHashMap.put("Taneler", R.drawable.grain_image);
        categories_LinkedHashMap.put("Baklagille", R.drawable.legumes_image);
        categories_LinkedHashMap.put("Fındık", R.drawable.nuts_image);
        categories_LinkedHashMap.put("Tohumları", R.drawable.seed_image);
        categories_LinkedHashMap.put("Günlük Ürünler", R.drawable.dairy_image);
        categories_LinkedHashMap.put("Yumurtalar", R.drawable.eggs_image);
        categories_LinkedHashMap.put("Kümes hayvanları", R.drawable.poultry_image);

        //Creating array_list of Categories
        categories_ArrayList = new ArrayList<>();

        //Creating CategoryAdapter: is used to connect arrayList of "categories_ArrayList" to recycleView
        categoryAdapter = new CategoryAdapter(this, categories_ArrayList);

        //Connecting to Categories_Recycle_view_Bar
        recyclerViewFor_category = findViewById(R.id.recycler_view_for_categories);

        //change the recycleView to be horizontally scrolled using horizontal linearLayout.
        recyclerViewFor_category.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Setting Category_adapter "adapter" to recycleView
        recyclerViewFor_category.setAdapter(categoryAdapter);

        // Creating Categories
        for (String key : categories_LinkedHashMap.keySet()) {
            categories_ArrayList.add(new Category(key, categories_LinkedHashMap.get(key)));
        }
        categoryAdapter.notifyDataSetChanged();
    }

    public void updateProducts() {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                farmersProducts_list.clear();
                for (DataSnapshot itemOwner : dataSnapshot.getChildren()) {
                    for (DataSnapshot item : itemOwner.getChildren()) {
                        final ArrayList<String> images = new ArrayList<>();
                        Product it = new Product();

                        for (int i = 0; i < 4; i++) {
                            if (item.hasChild("image" + i)) {
                                images.add(item.child("image" + i).getValue().toString());
                            }
                        }
                        it.setImages(images);
                        it.setTitle(item.child("title").getValue().toString());
                        it.setCategory(item.child("category").getValue().toString());
                        it.setDescription(item.child("description").getValue().toString());
                        it.setAmount(item.child("amount").getValue().toString());
                        it.setSellerId(item.child("sellerId").getValue().toString());
                        it.setProductId(item.child("itemId").getValue().toString());

                        farmersProducts_list.add(it);


                    }
                }
                recyclerView_AdapterFor_farmersProducts = new RecyclerViewAdapter(MainActivity.this, farmersProducts_list);
                recyclerView_For_farmersProducts.setAdapter(recyclerView_AdapterFor_farmersProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("userState")
                .updateChildren(onlineStateMap);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

