package org.com.example.dogal_sepeti;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.ChatBoxViewHolder> {
    private Context mContext;
    private ArrayList<ChatBox> chatBox_ArrayList;
    private ConstraintLayout chatBox_layout;

    public ChatBoxAdapter(Context context, ArrayList<ChatBox> chatBox_ArrayList) {
        this.mContext = context;
        this.chatBox_ArrayList = chatBox_ArrayList;
    }

    @Override
    public ChatBoxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.users_display_layout, viewGroup, false);
        return new ChatBoxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxViewHolder holder, final int position) {

        holder.productOwnerName.setText(chatBox_ArrayList.get(position).getReceiverName());
        Picasso.get().load(chatBox_ArrayList.get(position).getReceiverImage()).into(holder.profileImage);
        Picasso.get().load(chatBox_ArrayList.get(position).getProductImage()).into(holder.productImage);
        holder.productName.setText(chatBox_ArrayList.get(position).getProductName());
        holder.productAvailability.setText(chatBox_ArrayList.get(position).getProductAvailability());
        holder.timeTextView.setText(chatBox_ArrayList.get(position).getTimeTextView());

        holder.chatBox_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("message_receiver_id", chatBox_ArrayList.get(position).getReceiverId());
                chatIntent.putExtra("message_receiver_name", chatBox_ArrayList.get(position).getReceiverName());
                chatIntent.putExtra("message_receiver_photo", chatBox_ArrayList.get(position).getReceiverImage());

                chatIntent.putExtra("product_photo", chatBox_ArrayList.get(position).getProductImage());
                chatIntent.putExtra("productId", chatBox_ArrayList.get(position).getProductId());
                chatIntent.putExtra("productName", chatBox_ArrayList.get(position).getProductName());
                chatIntent.putExtra("productPrice", chatBox_ArrayList.get(position).getProductPrice());

                mContext.startActivity(chatIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatBox_ArrayList.size();
    }


    public static class ChatBoxViewHolder extends RecyclerView.ViewHolder {
        CircleImageView productImage, profileImage;
        TextView productName, timeTextView, productOwnerName, productAvailability;
        ConstraintLayout chatBox_layout;

        public ChatBoxViewHolder(@NonNull final View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImageHere);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            productName = itemView.findViewById(R.id.productName);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            productOwnerName = itemView.findViewById(R.id.productOwnerName);
            productAvailability = itemView.findViewById(R.id.availabilityTextView);
            chatBox_layout = itemView.findViewById(R.id.chatBox_layout);

        }

    }
}
