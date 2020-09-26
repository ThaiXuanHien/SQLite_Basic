package com.example.sqlite_basic;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlite_basic.adapter.NhanVienAdapter;
import com.example.sqlite_basic.database.DatabaseHelper;
import com.example.sqlite_basic.model.NhanVien;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String DATABASE_NAME = "QLNV.sqlite";
    SQLiteDatabase sqLiteDatabase;
    ListView lvNhanVien;
    NhanVienAdapter nhanVienAdapter;
    ArrayList<NhanVien> nhanVienArrayList;

    final int REQUEST_TAKE_PHOTO = 123;
    final int REQUEST_CHOOSE_PHOTO = 321;

    Dialog dialog;

    Button btnCamera, btnFolder, btnLuu, btnHuy;
    TextInputLayout edtTen, edtSDT;
    ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();
        readData();

    }

    private void anhXa() {
        lvNhanVien = findViewById(R.id.listviewNhanVien);
        nhanVienArrayList = new ArrayList<>();
        nhanVienAdapter = new NhanVienAdapter(this, nhanVienArrayList);
        lvNhanVien.setAdapter(nhanVienAdapter);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_save_nhanvien);

        btnCamera = dialog.findViewById(R.id.buttonCamera);
        btnFolder = dialog.findViewById(R.id.buttonFolder);
        btnLuu = dialog.findViewById(R.id.buttonLuu);
        btnHuy = dialog.findViewById(R.id.buttonHuy);
        edtTen = dialog.findViewById(R.id.edittextTen);
        edtSDT = dialog.findViewById(R.id.edittextSDT);
        imgAvatar = dialog.findViewById(R.id.imageviewAvatar);
    }

    private void readData() {
        sqLiteDatabase = DatabaseHelper.initDatabase(this, DATABASE_NAME);
        Cursor cursor = sqLiteDatabase.rawQuery("select *from NhanVien", null);
        nhanVienArrayList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String sdt = cursor.getString(2);
            byte[] hinAnh = cursor.getBlob(3);
            nhanVienArrayList.add(new NhanVien(id, ten, sdt, hinAnh));
        }
        nhanVienAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_nhanvien) {

            DialogAddNhanVien();
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogAddNhanVien() {

        edtTen.requestFocus();
        
        edtTen.getEditText().setText("");
        edtSDT.getEditText().setText("");
        imgAvatar.setImageResource(android.R.drawable.ic_menu_gallery);

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();

            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        btnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    private void insert() {
        String tenMoi = edtTen.getEditText().getText().toString().trim();
        String sdtMoi = edtSDT.getEditText().getText().toString().trim();
        byte[] hinhAnhMoi = ImageView_To_Byte(imgAvatar);

        ContentValues contentValues = new ContentValues();
        contentValues.put("ten", tenMoi);
        contentValues.put("sdt", sdtMoi);
        contentValues.put("hinhAnh", hinhAnhMoi);
        sqLiteDatabase.insert("NhanVien", null, contentValues);
        readData();
        dialog.dismiss();
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_PHOTO) {

                try {
                    Uri uriImage = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uriImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgAvatar.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgAvatar.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void dialogUpdateNhanVien(String ten, String sdt, byte[] hinhAnh, final int id) {


        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
        imgAvatar.setImageBitmap(bitmap);

        edtTen.getEditText().setText(ten);
        edtSDT.getEditText().setText(sdt);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(id);
                

            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        btnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void update(int id) {
        String tenMoi = edtTen.getEditText().getText().toString().trim();
        String sdtMoi = edtSDT.getEditText().getText().toString().trim();
        byte[] hinhAnhMoi = ImageView_To_Byte(imgAvatar);

        ContentValues contentValues = new ContentValues();
        contentValues.put("ten", tenMoi);
        contentValues.put("sdt", sdtMoi);
        contentValues.put("hinhAnh", hinhAnhMoi);
        sqLiteDatabase.update("NhanVien", contentValues, "id = ?", new String[]{id + ""});
        readData();
        dialog.dismiss();
    }

    public byte[] ImageView_To_Byte(ImageView imgv) {

        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        Bitmap bmp = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}