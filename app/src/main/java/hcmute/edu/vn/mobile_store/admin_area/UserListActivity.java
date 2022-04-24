package hcmute.edu.vn.mobile_store.admin_area;

import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mobile_store.utils.Utility.CURRENT_NAME;
import static hcmute.edu.vn.mobile_store.utils.Utility.convertCompressedByteArrayToBitmap;
import static hcmute.edu.vn.mobile_store.utils.Utility.getBitmapAsByteArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.mobile_store.models.Category;
import hcmute.edu.vn.mobile_store.models.Product;
import hcmute.edu.vn.mobile_store.utils.DatabaseHelper;
import hcmute.edu.vn.mobile_store.R;
import hcmute.edu.vn.mobile_store.adapter.UserListAdapter;
import hcmute.edu.vn.mobile_store.identity_area.LoginActivity;
import hcmute.edu.vn.mobile_store.models.User;
import hcmute.edu.vn.mobile_store.utils.SharedPrefs;

public class UserListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    ImageView mImageView;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSTION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        TextView tvLogOut = findViewById(R.id.tvLogOut);

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.user_manage);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.user_manage:
                        return true;
                    case R.id.bill_manage:
                        startActivity(new Intent(getApplicationContext(), BillListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.product_manage:
                        startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        loadData();

        tvLogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SharedPrefs.getInstance().put(CURRENT_ID, "");
                                SharedPrefs.getInstance().put(CURRENT_NAME, null);
                                startActivity(new Intent(UserListActivity.this, LoginActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "Bạn đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(UserListActivity.this);
                builder.setMessage("Đăng xuất tài khoản này?").setPositiveButton("Đăng Xuất", dialogClickListener)
                        .setNegativeButton("Hủy", dialogClickListener).show();
            }
        });

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showDiaglogAdd(view);
            }
        });


    }

    private void loadData() {
        ListView listView = (ListView) findViewById(R.id.listViewUser);
        androidx.cardview.widget.CardView cvListBill = findViewById(R.id.cvListUser);
        List<User> userList = dbHelper.getUsers();
        if (userList.isEmpty() ||
                SharedPrefs.getInstance().get("current_role", Integer.class) != 0) {
            RelativeLayout mainLayout = findViewById(R.id.mainUserAdmin);

            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            listView.setVisibility(View.GONE);
            cvListBill.setVisibility(View.GONE);

            TextView tvNone = new TextView(UserListActivity.this);
            tvNone.setTextSize(20);
            tvNone.setText("Oops! Lỗi rồi");
            tvNone.setLayoutParams(lparams);
            mainLayout.addView(tvNone);
        } else {
            listView.setAdapter(new UserListAdapter(this, userList));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    long userId = parent.getItemIdAtPosition(position);
                    showDiaglogUpdateDelete(view, (int) userId);
                }
            });
        }
    }

    public void showDiaglogAdd(View view) {
        Context context = view.getRootView().getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.user_input_form, null, false);
        final EditText etName =  (EditText) formElementsView.findViewById(R.id.etNameAdF);
        final EditText etUserName = (EditText) formElementsView.findViewById(R.id.etUsernameAdF);
        final EditText etEmail = (EditText) formElementsView.findViewById(R.id.etEmailAdF);
        final EditText etPassword = (EditText) formElementsView.findViewById(R.id.etPasswordAdF);
        final ImageView ivImageUser = (ImageView) formElementsView.findViewById(R.id.ivImageUserF);
        mImageView =  (ImageView) formElementsView.findViewById(R.id.ivImageUserF);
        final Spinner spinnerRole = (Spinner) formElementsView.findViewById(R.id.spinRoleAdF);
        spinnerRole.setSelection(2);
        loadCategory_Spinner(spinnerRole);

        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSTION_CODE);
                    }
                    else {
                        pickImageFromGarelly();
                    }
                }
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("TẠO NGƯỜI DÙNG")
                .setPositiveButton("Thêm", null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        String username = etUserName.getText().toString();
                        String email = etEmail.getText().toString();
                        String password = etPassword.getText().toString();
                        int role = spinnerRole.getSelectedItemPosition();

                        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                        byte[] byteivImage = getBitmapAsByteArray(bitmap);

                        if (TextUtils.isEmpty(name)) {
                            etName.requestFocus();
                            etName.setError("Bạn phải nhập họ và tên!");
                        } else if (TextUtils.isEmpty(username)) {
                            etUserName.requestFocus();
                            etUserName.setError("Bạn phải nhập tên người dùng!");
                        } else if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            etEmail.requestFocus();
                            etEmail.setError("Email không hợp lệ!");
                        } else if (TextUtils.isEmpty(password)) {
                            etPassword.requestFocus();
                            etPassword.setError("Bạn phải nhập mật khẩu!");
                        }else if (dbHelper.emailExists(email))
                        {
                            etEmail.requestFocus();
                            etEmail.setError("Email đã tồn tạị!");
                        }
                        else if (dbHelper.usernameExists(username))
                        {
                            etUserName.requestFocus();
                            etUserName.setError("Username đã tồn tạị!");
                        }
                        else{

                            User user = new User(name,username,email,password,byteivImage,role);
                            boolean createSuccessful = dbHelper.addUser(user);

                            if(createSuccessful){
                                Toast.makeText(context, "Tạo người dùng thành công!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Có lỗi khi tạo người dùng!", Toast.LENGTH_SHORT).show();
                            }
                            loadData();
                            dialog.cancel();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void showDiaglogUpdateDelete(View view, int id) {
        Context context = view.getContext();
        final CharSequence[] items = { "Cập nhật", "Xóa" };
        new AlertDialog.Builder(context).setTitle("Thao tác")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            editRecord(id, view);
                        }
                        else if (item == 1) {
                            boolean deleteSuccessful = dbHelper.deleteUser(id);
                            if (deleteSuccessful){
                                Toast.makeText(context, "Đã xóa người dùng!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Bạn không thể xóa người dùng này!", Toast.LENGTH_SHORT).show();
                            }
                            loadData();
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    public void editRecord(int id, View view) {
        Context context = view.getRootView().getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View formElementsView = inflater.inflate(R.layout.user_input_form, null, false);
        final EditText etName =  (EditText) formElementsView.findViewById(R.id.etNameAdF);
        final EditText etUserName = (EditText) formElementsView.findViewById(R.id.etUsernameAdF);
        final EditText etEmail = (EditText) formElementsView.findViewById(R.id.etEmailAdF);
        final EditText etPassword = (EditText) formElementsView.findViewById(R.id.etPasswordAdF);
        final TextView tvPassAdF = (TextView) formElementsView.findViewById(R.id.tvPassAdF);
        final ImageView ivImageUser = (ImageView) formElementsView.findViewById(R.id.ivImageUserF);
        mImageView =  (ImageView) formElementsView.findViewById(R.id.ivImageUserF);
        final Spinner spinnerRole = (Spinner) formElementsView.findViewById(R.id.spinRoleAdF);

        User user = dbHelper.getUser(id);
        etName.setText(user.getName());
        etUserName.setText(user.getUsername());
        etEmail.setText(user.getEmail());
        etPassword.setVisibility(View.GONE);
        tvPassAdF.setVisibility(View.GONE);

        mImageView.setImageBitmap(convertCompressedByteArrayToBitmap(user.getImage()));

        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSTION_CODE);
                    }
                    else {
                        pickImageFromGarelly();
                    }
                }
            }
        });

        loadCategory_Spinner(spinnerRole);

        User userUpdated = dbHelper.getUser(id);
        spinnerRole.setSelection(userUpdated.getRole());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("SỬA NGƯỜI DÙNG")
                .setPositiveButton("Lưu", null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        String username = etUserName.getText().toString();
                        String email = etEmail.getText().toString();
                        int role = spinnerRole.getSelectedItemPosition();

                        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

                        if (TextUtils.isEmpty(name)) {
                            etName.requestFocus();
                            etName.setError("Bạn phải nhập họ và tên!");
                        } else if (TextUtils.isEmpty(username)) {
                            etUserName.requestFocus();
                            etUserName.setError("Bạn phải nhập tên người dùng!");
                        } else if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            etEmail.requestFocus();
                            etEmail.setError("Email không hợp lệ!");
                        }
                        else{
                            userUpdated.setName(name);
                            userUpdated.setEmail(email);
                            userUpdated.setUsername(username);
                            userUpdated.setRole(role);
                            userUpdated.setImage(getBitmapAsByteArray(bitmap));

                            boolean updateSuccessful = dbHelper.updateUser(userUpdated);

                            if(updateSuccessful){
                                Toast.makeText(context, "Người dùng đã được cập nhật.", Toast.LENGTH_SHORT).show();
                                loadData();
                            }else{
                                Toast.makeText(context, "Không thể cập nhật người dùng.", Toast.LENGTH_SHORT).show();
                            }
                            dialog.cancel();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void loadCategory_Spinner(Spinner spinnerRole){
        String[] arraySpinner = new String[] {
                "Quản lý", "Nhân viên", "Khách hàng"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void pickImageFromGarelly() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSTION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGarelly();
                }
                else{
                    Toast.makeText(this, "Permission denide...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data!=null) {
            Uri ImageUrl = data.getData();
            CropImage.activity(ImageUrl)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}