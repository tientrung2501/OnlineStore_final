package hcmute.edu.vn.mobile_store.customer_area;

import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mobile_store.utils.Utility.FormatPrice;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.adapter.BillDetailAdapter;
import hcmute.edu.vn.mobile_store.adapter.CartListAdapter;
import hcmute.edu.vn.mobile_store.models.Bill;
import hcmute.edu.vn.mobile_store.models.BillDetail;
import hcmute.edu.vn.mobile_store.models.Product;
import hcmute.edu.vn.mobile_store.models.User;
import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.utils.SharedPrefs;

public class BillDetailCustomerActivity extends AppCompatActivity {
    DatabaseHelper dbHelper = null;
    int curUserId, billId;
    Bill curBill;
    User user;
    SimpleDateFormat formatterDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail_customer);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        formatterDate = new SimpleDateFormat("dd-MM-yyyy");

        //Lấy userID hiện tại
        curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));

        // Lấy hóa đơn đã chọn ở trang trước
        billId = Integer.parseInt(getIntent().getStringExtra("current_bill_id"));
        curBill = dbHelper.getBill(billId);
        user = dbHelper.getUser(curBill.getUserId());

        // Lấy dữ liệu thông tin chi tiết đơn hàng
        loadData();

    }

    private void loadData() {
        ListView listView = (ListView) findViewById(R.id.lvBillsDetail);
        Button btnCancelOrder = findViewById(R.id.cirCancelOrderButton);
        Button btnConfirmOrder = findViewById(R.id.cirConfirmOrderButton);
        TextView orderId = (TextView) findViewById(R.id.tvOrderId);
        TextView orderStatus = (TextView) findViewById(R.id.tvStatusOrder);
        TextView orderDate = (TextView) findViewById(R.id.tvStartDateOrder);
        TextView userName = (TextView) findViewById(R.id.tvUserNameOrder);
        TextView userPhone = (TextView) findViewById(R.id.tvUserPhoneOrder);
        TextView userAddress = (TextView) findViewById(R.id.tvUserAddressOrder);
        TextView totalPrice = findViewById(R.id.tvTotalOrder);

        // Set giá trị
        orderId.setText(String.valueOf(curBill.getId()));
        orderStatus.setText(convertStatusOrder(curBill.getStatus()));
        orderDate.setText(curBill.getDate());
        userName.setText("'Tên': " + user.getName());
        userPhone.setText("'Số điện thoại': " + curBill.getPhone());
        userAddress.setText("'Địa chỉ': " + curBill.getAddress());
        totalPrice.setText(FormatPrice(curBill.getTotalPrice()));

        List<BillDetail> lBillDetail = dbHelper.getBillDetailByBillId(billId);

        List<Product> lProduct =  new ArrayList<Product>();;

        for ( BillDetail object : lBillDetail) {
            lProduct.add(dbHelper.getProduct(object.getProductId()));
        }

        listView.setAdapter(new BillDetailAdapter(this, lBillDetail,lProduct));

        if (user.getRole() != 2){ //User không phải là khách hàng
            btnConfirmOrder.setText("Xác nhận đơn hàng");
            // Chỉ hiện nút xác nhận khi đơn hàng chưa được xác nhận
            if (!curBill.getStatus().equals("processing")) {
                btnConfirmOrder.setVisibility(View.GONE);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            }
        }
        else
        {
            btnConfirmOrder.setText("Đã nhận hàng");
            // Chỉ hiện nút xác nhận khi đơn hàng được vận chuyển
            if (!curBill.getStatus().equals("delivery")) {
                btnConfirmOrder.setVisibility(View.GONE);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            }

        }
        System.out.println(curBill.getStatus());
        if(!curBill.getStatus().equals("processing")) {
            btnCancelOrder.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
    }

    public String convertStatusOrder (String status) {
        switch (status) {
            case "processing":
                return "(Đang chờ xác nhận)";
            case "delivery":
                return "(Đang giao hàng)";
            case "complete":
                return "(Đã nhận hàng)";
            case "cancel":
                return "(Đã hủy)";
            default:
                return status;
        }
    }

    public void cancelOrder (View view) {
        curBill.setStatus("cancel");

        dbHelper.updateBill(curBill);
        Toast.makeText(getApplicationContext(), "Hủy đơn hàng thành công!", Toast.LENGTH_SHORT).show();
        loadData();
    }

    public void confirmOrder (View view) {
        if (user.getRole() != 2){ //User không phải là khách hàng
            curBill.setStatus("delivery");

            dbHelper.updateBill(curBill);
            Toast.makeText(getApplicationContext(), "Xác nhận đơn hàng thành công!", Toast.LENGTH_SHORT).show();
        }
        else {
            curBill.setStatus("complete");

            dbHelper.updateBill(curBill);
            Toast.makeText(getApplicationContext(), "Xác nhận đã nhận hàng thành công!", Toast.LENGTH_SHORT).show();
        }
        loadData();
    }
}