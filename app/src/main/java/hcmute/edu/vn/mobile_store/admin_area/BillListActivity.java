package hcmute.edu.vn.mobile_store.admin_area;

import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import hcmute.edu.vn.mobile_store.customer_area.BillDetailCustomerActivity;
import hcmute.edu.vn.mobile_store.customer_area.BillListCustomerActivity;
import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.adapter.BillListCustomerAdapter;
import hcmute.edu.vn.mobile_store.identity_area.LoginActivity;
import hcmute.edu.vn.mobile_store.models.Bill;
import hcmute.edu.vn.mobile_store.utils.SharedPrefs;

public class BillListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        TextView tvLogOut = findViewById(R.id.tvLogOut);

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bill_manage);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.bill_manage:
                        return true;
                    case R.id.product_manage:
                        startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.user_manage:
                        startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        loadData();

        tvLogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SharedPrefs.getInstance().put(CURRENT_ID, "");
                                SharedPrefs.getInstance().put(CURRENT_NAME, null);
                                startActivity(new Intent(BillListActivity.this, LoginActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "Bạn đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(BillListActivity.this);
                builder.setMessage("Đăng xuất tài khoản này?").setPositiveButton("Đăng Xuất", dialogClickListener)
                        .setNegativeButton("Hủy", dialogClickListener).show();
            }
        });
    }

    private void loadData()
    {
        ListView listView = (ListView) findViewById(R.id.listViewBill);
        androidx.cardview.widget.CardView cvListBill = findViewById(R.id.cvListBill);
        List<Bill> billList = dbHelper.getBills();
        if (billList.isEmpty()) {
            RelativeLayout mainLayout = findViewById(R.id.mainBillAdmin);

            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            listView.setVisibility(View.GONE);
            cvListBill.setVisibility(View.GONE);

            TextView tvNone = new TextView(this);
            tvNone.setTextSize(20);
            tvNone.setText("Hiện tại chưa có đơn hàng!");
            tvNone.setLayoutParams(lparams);
            mainLayout.addView(tvNone);
        } else {
            listView.setAdapter(new BillListCustomerAdapter(this, billList));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent intentBill = new Intent(BillListActivity.this , BillDetailCustomerActivity.class);
                    intentBill.putExtra("current_bill_id", String.valueOf(parent.getItemIdAtPosition(position)));
                    startActivity(intentBill);
                }
            });
        }
    }
}