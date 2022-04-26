package hcmute.edu.vn.mobile_store.customer_area;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.adapter.ProductListCustomerAdapter;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.Product;

public class ProductListCustomerActivity extends AppCompatActivity {
    int categoryId;
    DatabaseHelper dbHelper= null;
    ImageButton btnFind;
    EditText edFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_customer);
        String search = getIntent().getStringExtra("search");
        if(search==null)
            search ="";
        edFind=(EditText)findViewById(R.id.edFind);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        if(search.equals(""))
        {
            categoryId = Integer.parseInt(getIntent().getStringExtra("current_category_id"));
            loadDataCategory();
        }
        else{
            searchProductFromHome();
        }
    }

    private void loadDataCategory()
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
    public void searchProduct(View view){
        String find = edFind.getText().toString();
        List<Product> lProduct = dbHelper.searchProducts(find);
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
    public void searchProductFromHome(){
        String search = getIntent().getStringExtra("search");
        List<Product> lProduct = dbHelper.searchProducts(search);
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