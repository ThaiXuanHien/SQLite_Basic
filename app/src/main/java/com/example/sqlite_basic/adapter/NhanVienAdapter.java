package com.example.sqlite_basic.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sqlite_basic.MainActivity;
import com.example.sqlite_basic.R;
import com.example.sqlite_basic.database.DatabaseHelper;
import com.example.sqlite_basic.model.NhanVien;

import java.util.ArrayList;

public class NhanVienAdapter extends BaseAdapter {
    MainActivity context;
    ArrayList<NhanVien> nhanVienArrayList;

    public NhanVienAdapter(MainActivity context, ArrayList<NhanVien> nhanVienArrayList) {
        this.context = context;
        this.nhanVienArrayList = nhanVienArrayList;
    }

    @Override
    public int getCount() {
        return nhanVienArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return nhanVienArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder {
        ImageView imgAvatar, imgXoa, imgSua;
        TextView txtID, txtTen, txtSDT;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_nhanvien, null);
            viewHolder.imgAvatar = view.findViewById(R.id.imageview);
            viewHolder.txtID = view.findViewById(R.id.textviewID);
            viewHolder.txtTen = view.findViewById(R.id.textviewTen);
            viewHolder.txtSDT = view.findViewById(R.id.textviewSDT);
            viewHolder.imgSua = view.findViewById(R.id.imageviewSua);
            viewHolder.imgXoa = view.findViewById(R.id.imageviewXoa);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final NhanVien nhanVien = (NhanVien) getItem(i);
        viewHolder.txtID.setText(nhanVien.getId() + "");
        viewHolder.txtTen.setText(nhanVien.getTen());
        viewHolder.txtSDT.setText(nhanVien.getSdt());
        Bitmap bmAvatar = BitmapFactory.decodeByteArray(nhanVien.getHinhAnh(), 0, nhanVien.getHinhAnh().length);
        viewHolder.imgAvatar.setImageBitmap(bmAvatar);

        viewHolder.imgSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.dialogUpdateNhanVien(nhanVien.getTen(), nhanVien.getSdt(), nhanVien.getHinhAnh(), nhanVien.getId());
            }
        });

        viewHolder.imgXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa Nhân Viên");
                builder.setMessage("Bạn có muốn Xóa " + nhanVien.getTen() + " không ?");
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(nhanVien.getId());
                    }
                });

                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;
    }

    private void delete(int idNhanVien) {
        SQLiteDatabase sqLiteDatabase = DatabaseHelper.initDatabase(context, "QLNV.sqlite");
        sqLiteDatabase.delete("NhanVien", "id = ?", new String[]{idNhanVien + ""});
        Cursor cursor = sqLiteDatabase.rawQuery("select *from NhanVien", null);
        nhanVienArrayList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String sdt = cursor.getString(2);
            byte[] hinAnh = cursor.getBlob(3);
            nhanVienArrayList.add(new NhanVien(id, ten, sdt, hinAnh));
        }
        notifyDataSetChanged();
    }
}
