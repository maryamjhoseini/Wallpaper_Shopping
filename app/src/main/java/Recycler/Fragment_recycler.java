package Recycler;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.qhs.wallpapershopping.AuthHelper;
import com.example.qhs.wallpapershopping.R;


import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


import Model.ListItem;
import Model.RecyclerHorizentalItem;
import Ui.SpannableGridLayoutManager;


import static com.android.volley.toolbox.Volley.newRequestQueue;


public class Fragment_recycler extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerAdapter adapter;
    public List<ListItem> listItems;
    private List<JSONArray> imageList;
    // okhttp request
    private ProgressDialog mProgressDialog;
    //log in
    private AuthHelper mAuthHelper;
    private Menu mOptionsMenu;
    private Button profileBtn;

    //com.example.qhs.deydigital.Json parametr
    private final static String URL_products = "https://mobifytech.ir/wc-api/v3/products?filter[limit] =-1";
    private final static String URL_category = "https://mobifytech.ir/wc-api/v3/products?filter[categories]=";
    private static String URL;
    private final static String URL_category_product = "http://mobifytech.ir/wp-json/wc/v3/products?category=";
    private static String category_id;
    private final static String per_page = "&per_page=";
    private final static String per_page_number = "100";
    private final static String offset = "&offset=";
    private final static String offset_number = "0";
    private RequestQueue queue;
    public View view;
    public JSONArray image_series_json;
    private int response_number = 0;
    private int iteration_number;
    public HurlStack hurlStack;

    private RecyclerView recyclerViewHorizental;
    private RecyclerView.Adapter adapterHorizental;
    private List<RecyclerHorizentalItem> HorizentalItems = new ArrayList<>();

    public ProgressBar pgsBar;
    // public Context context;
    public String URL_complete;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

//        //Splash Screen
//        if (Splashscreen.Splash==0){
//            Intent intent = new Intent(this,Splashscreen.class);
//            startActivity(intent);
//            super.onCreate(savedInstanceState);
//            finish();
//            return;
//        }
//        //End Splash



        queue = newRequestQueue(getContext());

        mProgressDialog = new ProgressDialog(getContext());
        mAuthHelper = AuthHelper.getInstance(getContext());
        if (mAuthHelper.isLoggedIn()) {
            Log.d("USERNAME: ", "isloggedin");
            // setupView();
        }

        //Horizental RecyclerView
        //image for horizental com.example.qhs.deydigital.Recycler
        int logos[] = {
                R.drawable.logo1, R.drawable.logo12, R.drawable.logo3, R.drawable.logo6,
                R.drawable.logo8,R.drawable.logo5};
        //text for horizental com.example.qhs.deydigital.Recycler
        String txt[]={
                "سالن پذیرایی",
                "اتاق کودک",
                "پشت Tv",
                "اتاق خواب",
                "سه بعدی",
                "هنری",
        };

        recyclerViewHorizental = (RecyclerView) view.findViewById(R.id.reciclerViewHorizental);

        // add a divider after each item for more clarity
        //recyclerViewHorizental.addItemDecoration(new DividerItemDecoration(
        //      RecyclerActivity.this, LinearLayoutManager.HORIZONTAL));

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewHorizental.setLayoutManager(horizontalLayoutManager);

        for (int i = 0; i < logos.length; i++) {

            RecyclerHorizentalItem HItem = new RecyclerHorizentalItem(logos[i], txt[i]);

            HorizentalItems.add(HItem);
            //  adapterHorizental.notifyDataSetChanged();

        }
        adapterHorizental = new RecyclerViewHorizontalListAdapter(HorizentalItems, getContext());
        recyclerViewHorizental.setAdapter(adapterHorizental);


        //Retrieve Bundle value
        Bundle extras = this.getArguments();
        if (extras == null) {
            category_id = null;
            Log.d("key:", category_id);
        } else {
            category_id = extras.getString("key");
        }

        imageList = new ArrayList<JSONArray>();
        image_series_json = new JSONArray();

        ///com.example.qhs.deydigital.Recycler
        recyclerView = (RecyclerView) view.findViewById(R.id.reciclerViewID);
        recyclerView.setHasFixedSize(true);
        //********************
        SpannableGridLayoutManager gridLayoutManager = new
                SpannableGridLayoutManager(new SpannableGridLayoutManager.GridSpanLookup() {
            @Override
            public SpannableGridLayoutManager.SpanInfo getSpanInfo(int position)
            {
                if (position == 0) {
                    return new SpannableGridLayoutManager.SpanInfo(2, 2);
                    //this will count of row and column you want to replace
                } else {
                    return new SpannableGridLayoutManager.SpanInfo(1, 1);
                }
            }
        }, 3, 1f); // 3 is the number of coloumn , how nay to display is 1f

        recyclerView.setLayoutManager(gridLayoutManager);

        //*******************
        // recyclerView.setLayoutManager( new GridLayoutManager(this,3));
        // SnapHelper snapHelper = new PagerSnapHelper();
        // snapHelper.attachToRecyclerView(recyclerView);
        listItems = new ArrayList<>();
        adapter = new RecyclerAdapter(getContext(), listItems);
        recyclerView.setAdapter(adapter);

        try {
            category_Response_edited(URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // newMyResponse(URL_products);
        pgsBar = (ProgressBar) view.findViewById(R.id.pBar);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                pgsBar.setVisibility(view.VISIBLE);
            }
        }, 1000);
        // pgsBar.setVisibility(view.GONE);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Bundle extras = this.getArguments();
        String name_string = extras.getString("name");
        //Toolbar
        Toolbar toolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
        TextView title = (TextView) ((AppCompatActivity)getActivity()).findViewById(R.id.txtTitle);
        title.setText(name_string);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity)getActivity()).onBackPressed();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("localhost", session);
            }
        };
    }


    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void category_Response_edited(String url) throws MalformedURLException {

        imageList.clear();
        Bundle extras = this.getArguments();
        String count_string = extras.getString("count");
        String key_string = extras.getString("key");
//        int count_int = Integer.valueOf(count_string);

        String url_jwt = URL_category_product + key_string;

        JsonArrayRequest jsonArrayRequest;

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url_jwt, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    //JSONArray products = response.getJSONArray();
                    for (int i = 0; i < response.length(); i++) {

                        response_number = response.length();


                        image_series_json = response.getJSONObject(i).getJSONArray("images");
                        //viewDialog.hideDialog();

                        //Log.d("j**response number",String.valueOf(iteration_number) + "***"+String.valueOf(response_number) );
                        //get image urls and save in arraylist
                        imageList.add(image_series_json);

                        Log.d("response*****", response.getJSONObject(i).getString("name"));
                        ListItem item = new ListItem(
                                response.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("src"),
                                response.getJSONObject(i).getString("name"),
                                /*image_series_json*/
                                response.getJSONObject(i).getString("id"),
                                response.getJSONObject(i).getString("short_description"),
                                response.getJSONObject(i).getJSONArray("images"),
                                new ArrayList()
                        );


                        for (int u = 0; u < response.getJSONObject(i).getJSONArray("images").length(); u++) {
                            Log.d("imgsrc***", response.getJSONObject(i).getJSONArray("images").getJSONObject(u).getString("src"));
                        }


                        listItems.add(item);
                        adapter.notifyDataSetChanged();
                        pgsBar.setVisibility(view.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error:", error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("NoConnectionError", error.getMessage());
                } else if (error instanceof AuthFailureError) {
                    Log.e("AuthFailureError", error.getMessage());
                } else if (error instanceof ServerError) {
                    Log.e("ServerError", error.getMessage());
                } else if (error instanceof NetworkError) {
                    Log.e("NetworkError", error.getMessage());
                } else if (error instanceof ParseError) {
                    Log.e("ParseError", error.getMessage());
                }
            }
        }){

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                //params.put("Content-Type", "application/x-www-form-urlencoded");

                //  String access_token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9tb2JpZnl0ZWNoLmlyIiwiaWF0IjoxNTcxNTU5MTAxLCJuYmYiOjE1NzE1NTkxMDEsImV4cCI6MTU3MjE2MzkwMSwiZGF0YSI6eyJ1c2VyIjp7ImlkIjoiMSJ9fX0.9Cgl5ZrBMV_MZ-ojZWjlguxHwqT0IuB0MiMCSxIJX2k";
                // String creds = String.format("%s", access_token);

                // params.put("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9tb2JpZnl0ZWNoLmlyIiwiaWF0IjoxNTY3NTg4MDczLCJuYmYiOjE1Njc1ODgwNzMsImV4cCI6MTU2ODE5Mjg3MywiZGF0YSI6eyJ1c2VyIjp7ImlkIjoiMSJ9fX0.pP2G2lZJys5USenPHpvKxhy5ugH1xxWCDX2tSAikwfw");

                params.put("Authorization", "Bearer " + mAuthHelper.getIdToken());
                //params.replace("\n", "");
                return params;
            }
        };


        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(1000000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //requests.add(jsonArrayRequest);
        queue.add(jsonArrayRequest);

    }



}
