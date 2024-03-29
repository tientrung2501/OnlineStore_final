package hcmute.edu.vn.mobile_store.customer_area;

import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mobile_store.utils.Utility.FormatPrice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    //broadcast
    private final String SEND_ORDER_ACTION = "hcmute.edu.vn.mobile_store.ACTION";
    private final String SEND_ORDER_KEY = "hcmute.edu.vn.mobile_store.KEY";

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
        Button btnEditOrderInfo = findViewById(R.id.btnEditOrderInfo);
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

        if (dbHelper.getUser(curUserId).getRole() != 2){ //User không phải là khách hàng
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
        if (dbHelper.getUser(curUserId).getRole() == 2 ||
                !curBill.getStatus().equals("processing")) {
            btnEditOrderInfo.setVisibility(View.INVISIBLE);
        }
    }

    public String convertStatusOrder (String status) {
        switch (status) {
            case "processing":
                return "(Đang chờ xác nhận)";
            case "accepted":
                return "(Đang đợi người giao hàng)";
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

    public void editOrderInfo (View view) {
        Context context = view.getRootView().getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View formElementsView = inflater.inflate(R.layout.bill_input_form, null, false);

        EditText etAddress =  (EditText) formElementsView.findViewById(R.id.etAddress);
        EditText etPhone =  (EditText) formElementsView.findViewById(R.id.etPhone);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("Chỉnh sửa đơn hàng")
                .setPositiveButton("Hoàn tất", null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                etAddress.setText(curBill.getAddress());
                etPhone.setText(curBill.getPhone());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String address = etAddress.getText().toString();
                        String phone = etPhone.getText().toString();
                        if(TextUtils.isEmpty(address))
                        {
                            etAddress.requestFocus();
                            etAddress.setError("Bạn phải nhập địa chỉ!");
                        }
                        else if(TextUtils.isEmpty(phone))
                        {
                            etPhone.requestFocus();
                            etPhone.setError("Bạn phải nhập số điện thoại!");
                        }
                        else{
                            curBill.setAddress(address);
                            curBill.setPhone(phone);

                            dbHelper.updateBill(curBill);
                            Toast.makeText(getApplicationContext(), "Bạn đã chỉnh sửa thành công!", Toast.LENGTH_SHORT).show();
                            loadData();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void confirmOrder (View view) {
        if (dbHelper.getUser(curUserId).getRole() != 2){ //User không phải là khách hàng
            curBill.setStatus("delivery");

            dbHelper.updateBill(curBill);
            Toast.makeText(getApplicationContext(), "Xác nhận đơn hàng thành công!", Toast.LENGTH_SHORT).show();

            // send broadcast to shipper
            List<Bill> billList  = dbHelper.getDeliveryBills();
            Intent sendBroadCastIntent = new Intent(SEND_ORDER_ACTION);
            Gson gson = new Gson();
            JsonArray jsonArray = gson.toJsonTree(billList).getAsJsonArray();
            String strJson = jsonArray.toString();

            sendBroadCastIntent.putExtra(SEND_ORDER_KEY, strJson);
            sendBroadcast(sendBroadCastIntent);
            System.out.println("Send broadcast success");
        }
        else {
            curBill.setStatus("complete");

            dbHelper.updateBill(curBill);
            Toast.makeText(getApplicationContext(), "Xác nhận đã nhận hàng thành công!", Toast.LENGTH_SHORT).show();
        }
        loadData();
    }
}