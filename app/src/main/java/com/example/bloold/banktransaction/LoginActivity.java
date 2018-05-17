package com.example.bloold.banktransaction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import android.database.Cursor;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.bloold.banktransaction.LoginController.userName;
import static com.example.bloold.banktransaction.LoginController.userNum;
import static com.example.bloold.banktransaction.LoginController.userSum;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mCardNum;

    private SqlOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //находим элементы интерфейса
        mCardNum = findViewById(R.id.cardNum);
        mName = findViewById(R.id.name);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        //подключаемся к базе
        dbHelper = new SqlOpenHelper(this);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCardNum.getText().toString().length() != 16) {
                    Toast.makeText(LoginActivity.this, "неправильно введен номер карты", Toast.LENGTH_LONG).show();
                    return;
                }
                //получение курсора в бд
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                Cursor cursor = database.query(SqlOpenHelper.TABLE_CONTACTS, null, null,
                        null, null, null, null);

                //пока курсор не достиг конца бд ищем сходство полей и если нашли, то открываем другой экран
                // и сохраняем наши данные в LoginController (это синглтон для хранения наших данных во время работы программы
                while (cursor.moveToNext()) {
                    if (TextUtils.equals(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NUM)), mCardNum.getText().toString())
                            && TextUtils.equals(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.KEY_NAME)), mName.getText().toString())) {
                        putCredentials(cursor.getInt(cursor.getColumnIndex(SqlOpenHelper.KEY_SUM)));
                        cursor.close();
                    }
                }

                //если не нашли наши данные в бд, то создаем новую запись
                ContentValues contentValues = new ContentValues();

                contentValues.put(SqlOpenHelper.KEY_NAME, mName.getText().toString());
                contentValues.put(SqlOpenHelper.KEY_NUM, mCardNum.getText().toString());
                contentValues.put(SqlOpenHelper.KEY_SUM, 10000);

                database.insert(SqlOpenHelper.TABLE_CONTACTS, null, contentValues);

                putCredentials(10000);
            }
        });
    }

    private void putCredentials(int sum) {
        userName = mName.getText().toString();
        userNum = mCardNum.getText().toString();
        userSum = sum;

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}

