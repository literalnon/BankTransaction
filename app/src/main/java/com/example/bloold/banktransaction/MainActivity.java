package com.example.bloold.banktransaction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etOtherCard;
    private EditText etSum;

    private Button btnSend;
    private Button btnTable;

    private SqlOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //находим элементы интерфейса
        etOtherCard = findViewById(R.id.etEnterCardNumOther);
        etSum = findViewById(R.id.etEnterSum);

        btnSend = findViewById(R.id.btnSend);
        btnTable = findViewById(R.id.btnTable);
//бд
        dbHelper = new SqlOpenHelper(this);

        if (TextUtils.equals(LoginController.userName, "admin") && TextUtils.equals(LoginController.userNum, "0000000000000000")) {
            //вешаем слушателя на кнопку
            btnTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, TableActivity.class));
                }
            });
        } else {
            btnTable.setVisibility(View.GONE);
        }

        //слушатель на кнопке отправить
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//открытие бд и взятие курсора
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                Cursor cursor = database.query(SqlOpenHelper.TABLE_CONTACTS, null, null,
                        null, null, null, null);

                //поиск совпадения с отправителем и обновление данных если у нас хватает на это средств
                while (cursor.moveToNext()) {
                    if (TextUtils.equals(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NUM)), etOtherCard.getText().toString())) {

                        int curSum = cursor.getInt(cursor.getColumnIndex(SqlOpenHelper.KEY_SUM));
                        int sendSum = Integer.parseInt(etSum.getText().toString());

                        if (LoginController.userSum >= sendSum) {
                            //обновление данных карты получателя
                            ContentValues contentValues = new ContentValues();

                            contentValues.put(SqlOpenHelper.KEY_NAME, cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NAME)));
                            contentValues.put(SqlOpenHelper.KEY_NUM, cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NUM)));
                            contentValues.put(SqlOpenHelper.KEY_SUM, curSum + sendSum);

                            database.update(SqlOpenHelper.TABLE_CONTACTS, contentValues,
                                    SqlOpenHelper.KEY_NUM + "= ?",
                                    new String[]{cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NUM))});

                            LoginController.userSum -= sendSum;

                            updateMySum(database);

                            //вывод окна - уведомления
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Данные обновлены")
                                    .setMessage("У вас на счету: " + LoginController.userSum)
                                    .create()
                                    .show();
                        } else {
                            Toast.makeText(MainActivity.this, "на счету не хватает средств", Toast.LENGTH_LONG).show();
                        }

                        cursor.close();
                        return;
                    }
                }

                cursor.close();

                Toast.makeText(MainActivity.this, "нет такой карты", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateMySum(SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();

        //обновление данных карты получателя
        contentValues.put(SqlOpenHelper.KEY_NAME, LoginController.userName);
        contentValues.put(SqlOpenHelper.KEY_NUM, LoginController.userNum);
        contentValues.put(SqlOpenHelper.KEY_SUM, LoginController.userSum);

        database.update(SqlOpenHelper.TABLE_CONTACTS, contentValues,
                SqlOpenHelper.KEY_NUM + "= ?",
                new String[]{LoginController.userNum});
    }
}
