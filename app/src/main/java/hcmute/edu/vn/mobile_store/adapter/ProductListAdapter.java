package hcmute.edu.vn.mobile_store.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.Product;

import static hcmute.edu.vn.mobile_store.utils.Utility.convertCompressedByteArrayToBitmap;

public class ProductListAdapter  extends BaseAdapter{
    private List<Product> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public ProductListAdapter(Context aContext,  List<Product> listData) {
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
            convertView = layoutInflater.inflate(R.layout.product_item_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.ivImage);
            holder.name = (TextView) convertView.findViewById(R.id.tvName);
            holder.price = (TextView) convertView.findViewById(R.id.tvPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = this.listData.get(position);

        holder.image.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));
        holder.name.setText(product.getName());
        holder.price.setText("Giá: " + product.getPrice().toString() +"VNĐ");

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView  name;
        TextView price;
    }

}