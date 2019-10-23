package edu.niit.providertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TBL_NAME_STUDENT="student";
    private static  final  String  CONTENT="content://";
    private static  final String AUTHORITY="edu.niit.android.content.provider";
    private  static  final  String URI=CONTENT+AUTHORITY+"/"+TBL_NAME_STUDENT;

    public static final String NAME="name";
    public static final String CLASSMATE="classmate";
    public static final String AGE="age";

    private String newId;           //insert的返回值
    private List<String> contacts;  //Student表的属性组成的字符串的集合
    private String selected;         //ListView中选中的选中项的内容
    private int selectedPos;        //ListView中选中的索引号
    private Uri uri;

    //ListView相关的对象
    private ListView listView;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uri=Uri.parse(URI);

        //ListView配置适配器
        contacts=new ArrayList<>();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,contacts);
        listView=findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //ListView选中项的事件监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPos=position;
                selected=(String) parent.getItemAtPosition(position);
            }
        });
    }

    public void onClick(View v) {
        Uri uri =Uri.parse(URI);
        switch (v.getId()){
            case R.id.btn_query:
                //清空adapter的列表内容
                adapter.clear();
                List<String> students =query(uri);
                if (students!=null){
                    //将获取的数据集合加入adapter，并刷新
                    adapter.addAll(students);
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(this,"没有数据",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add:
                Student student=new Student("张三","软件1721",20);
                ContentValues values=new ContentValues();
                values.put(NAME,student.getName());
                values.put(CLASSMATE,student.getClassmate());
                values.put(AGE,student.getAge());

                Uri newUri =getContentResolver().insert(uri,values);
                if (newUri!=null){
                    newId =newUri.getPathSegments().get(1);
                    String str=newId+"\t"+student.getName()+"\t"+student.getClassmate()+"\t"+student.getAge();
                    //更新ListView
                    adapter.add(str);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private List<String> query(Uri uri){
        List<String> result =null;
        Cursor cursor=getContentResolver().query(uri,null,null,null,null);
        if (cursor!=null){
            result =new ArrayList<>();
            while (cursor.moveToNext()){
                int id=cursor.getInt(cursor.getColumnIndex("_id"));
                String name=cursor.getString(cursor.getColumnIndex(NAME));
                String classmate =cursor.getString(cursor.getColumnIndex(CLASSMATE));
                int age =cursor.getInt(cursor.getColumnIndex(AGE));
                result.add(id+"\t"+name+"\t"+classmate+"\t"+age);
            }
            cursor.close();
        }
        return result;
    }
}
