package hcmute.edu.vn.mobile_store.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.mobile_store.adapter.CategoryListAdapter;
import hcmute.edu.vn.mobile_store.models.Bill;
import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.adapter.ProductListCustomerAdapter;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.Category;
import hcmute.edu.vn.mobile_store.models.Product;
import hcmute.edu.vn.mobile_store.utils.SharedPrefs;

public class HomeActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;
    ImageButton btnSearch;
    EditText edtSearch;

    //broadcast
    private final String SEND_ORDER_ACTION = "hcmute.edu.vn.mobile_store.ACTION";
    private final String SEND_ORDER_KEY = "hcmute.edu.vn.mobile_store.KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        // send broadcast to shipper
        List<Bill> billList  = dbHelper.getAcceptedBills();
        Intent sendBroadCastIntent = new Intent(SEND_ORDER_ACTION);
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(billList).getAsJsonArray();
        String strJson = jsonArray.toString();

        sendBroadCastIntent.putExtra(SEND_ORDER_KEY, strJson);
        sendBroadcast(sendBroadCastIntent);
        System.out.println(billList);
        System.out.println("Send broadcast success");

        TextView tvHello = findViewById(R.id.tvHello);

        tvHello.setText("Xin chào " + SharedPrefs.getInstance().get("current_name", String.class) + "!");

        ViewFlipper viewFlipper = findViewById(R.id.viewFlipperAdvertise);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.startFlipping();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.bill:
                        startActivity(new Intent(getApplicationContext(), BillListCustomerActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.cart:
                        startActivity(new Intent(getApplicationContext(), CartActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        loadData();
        edtSearch = (EditText) findViewById(R.id.edSearch);

        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }
    private void search(){
        String search = edtSearch.getText().toString();
        if(search.equals("")){
            Toast.makeText(this,"Nhập nội dung tìm kiếm",Toast.LENGTH_SHORT).show();
        }else{
            Intent i = new Intent(HomeActivity.this,ProductListCustomerActivity.class);
            i.putExtra("search",search);
            i.putExtra("current_category_id",0);
            startActivity(i);
        }
    }
    private void loadData()
    {
        List<Product> lProduct;
        int numberProduct = dbHelper.countProduct();

        if (numberProduct>10)
            lProduct = dbHelper.getProducts().subList(numberProduct - 11 , numberProduct - 1);
        else
            lProduct = dbHelper.getProducts();

        GridView gridView = (GridView) findViewById(R.id.gvProductListCustomer);
        gridView.setAdapter(new ProductListCustomerAdapter(getApplicationContext(), lProduct));

        List<Category> lCategory = dbHelper.getCategories();
        GridView gridViewCategory = (GridView) findViewById(R.id.gvCategoryListCustomer);
        gridViewCategory.setAdapter(new CategoryListAdapter(getApplicationContext(), lCategory));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intentProduct = new Intent(HomeActivity.this , ProductActivity.class);
                intentProduct.putExtra("current_product_id", String.valueOf(parent.getItemIdAtPosition(position)));
                startActivity(intentProduct);
            }
        });

        gridViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long categoryId = parent.getItemIdAtPosition(position);
                startActivity(new Intent(HomeActivity.this , ProductListCustomerActivity.class).putExtra("current_category_id", String.valueOf(categoryId)));
            }
        });
    }
}