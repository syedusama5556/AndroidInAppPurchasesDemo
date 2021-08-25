package com.example.downloadandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    CardView btnPurchase,btnSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        btnPurchase =(CardView)findViewById(R.id.card_purchase_item);
        btnSubscribe = (CardView)findViewById(R.id.card_subscribe_item);

        //Event
        btnPurchase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,PurchaseItemActivity.class
                ));
            }
        });

        btnSubscribe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,SubscribeActivity.class
                ));
            }
        });
    }
}