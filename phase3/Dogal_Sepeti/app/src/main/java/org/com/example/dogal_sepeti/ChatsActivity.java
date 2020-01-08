package org.com.example.dogal_sepeti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ChatsActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{
    private Toolbar mToolbar;
    private ImageButton homeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        setToolBar();

        homeBtn = findViewById(R.id.home_image_button);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.chats_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chats");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
