package org.com.example.dogal_sepeti;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<String> mImagesIds;

    ImageAdapter(Context context, ArrayList<String> images_ArrayList) {
        mContext = context;
        mImagesIds = images_ArrayList;
    }

    @Override
    public int getCount() {
        return mImagesIds.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(mImagesIds.get(position)).into(imageView);
        container.addView(imageView,0);
        return imageView;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
