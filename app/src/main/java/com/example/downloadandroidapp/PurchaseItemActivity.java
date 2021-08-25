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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static android.app.ProgressDialog.show;

public class PurchaseItemActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    BillingClient billingClient;
    ConsumeResponseListener listener;
    Button loadProduct;
    RecyclerView recyclerView;
    TextView textPremium;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_item);

        setupBillingClient();
        init();
    }
    private void init(){
        textPremium =(TextView)findViewById(R.id.txt_premium);
        loadProduct = (Button)findViewById(R.id.btn_load_product);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));
        //event
        loadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(billingClient.isReady())
                {
                    SkuDetailsParams params= SkuDetailsParams.newBuilder()
                            .setSkusList(Arrays.asList("jewel_of_time","sword_of_angle"))
                            .setType(BillingClient.SkuType.INAPP)
                            .build();
                    billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                            if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK)
                                loadProductToRecyclerView(list);
                            else
                                Toast.makeText(PurchaseItemActivity.this,"Error code"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }

    private void loadProductToRecyclerView(List<SkuDetails> list) {
        MyProductAdapter adapter = new MyProductAdapter(this,list,billingClient);
        recyclerView.setAdapter(adapter);
    }

    private void setupBillingClient(){
        listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
               if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK)
                   Toast.makeText( PurchaseItemActivity.this,"Consume Ok!",Toast.LENGTH_SHORT).show();
            }
        };
        billingClient= BillingClientSetup.getInstance(this,this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK)
                {
                    Toast.makeText(PurchaseItemActivity.this,"Success to connect billing",Toast.LENGTH_SHORT).show();
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            .getPurchasesList();
                    handleitemAlreadyPuchase(purchases);
                }
                else
                    Toast.makeText( PurchaseItemActivity.this,"Error code"+
                            billingResult.getResponseCode()
                            ,Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText( PurchaseItemActivity.this,"You are disconnect from Billing Service"
                        ,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void handleitemAlreadyPuchase(List<Purchase> purchases) {
        StringBuilder purchaseditem = new StringBuilder(textPremium.getText());//empty
        for (Purchase purchase : purchases) {
            if (purchase.getSku().equals("jewel_of_time"))//Consume
            {
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.consumeAsync(consumeParams, listener);
            }
            purchaseditem.append("\n" + purchase.getSku())
                    .append("\n");
        }
        textPremium.setText(purchaseditem.toString());
        textPremium.setVisibility(View.VISIBLE);
    }




    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK
        && list != null)
            handleitemAlreadyPuchase(list);
        else if  (billingResult.getResponseCode()==BillingClient.BillingResponseCode.USER_CANCELED)
            Toast.makeText(this,"User has been cancelled",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Error"+billingResult.getResponseCode(),Toast.LENGTH_SHORT).show();
    }
}