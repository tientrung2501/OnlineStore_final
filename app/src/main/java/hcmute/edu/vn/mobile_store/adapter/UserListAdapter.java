package hcmute.edu.vn.mobile_store.adapter;

import static hcmute.edu.vn.mobile_store.utils.Utility.convertCompressedByteArrayToBitmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.User;

public class UserListAdapter extends BaseAdapter {
    private List<User> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public UserListAdapter(Context aContext,  List<User> listData) {
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
        UserListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.user_item_layout, null);
            holder = new UserListAdapter.ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.ivUserImageAd);
            holder.name = (TextView) convertView.findViewById(R.id.tvUserNameAd);
            holder.email = (TextView) convertView.findViewById(R.id.tvEmailAd);
            convertView.setTag(holder);
        } else {
            holder = (UserListAdapter.ViewHolder) convertView.getTag();
        }

        User user = this.listData.get(position);

        holder.image.setImageBitmap(convertCompressedByteArrayToBitmap(user.getImage()));
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView  name;
        TextView email;
    }
}
