package com.oneous.currencyconverter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.oneous.currencyconverter.App;
import com.oneous.currencyconverter.R;

public class MainActivity extends AppCompatActivity {
    public static final String FLAG_COMMIT_FRAGMENT = "commitFragment";

    public static Intent getStartIntent(Context context, boolean commitFragment) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(FLAG_COMMIT_FRAGMENT, commitFragment);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.getAppComponent(this).inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        boolean commitFragment = intent.getBooleanExtra(FLAG_COMMIT_FRAGMENT, true);
        if (savedInstanceState == null && commitFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root, new ConversionFragment())
                    .commit();
        }
    }
}
