package hcmute.edu.vn.mobile_store.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.Bill;
import hcmute.edu.vn.mobile_store.models.BillDetail;
import hcmute.edu.vn.mobile_store.models.Product;

import static hcmute.edu.vn.mobile_store.utils.Utility.FormatPrice;
import static hcmute.edu.vn.mobile_store.utils.Utility.convertCompressedByteArrayToBitmap;

public class BillListCustomerAdapter  extends BaseAdapter {
    private List<Bill> listData;
    private LayoutInflater layoutInflater;
    private Context context;


    public BillListCustomerAdapter(Context aContext,  List<Bill> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listData.get(position).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.bill_customer_item_layout, null);
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.tvId);
            holder.price = (TextView) convertView.findViewById(R.id.tvPrice);
            holder.date = (TextView) convertView.findViewById(R.id.tvDate);
            holder.status = (TextView) convertView.findViewById(R.id.tvStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bill bill = this.listData.get(position);

        holder.id.setText("Mã đơn hàng: "+ String.valueOf(bill.getId()));
        holder.price.setText("Giá:" + FormatPrice(bill.getTotalPrice()));
        holder.date.setText("Ngày đặt hàng: " + bill.getDate());
        if (bill.getStatus().equals("processing"))
        {
            holder.status.setText("Đang chờ xác nhận");
            holder.status.setTextColor(Color.rgb(255,158,0));
        }
        else if (bill.getStatus().equals("delivery"))
        {
            holder.status.setText("Đang giao hàng");
            holder.status.setTextColor(Color.rgb(255,158,0));
        }
        else if (bill.getStatus().equals("accepted"))
        {
            holder.status.setText("Đang đợi người giao hàng xác nhận");
            holder.status.setTextColor(Color.rgb(255,158,0));
        }
        else if (bill.getStatus().equals("complete"))
        {
            holder.status.setText("Đã nhận hàng");
            holder.status.setTextColor(Color.rgb(0,255,0));
        }
        else if (bill.getStatus().equals("cancel"))
        {
            holder.status.setText("Đã hủy");
            holder.status.setTextColor(Color.rgb(255,0,0));
        } else {
            holder.status.setText(bill.getStatus());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView id;
        TextView price;
        TextView date;
        TextView status;
    }

}
