package hcmute.edu.vn.mobile_store.customer_area;

import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.identity_area.LoginActivity;
import hcmute.edu.vn.mobile_store.models.User;
import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.utils.SharedPrefs;

public class ChangePasswordActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;
    EditText edOldPass, edNewPass,edConfirmPass;
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        btnSave = (Button)findViewById(R.id.cirConfirmButton);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }
    public void changePassword(){
        User user = (User) getIntent().getSerializableExtra("user");
        edOldPass=(EditText)findViewById(R.id.editOldPass);
        String oldPassTyping = edOldPass.getText().toString();
        edNewPass =(EditText)findViewById(R.id.editNewPass);
        String newPass = edNewPass.getText().toString();
        edConfirmPass=(EditText)findViewById(R.id.editConfirmPass);
        String confirmPass = edConfirmPass.getText().toString();
        String oldPassDB = user.getPassword();

        if(md5(oldPassTyping).equals(oldPassDB)){
            if (!isValidPassword(newPass))
            {
                Toast.makeText(this,"Mật khẩu phải tối thiểu 8 ký tự, bao gồm chữ cái, số và ký tự đặc biệt!",Toast.LENGTH_SHORT).show();
            }
            else{
                if(newPass.equals(confirmPass))
                {
                    user.setPassword(newPass);
                    dbHelper.updateUserPass(user);
                    //Logout
                    SharedPrefs.getInstance().put(CURRENT_ID, "");
                    SharedPrefs.getInstance().put(CURRENT_NAME, null);
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    Toast.makeText(ChangePasswordActivity.this,"Đổi mật khẩu thành công",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ChangePasswordActivity.this,"Xác nhận mật khẩu mới sai, hãy nhập lại",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(ChangePasswordActivity.this,"Sai mật khẩu cũ, hãy nhập lại",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isValidPassword(String password) { //Tối thiểu 8 ký tự, 1 chữ cái, 1 số và 1 ký tự đặc biệt

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
    public String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}