package edu.niit.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    List<String> contacts=new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //按钮的事件监听
        Button btnRead =findViewById(R.id.button_read_contact);
        btnRead.setOnClickListener(this);

        //ListView设置适配器
        listView=findViewById(R.id.list_view);
        contacts=new ArrayList<>();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,contacts);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        //判断是否有运行时限
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},1);
            return;
        }
        //读取联系人
        readContacts();
    }

    private void readContacts() {
        Cursor cursor=null;
        try {
            //查询联系人数据
            contacts.clear(); //contacts =new ArrayList<>()使得
            cursor =getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI,null,null,null,null);
            if (cursor !=null){
                while (cursor.moveToNext()){
                    //获取联系人姓名
                    String displayName =cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //获取联系人手机号
                    String number=cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contacts.add(displayName + "\n" +number);
                }
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
    }

    //运行时权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions,@NonNull int[] grantResults){
        if (requestCode ==1 && grantResults.length >0 &&grantResults[0] ==PackageManager.PERMISSION_GRANTED){
            readContacts();
        }else {
            Toast.makeText(MainActivity.this,"申请权限被拒绝",Toast.LENGTH_SHORT).show();
        }
    }
}
