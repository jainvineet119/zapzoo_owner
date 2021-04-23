package com.example.zapzoo_seller.drawer_classes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zapzoo_seller.R;

public class ChangePassword extends AppCompatActivity {

    //Variables
    CheckBox checkBox1,checkBox2,checkBox3;
    EditText old_password,new_password,c_password;
    Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // Action Bar title
        getSupportActionBar().setTitle("Change Password");

        // Back button
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Hooks
        checkBox1=findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox3 = findViewById(R.id.checkbox3);
        old_password=findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        c_password = findViewById(R.id.confirm_password);
        changePassword = findViewById(R.id.change_password);

        // check box
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    old_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    c_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    c_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // Change password
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChangePassword.this, "Password Changed Successfully !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home :
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}