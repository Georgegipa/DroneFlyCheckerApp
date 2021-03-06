package com.example.flychecker;

import android.os.Bundle;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.about));
        setLinks();
        //set version
        TextView version = findViewById(R.id.tv_version_number);
        version.setText(BuildConfig.VERSION_NAME);
    }

    //make the links in about activity clickable
    private void setLinks()
    {
        TextView linkTextView = findViewById(R.id.tv_website_url);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView linkTextView2 = findViewById(R.id.tv_cloud_api_url);
        linkTextView2.setMovementMethod(LinkMovementMethod.getInstance());
        TextView linkTextView3 = findViewById(R.id.tv_icons_website_url);
        linkTextView3.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
