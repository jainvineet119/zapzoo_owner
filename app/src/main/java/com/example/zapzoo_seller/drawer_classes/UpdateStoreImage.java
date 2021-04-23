package com.example.zapzoo_seller.drawer_classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapzoo_seller.R;

public class UpdateStoreImage extends AppCompatActivity {

    ImageView updateImage;
    Button changeImagebtn;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_storename);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // Action Bar title
        getSupportActionBar().setTitle("Update Store Image");

        // Back button
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        changeImagebtn=findViewById(R.id.update_store_image);
        updateImage=findViewById(R.id.choose_image_update);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // code to pick image
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,101);
            }
        });

        changeImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateStoreImage.this, "Store Image Updated Successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==101 && resultCode == RESULT_OK){
            if(data!=null){
                imageUri=data.getData();
                Glide.with(getApplicationContext()).load(imageUri).into(updateImage);
            }
        }
    }
}