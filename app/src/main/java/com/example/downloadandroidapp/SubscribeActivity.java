package com.example.downloadandroidapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.downloadandroidapp.Adapter.MyProductAdapter;
import com.example.downloadandroidapp.utils.BillingClientSetup;

import java.util.Arrays;
import java.util.List;

public class SubscribeActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    Button loadProduct;
    RecyclerView recyclerView;
    TextView textPremium;
    private void init(){
        textPremium =(TextView)findViewById(R.id.txt_premium);
        loadProduct = (Button)findViewById(R.id.btn_load_product);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

    }


    private void setupBillingClient(){
       acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
           @Override
           public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
               if (billingResult.getResponseCode()== BillingClient.BillingResponseCode.OK)
                   textPremium.setVisibility(View.VISIBLE);
           }
       };
        billingClient= BillingClientSetup.getInstance(this,this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK)
                {
                    Toast.makeText(SubscribeActivity.this,"Success to connect billing",Toast.LENGTH_SHORT).show();
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                            .getPurchasesList();
                    if (purchases.size()>0)
                    {
                        recyclerView.setVisibility(View.GONE);
                        for (Purchase purchase:purchases)
                            handleitemAlreadyPuchase(purchase);

                    }
                    else {
                        textPremium.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadAllSubscribePackage();
                    }

                }
                else
                    Toast.makeText( SubscribeActivity.this,"Error code"+
                                    billingResult.getResponseCode()
                            ,Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText( SubscribeActivity.this,"You are disconnect from Billing Service"
                        ,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadAllSubscribePackage() {
        if (billingClient.isReady())
        {
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("vvip_2020"))
                    .setType(BillingClient.SkuType.SUBS)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK)
                    {
                        MyProductAdapter adapter = new MyProductAdapter(SubscribeActivity.this,list,billingClient);
                        recyclerView.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(SubscribeActivity.this,"Error"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(SubscribeActivity.this,"Billing Client not ready",Toast.LENGTH_SHORT).show();
        }


    }

    private void handleitemAlreadyPuchase( Purchase purchases) {
       if (purchases.getPurchaseState()==Purchase.PurchaseState.PURCHASED)
       {
           if (!purchases.isAcknowledged())
           {
               AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                       .setPurchaseToken(purchases.getPurchaseToken())
                       .build();
               billingClient.acknowledgePurchase(acknowledgePurchaseParams,acknowledgePurchaseResponseListener);
           }
           else
           {

               textPremium.setVisibility(View.VISIBLE);
               textPremium.setText("You are Premium !!!");

           }
       }
    }




    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK
                && list != null)
        {
            for (Purchase purchase:list)
                handleitemAlreadyPuchase(purchase);
        }
        else if  (billingResult.getResponseCode()==BillingClient.BillingResponseCode.USER_CANCELED)
            Toast.makeText(this,"User has been cancelled",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Error"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        setupBillingClient();
        init();
    }

}