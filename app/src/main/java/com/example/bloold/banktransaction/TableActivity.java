package com.example.bloold.banktransaction;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

public class TableActivity extends AppCompatActivity {

    private TextView tvTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tvTable = findViewById(R.id.tvTable);

        Cursor cursor = new SqlOpenHelper(this)
                .getWritableDatabase()
                .query(SqlOpenHelper.TABLE_CONTACTS, null, null,
                null, null, null, null);

        //вывод всех записей из бд
        String table = "Данные \n";

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NAME));
            String sum = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_SUM));
            String num = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NUM));

            table += "Имя: " + name + " номер: " + num + " сумма: " + sum + "\n";

        }

        cursor.close();

        tvTable.setText(table);
    }
}
