package hcmute.edu.vn.mobile_store.customer_area;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import hcmute.edu.vn.mobile_store.DatabaseHelper;
import hcmute.edu.vn.mobile_store.adapter.ProductListCustomerAdapter;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.Product;

public class ProductListCustomerActivity extends AppCompatActivity {
    int categoryId;
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_customer);
        categoryId = Integer.parseInt(getIntent().getStringExtra("current_category_id"));
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());



        loadData();
    }

    private void loadData()
    {
        List<Product> lProduct = dbHelper.getProductsByCategoryId(categoryId);
        final ListView listView = (ListView) findViewById(R.id.lvProduct);
        listView.setAdapter(new ProductListCustomerAdapter(getApplicationContext(), lProduct));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long productId = parent.getItemIdAtPosition(position);

                Intent intentProduct = new Intent(ProductListCustomerActivity.this , ProductActivity.class);
                intentProduct.putExtra("current_product_id", String.valueOf(parent.getItemIdAtPosition(position)));
                startActivity(intentProduct);
            }
        });
    }
}