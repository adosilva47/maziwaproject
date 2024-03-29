package com.lastmilesale.android.mobileapps.lastmile.Activities.Main;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lastmilesale.android.mobileapps.lastmile.Objects.Order;
import com.lastmilesale.android.mobileapps.lastmile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetOrdersTask extends AsyncTask<Void, Integer, Integer> {

    private String user_id,API_PENDING,API_RECEIVED,API_ONTRANSIT,API_DELIVERED;
    private View rootView;
    private Context context;
    private RecyclerView ordersRecyclerView;
    private RecyclerView.LayoutManager recyclerViewlayoutManager;
    private Response.ErrorListener errorListener;
    private Response.Listener listener;
    private List<Order> orderList;

    //RecyclerView.Adapter homeSchedulesFeedRecyclerAdapter;

    public GetOrdersTask(Context context,View rootView, String user_id){
        this.user_id = user_id;
        this.context = context;
        this.rootView = rootView;
        this.API_PENDING = "http://lastmilesale.com/api/orders/pending/"+user_id;
        this.API_RECEIVED = "http://lastmilesale.com/api/orders/received/"+user_id;
        this.API_ONTRANSIT = "http://lastmilesale.com/api/orders/ontransit/"+user_id;
        this.API_DELIVERED = "http://lastmilesale.com/api/orders/delivered/"+user_id;
        this.ordersRecyclerView = (RecyclerView) rootView.findViewById(R.id.orders_recycler_view);
        orderList = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        RequestQueue queue = Volley.newRequestQueue(context);
        GetOrdersRequest getOrdersRequest = new GetOrdersRequest(API_PENDING,user_id,listener,errorListener);
        queue.add(getOrdersRequest);
        GetProductsRequest getReceivedRequest = new GetProductsRequest(API_RECEIVED,user_id,listener,errorListener);
        queue.add(getReceivedRequest);
        GetProductsRequest getOnTransitRequest = new GetProductsRequest(API_ONTRANSIT,user_id,listener,errorListener);
        queue.add(getOnTransitRequest);
        GetProductsRequest getDeliveredRequest = new GetProductsRequest(API_DELIVERED,user_id,listener,errorListener);
        queue.add(getDeliveredRequest);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ordersRecyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(context);
        ordersRecyclerView.setLayoutManager(recyclerViewlayoutManager);
        errorListener = initializeErrorListener();
        listener = initializeResponseListener();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


    protected Response.ErrorListener initializeErrorListener(){
        Response.ErrorListener localErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError){
                    //SETTING UP MESSAGE
                    String msg = context.getResources().getString(R.string.timeout_error);
                    Snackbar snack = Snackbar.make(rootView.findViewById(R.id.orders_fab), msg, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();

                }else if (error instanceof AuthFailureError){
                    //SETTING UP MESSAGE
                    String msg = context.getResources().getString(R.string.authentication_error);
                    Snackbar snack = Snackbar.make(rootView.findViewById(R.id.orders_fab), msg, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();


                }else if(error instanceof NetworkError){
                    //SETTING UP MESSAGE
                    String msg = context.getResources().getString(R.string.network_error);
                    Snackbar snack = Snackbar.make(rootView.findViewById(R.id.orders_fab), msg, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();

                }else if(error instanceof ServerError){
                    //SETTING UP MESSAGE
                    String msg = context.getResources().getString(R.string.serverbusy_error);
                    Snackbar snack = Snackbar.make(rootView.findViewById(R.id.orders_fab), msg, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();

                }else if(error instanceof ParseError){
                    //SETTING UP MESSAGE
                    String msg = context.getResources().getString(R.string.parse_error);
                    Snackbar snack = Snackbar.make(rootView.findViewById(R.id.orders_fab), msg, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();
                }
            }
        };
        return localErrorListener;
    }

    protected Response.Listener<String> initializeResponseListener(){
        Response.Listener<String> localResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    //JSON_PARSE_DATA_AFTER_WEBCALL(jsonArray);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = null;
                        Order order = null;
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            order = new Order(jsonObject.getString("id"),jsonObject.getString("product_id"),jsonObject.getString("product_name"),jsonObject.getString("supplier_name"),jsonObject.getString("no_of_dozens"),jsonObject.getString("no_of_pieces"),jsonObject.getString("no_of_cartons"),jsonObject.getString("order_status"),jsonObject.getString("product_image"),jsonObject.getString("price"));
                            orderList.add(order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    ((OrdersActivity)context).ordersRecyclerAdapter = new OrdersRecyclerAdapter(context,user_id,orderList,"pending");
                    ordersRecyclerView.setAdapter(((OrdersActivity)context).ordersRecyclerAdapter);
                } catch (JSONException em) {
                    em.printStackTrace();
                    String b_msg = "Temporary error occurred, try again later";
                    Snackbar snack = Snackbar.make(rootView.findViewById(R.id.orders_fab), b_msg, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();
                }
            }
        };

        return localResponseListener;
    }

}

class GetOrdersRequest extends StringRequest{
    private Map<String, String> params;

    public GetOrdersRequest(String API, String user_id, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET, API, listener, errorListener);
        params = new HashMap<>();
        params.put("user_id",user_id);
    }


    public Map<String, String> getParams(){
        return params;
    }
}
