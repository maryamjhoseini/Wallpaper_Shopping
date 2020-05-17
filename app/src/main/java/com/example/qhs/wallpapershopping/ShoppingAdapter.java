package com.example.qhs.wallpapershopping;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.example.qhs.wallpapershopping.Fragments.Fragment_Shopping;
import com.example.qhs.wallpapershopping.Fragments.Fragment_favorite;
import com.example.qhs.wallpapershopping.Fragments.Fragment_gallery;
import com.example.qhs.wallpapershopping.R;
import com.example.qhs.wallpapershopping.network.Admin;
import com.example.qhs.wallpapershopping.network.NetRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import Data.DatabaseHandler;
import Model.ListItem;

public class ShoppingAdapter  extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {
    private Context context;
    private NetRequest request;
    private List<ListItem> listItems;
    private boolean mFlag;
    private int deleteId;
    private Admin admin;
    ItemCallback Listener;

    public  ShoppingAdapter(Context context, List listitem,ItemCallback Listener) {
        this.context = context;
        this.listItems = listitem;
        this.Listener=Listener;
        mFlag = false;
        request = new NetRequest(context);
        admin = Admin.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.shopping_item, parent, false);

        return new ShoppingAdapter.ViewHolder(rootView);
    }
    private boolean onBind;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DatabaseHandler db1 = new DatabaseHandler(context);
        final ListItem item = listItems.get(position);
        final List <String> image_link = new ArrayList <>(Arrays.asList(item.getImgLink().split("\\s*,\\s*")));
        String temp = image_link.get(0);
       int num = db1.getShoppingItemCount();
        //  temp = temp.replace("https", "http");
        if (URLUtil.isValidUrl(temp)) {
            Picasso.with(context)
                    .load(temp).resize(200, 200)
                    .into(holder.img);

            holder.counter.setText(String.valueOf(item.getCount_shop()));
             Log.d("shopcount1",String.valueOf(item.getCount_shop()));


            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    holder.counter.setText(String.valueOf(item.getCount_shop()-1));
                    item.setCount_shop(item.getCount_shop()-1);
                    Log.d("shopcount2",String.valueOf(item.getCount_shop()));

                    if (item.getCount_shop()<1) {
                        deleteId = Integer.parseInt(item.getId());
                        db1.deleteListItem(item.getId());
                        listItems.remove(position); // remove the item from list
                        notifyItemRemoved(position); // notify the adapter about the removed item
                        notifyItemRangeChanged(position, getItemCount());
                        deleteFromServer();
                    }
                    db1.updateListItem(item);
                    deleteFromServer();
                    //Todo: update count in site
                    Listener.TotalPrice();


                }
            });
            holder.counterTxt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    holder.counter.setText(String.valueOf(item.getCount_shop()+1));
                    item.setCount_shop(item.getCount_shop()+1);
                    Log.d("shopcount3",String.valueOf(item.getCount_shop()));
                    db1.updateListItem(item);

                    Listener.TotalPrice();

//
               }
            });
//
            holder.price.setText(String.valueOf(item.getPrice()));

            holder.name.setText(" نام محصول:" + item.getName());

        }
        else
        {
            db1.deleteListItem(item.getId());
            listItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

        }
    }
//TODO: endpoint
    private void deleteFromServer() {
        request.JsonObjectNetRequest("GET", "cocart/v1/get-cart", mShoppingProductCallback, admin.getAdminAuth());
    }

    private NetRequest.Callback<JSONObject> mShoppingProductCallback = new NetRequest.Callback<JSONObject>(){

        @Override
        public void onResponse(@NonNull JSONObject response) {
            Iterator<String> keys = response.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                try {
                    if (response.get(key) instanceof JSONObject) {
                        Log.d("SHOPPING ", key);

                        int productId = response.getJSONObject(key).getInt("product_id");
                        if (productId == deleteId){
                            request.JsonStringNetRequest("DELETE", "cocart/v1/item?cart_item_key="+ key);
                            break;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onError(String error) {
            Log.d("Server Error ", error);

        }
    };

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img;
        public TextView counter;
        public Button deleteBtn;
        public TextView price;
        public TextView name;
        public Button counterTxt;
     //   public TextView total;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            img=(ImageView) itemView.findViewById(R.id.imgF);
            counter=(TextView)itemView.findViewById(R.id.counter);
            deleteBtn=(Button)itemView.findViewById(R.id.deleteBtn);
            counterTxt=(Button)itemView.findViewById(R.id.counterTxt);
            price=(TextView)itemView.findViewById(R.id.price);
            name=(TextView)itemView.findViewById(R.id.name);
            //total=(TextView)itemView.findViewById(R.id.totalPrice);

        }
        @Override
        public void onClick(View view) {
            //  List<String> image_link = null;
            int Position=getAdapterPosition();
            ListItem item=listItems.get(Position);
            Bundle bundle = new Bundle();
            bundle.putString("id",item.getId());
            bundle.putString("name",item.getName());
            bundle.putString("description",item.getDescription());
            //convert string to array with , seperator
            List <String> image_link =new ArrayList<>(Arrays.asList(item.getImgLink().split("\\s*,\\s*")));

            bundle.putStringArrayList("imageJsonObj", (ArrayList <String>) image_link);
            Fragment fragment = new Fragment_gallery();
            fragment.setArguments(bundle);
            ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).addToBackStack(null).commit();


        }
    }
    public interface ItemCallback{
        void TotalPrice();
    }
}