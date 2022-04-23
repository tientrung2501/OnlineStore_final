package hcmute.edu.vn.mobile_store.identity_area;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hcmute.edu.vn.mobile_store.DatabaseHelper;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.models.User;
//import hcmute.edu.vn.mobile_store.utils.SendMail;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etMail;
    private Button btnForgot;
    DatabaseHelper db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        db = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        etMail = (EditText) findViewById(R.id.editTextEmail);
        btnForgot = (Button) findViewById(R.id.cirForgotButton);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }
    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    private void sendEmail() {
        String email = etMail.getText().toString().trim();
        boolean check = db.emailExists(email);
        if(check){
            String subject = "Reset Password";
            String message = "AbcXyz123@";
            User u = db.getUser(email);
            u.setPassword(message);
            db.updateUser(u);
//            SendMail sm = new SendMail(this, email, subject, message);
//            sm.execute();
        }
        else{
            Toast.makeText(this,"Email không tồn tại",Toast.LENGTH_SHORT).show();
        }
    }
}