package hcmute.edu.vn.mobile_store.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Currency;

public class  Utility {
    public static final String CURRENT_ID = "current_id";
    public static final String CURRENT_NAME = "current_name";
    public static final String CURRENT_ROLE = "current_role";

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src){
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }

    public static String FormatPrice(double price)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("VND"));
        String result = format.format(price);
        return result;
    }
}
