package hcmute.edu.vn.mobile_store.identity_area;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.User;

public class VerifyRegisterActivity extends AppCompatActivity {
    Button btnConfirm;
    EditText edOTP;
    DatabaseHelper db= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_register);
        db = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        edOTP = (EditText)findViewById(R.id.editTextOTP);
        btnConfirm = (Button) findViewById(R.id.cirConfirmButton);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
    }
    private void verify() {
        User user = (User) getIntent().getSerializableExtra("user");
        String otp = getIntent().getStringExtra("otp");
        if(edOTP.getText().toString().equals(otp)) {
            boolean createSuccessful = db.addUser(user);
            if (createSuccessful) {
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
                Toast.makeText(this,"Bạn đã đăng ký tài khoản thành công",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Có lỗi khi đăng ký!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this,"Đã xảy ra lỗi",Toast.LENGTH_SHORT).show();
        }
    }
}