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
        getSupportActionBar().setTitle("About");
        //make the link clickable
        TextView linkTextView = findViewById(R.id.tv_website_url);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
