package com.example.downloadandroidapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.example.downloadandroidapp.R;
import com.example.downloadandroidapp.listener.IRecyclerClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {

    AppCompatActivity appCompatActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;
    public MyProductAdapter(AppCompatActivity appCompatActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.appCompatActivity = appCompatActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(appCompatActivity.getBaseContext())
                .inflate(R.layout.layout_product_display,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_product_name.setText(skuDetailsList.get(position).getTitle());
        holder.txt_description.setText(skuDetailsList.get(position).getDescription());
        holder.txt_price.setText(skuDetailsList.get(position).getPrice());

        //Product click
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void OnClick(View view, int position) {
                // Launch billing flow
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(position))
                        .build();
                int reponse = billingClient.launchBillingFlow(appCompatActivity,billingFlowParams)
                        .getResponseCode();
                switch (reponse)
                {
                    case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                        Toast.makeText(appCompatActivity,"BILLING_UNAVAILABLE",Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                        Toast.makeText(appCompatActivity,"DEVELOPER_ERROR",Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                        Toast.makeText(appCompatActivity,"FEATURE_NOT_SUPPORTED",Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                        Toast.makeText(appCompatActivity,"ITEM_ALREADY_OWNED",Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                        Toast.makeText(appCompatActivity,"SERVICE_DISCONNECTED",Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                        Toast.makeText(appCompatActivity,"SERVICE_TIMEOUT",Toast.LENGTH_SHORT).show();
                        break;
                    case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                        Toast.makeText(appCompatActivity,"ITEM_UNAVAILABLE",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_product_name,txt_price,txt_description;
        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener){
            this.listener = listener;
        }


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_description = (TextView)itemView.findViewById(R.id.txt_description);
            txt_product_name = (TextView)itemView.findViewById(R.id.txt_product_name);
            txt_price= (TextView)itemView.findViewById(R.id.txt_price);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.OnClick(view,getAdapterPosition());

        }


    }
}
