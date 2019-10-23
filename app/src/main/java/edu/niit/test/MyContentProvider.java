package edu.niit.test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    private static  final  String  CONTENT="content://";
    private static  final String AUTHORITY="edu.niit.android.content.provider";
    private  static  final  String URI=CONTENT+AUTHORITY+"/"+DBHelper.TBL_NAME_STUDENT;

    public static  final  int STUDENTS_DIR=1;//表示访问student表得所有数据
    public  static  final  int STUDENTS_ITEM=2;//表示访问student表得单条数据

    private static UriMatcher matcher;

    //#：表示匹配任意长度的数字
    //#：表示匹配任意长度的任意字符
    static {
        matcher =new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY,DBHelper.TBL_NAME_STUDENT,STUDENTS_DIR);
        matcher.addURI(AUTHORITY,DBHelper.TBL_NAME_STUDENT+"/#",STUDENTS_ITEM);
    }

    private DBHelper helper;
    @Override
    public boolean onCreate(){
        helper=new DBHelper(getContext());
        return true;
    }
    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db =helper.getWritableDatabase();
        int deleteRows=0;
        switch (matcher.match(uri)){
            case STUDENTS_DIR:
                deleteRows=db.delete(DBHelper.TBL_NAME_STUDENT,selection,selectionArgs);
                break;
            case STUDENTS_ITEM:
                String id=uri.getPathSegments().get(1);
                deleteRows=db.delete(DBHelper.TBL_NAME_STUDENT,"_id=?",new String[]{id});
                break;
            default:
                break;
        }
        return deleteRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)){
            case STUDENTS_DIR:
                return "vnd.android.cursor.dir/vnd."+AUTHORITY;     //数据集合
            case STUDENTS_ITEM:
                return "vnd.android.cursor.item/vnd."+AUTHORITY;    //单项数据
            default:
                break;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db=helper.getWritableDatabase();
        Uri newUri =null;
        switch (matcher.match(uri)){
            case STUDENTS_DIR:
            case STUDENTS_ITEM:
                long newId =db.insert(DBHelper.TBL_NAME_STUDENT,null,values);
                newUri =Uri.parse(URI+"/"+newId);
                break;
            default:
                break;
        }
        return newUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor=null;
        SQLiteDatabase db=helper.getReadableDatabase();
        switch (matcher.match(uri)){
            case STUDENTS_DIR:
                cursor=db.query(DBHelper.TBL_NAME_STUDENT,projection,
                                selection,selectionArgs,
                                null,null,sortOrder);
                break;
            case STUDENTS_ITEM:
                String id =uri.getPathSegments().get(1);
                cursor=db.query(DBHelper.TBL_NAME_STUDENT,projection,
                                "_id=?",new String[]{id},
                                 null,null,sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db=helper.getWritableDatabase();
        int updateRows=0;
        switch (matcher.match(uri)){
            case STUDENTS_DIR:
                updateRows=db.update(DBHelper.TBL_NAME_STUDENT,values,selection,selectionArgs);
                break;
            case STUDENTS_ITEM:
                String id=uri.getPathSegments().get(1);
                updateRows=db.update(DBHelper.TBL_NAME_STUDENT,values,"_id=?" , new String[]{id});
                break;
            default:
                break;
        }
        return updateRows;
    }
}
