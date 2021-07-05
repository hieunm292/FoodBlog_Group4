package com.example.jeremy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostToolbar=(Toolbar) findViewById(R.id.new_post_toolbar);
        // setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Jeremy Blog - Add New Post");

    }
}