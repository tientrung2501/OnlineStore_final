package hcmute.edu.vn.mobile_store.adapter;

import static hcmute.edu.vn.mobile_store.utils.Utility.FormatPrice;
import static hcmute.edu.vn.mobile_store.utils.Utility.convertCompressedByteArrayToBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.BillDetail;
import hcmute.edu.vn.mobile_store.models.Product;

public class BillDetailAdapter extends BaseAdapter {
    private List<BillDetail> listData;
    private List<Product> listProductWithData;
    private LayoutInflater layoutInflater;
    private Context context;


    public BillDetailAdapter(Context aContext, List<BillDetail> listData, List<Product> listProductWithData) {
        this.context = aContext;
        this.listData = listData;
        this.listProductWithData = listProductWithData;
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

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        BillDetailAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.cart_item_layout, null);
            holder = new BillDetailAdapter.ViewHolder();
            holder.imageProduct = convertView.findViewById(R.id.ivImage);
            holder.nameProduct = convertView.findViewById(R.id.tvName);
            holder.quantityProduct = convertView.findViewById(R.id.tvQuantity);
            holder.priceProduct = convertView.findViewById(R.id.tvPrice);
            convertView.setTag(holder);
        } else {
            holder = (BillDetailAdapter.ViewHolder) convertView.getTag();
        }

        BillDetail billDetail = this.listData.get(position);
        Product product = this.listProductWithData.get(position);

        holder.imageProduct.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));
        holder.nameProduct.setText(product.getName());
        holder.priceProduct.setText("Giá: " + FormatPrice(billDetail.getPrice()));
        holder.quantityProduct.setText("Số lượng: " + String.valueOf(billDetail.getQuantity()));
        return convertView;
    }

    static class ViewHolder {
        ImageView imageProduct;
        TextView  nameProduct;
        TextView  priceProduct;
        TextView  quantityProduct;
    }
}
