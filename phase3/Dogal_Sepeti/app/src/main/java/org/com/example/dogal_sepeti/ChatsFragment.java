package org.com.example.dogal_sepeti;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private View PrivateChatsView;
    private RecyclerView chatBox_recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserID = "";
    private DatabaseReference messagesRef, usersRef, productsRef;
    private String receiverKey;
    private String receiverImage;
    private ChatBoxAdapter chatBoxAdapter;
    private ArrayList<String> receiverKey_productID_ArrayList;
    private int downloadedDataIndex;

    private ArrayList<ChatBox> chatBoxes_list;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        { // Inflate the layout for this fragment
            PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                currentUserID = mAuth.getCurrentUser().getUid();
            }
            // to retrieve receiver id
            messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        }
        chatBoxes_list = new ArrayList<>();
        chatBox_recyclerView = PrivateChatsView.findViewById(R.id.chatBox_recyclerView);
        chatBoxAdapter = new ChatBoxAdapter(getContext(), chatBoxes_list);
        chatBox_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatBox_recyclerView.setAdapter(chatBoxAdapter);

        receiverKey_productID_ArrayList = new ArrayList<>();

        getChatBoxes();
        return PrivateChatsView;
    }

    public void getChatBoxes() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // check if there is any message or not
                if (dataSnapshot.hasChild("from")) {
                    String from = dataSnapshot.child("from").getValue().toString();
                    String to = dataSnapshot.child("to").getValue().toString();
                    String productID = dataSnapshot.child("productID").getValue().toString();
                    String receiverKey_productID = "";
                    if (currentUserID.equals(from) || currentUserID.equals(to)) {
                        if (currentUserID.equals(from)) {
                            receiverKey = to;
                        } else {
                            receiverKey = from;
                        }

                        receiverKey_productID = "" + receiverKey + "_" + productID;

                        if (!receiverKey_productID_ArrayList.contains(receiverKey_productID)) {
                            receiverKey_productID_ArrayList.add(receiverKey_productID);
                        }

                        retrieveReceiverAndProductInfo();
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveReceiverAndProductInfo() {
        RetrieveData retrieveDataTask = new RetrieveData();
        retrieveDataTask.execute();
    }


    private class RetrieveData extends AsyncTask<Void, Void, ArrayList<ChatBox>> {

        @Override
        protected ArrayList<ChatBox> doInBackground(Void... voids) {

            String receiverKey_productID = "";
            String receiverKey = "";
            String productID = "";
            for (; downloadedDataIndex < receiverKey_productID_ArrayList.size(); downloadedDataIndex++) {
                receiverKey_productID = receiverKey_productID_ArrayList.get(downloadedDataIndex);
                receiverKey = receiverKey_productID.substring(0, 28);
                productID = receiverKey_productID.substring(29, 49);

                Log.i("receiverKey", receiverKey);
                Log.i("productID", productID);
                final ChatBox chatBox = new ChatBox();
                final String finalReceiverKey = receiverKey;
                usersRef.child(receiverKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("image")) {
                            receiverImage = dataSnapshot.child("image").getValue().toString();
                        } else {
                            receiverImage = "https://firebasestorage.googleapis.com/v0/b/dogalsepetifinal.appspot.com/o/farmer.png?alt=media&token=6868cb59-7b90-4577-b259-2ddf55bc7770";
                        }
                        chatBox.setReceiverImage(receiverImage);
                        chatBox.setReceiverId(finalReceiverKey);
                        chatBox.setReceiverName(dataSnapshot.child("name").getValue().toString());
                        Log.d("Firebase data", "user data received");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final String finalProductID = productID;
                productsRef.child(receiverKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(finalProductID)) {
                            chatBox.setProductId(finalProductID);
                            chatBox.setProductName(dataSnapshot.child(finalProductID).child("title").getValue().toString());
                            chatBox.setProductImage(dataSnapshot.child(finalProductID).child("image0").getValue().toString());
                            chatBox.setProductPrice(dataSnapshot.child(finalProductID).child("amount").getValue().toString());
                            chatBox.setProductAvailability(dataSnapshot.child(finalProductID).child("state").getValue().toString());
                            Log.d("Firebase data", "product data received from receiver");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                productsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(finalProductID)) {
                            chatBox.setProductId(finalProductID);
                            chatBox.setProductName(dataSnapshot.child(finalProductID).child("title").getValue().toString());
                            chatBox.setProductImage(dataSnapshot.child(finalProductID).child("image0").getValue().toString());
                            chatBox.setProductPrice(dataSnapshot.child(finalProductID).child("amount").getValue().toString());
                            chatBox.setProductAvailability(dataSnapshot.child(finalProductID).child("state").getValue().toString());
                            Log.d("Firebase data", "product data received from current user");
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Log.d("Firebase data", "chat box added");
                chatBoxes_list.add(chatBox);
            }
            return chatBoxes_list;
        }

        @Override
        protected void onPostExecute(ArrayList<ChatBox> chatBoxes_list) {
            super.onPostExecute(chatBoxes_list);
            Log.d("Firebase data", "adapter will be created");
            chatBoxAdapter = new ChatBoxAdapter(getContext(), chatBoxes_list);
            chatBox_recyclerView.setAdapter(chatBoxAdapter);
        }
    }
}