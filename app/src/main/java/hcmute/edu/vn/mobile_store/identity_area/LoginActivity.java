package hcmute.edu.vn.mobile_store.identity_area;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


import hcmute.edu.vn.mobile_store.admin_area.ProductListActivity;
import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.customer_area.HomeActivity;
import hcmute.edu.vn.mobile_store.models.User;
import hcmute.edu.vn.mobile_store.utils.SharedPrefs;

import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_NAME;
import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ROLE;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        String userId = SharedPrefs.getInstance().get(CURRENT_ID, String.class);
        User curUser = new User();
        try {
            curUser = dbHelper.getUser(Integer.parseInt(userId));
        }
        catch (Exception e) {
        }

        if (userId != "" && userId != null)
        {
            if (curUser.getRole() != 2)
            {
                startActivity(new Intent(this, ProductListActivity.class));
                finish();
                Toast.makeText(this, "Bạn đã đăng nhập vào tài khoản thành công!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                startActivity(new Intent(this,HomeActivity.class));
                finish();
                Toast.makeText(this, "Bạn đã đăng nhập vào tài khoản thành công!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
    public void onForgotClick(View View){
        startActivity(new Intent(this,ForgotPasswordActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
    public void doLogin(View view)
    {
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        //Login with email or username
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (dbHelper.loginIsSuccess(email,password))
        {
            User curUser = dbHelper.getUser(email);
            if(curUser == null)
            {
                curUser=dbHelper.getUserByUsername(email);
            }
            SharedPrefs.getInstance().put(CURRENT_ID, String.valueOf(curUser.getId()));
            SharedPrefs.getInstance().put(CURRENT_NAME, curUser.getName());
            SharedPrefs.getInstance().put(CURRENT_ROLE, curUser.getRole());

            if (curUser.getRole() != 2)
            {
                startActivity(new Intent(this, ProductListActivity.class));
                finish();
                Toast.makeText(this, "Bạn đã đăng nhập vào tài khoản thành công!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                startActivity(new Intent(this,HomeActivity.class));
                finish();
                Toast.makeText(this, "Bạn đã đăng nhập vào tài khoản thành công!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.blueMinistop));
        }
    }
}