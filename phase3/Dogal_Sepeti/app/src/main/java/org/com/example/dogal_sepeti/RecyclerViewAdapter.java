package org.com.example.dogal_sepeti;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Product> mData;

    public RecyclerViewAdapter(Context mContext, List<Product> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.item_title.setText(mData.get(position).getTitle());

        Picasso.get().load(mData.get(position).getImages().get(0)).fit().centerCrop().into(holder.img_item_thumbnail);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                intent.putExtra("Name", mData.get(position).getTitle());
                intent.putExtra("Amount", mData.get(position).getAmount());
                intent.putExtra("Description", mData.get(position).getDescription());
                intent.putExtra("images_ArrayList", mData.get(position).getImages());
                intent.putExtra("productId", mData.get(position).getProductId());
                intent.putExtra("sellerId", mData.get(position).getSellerId());

                mContext.startActivity(intent);
                Toast.makeText(mContext.getApplicationContext(), mData.get(position).getTitle(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;
        ImageView img_item_thumbnail;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            item_title = (TextView) itemView.findViewById(R.id.itemTitle);
            img_item_thumbnail = (ImageView) itemView.findViewById(R.id.itemImage);
            cardView = (CardView) itemView.findViewById(R.id.cardView);


        }
    }
}

