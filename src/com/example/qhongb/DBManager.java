package com.example.qhongb;
import java.util.ArrayList;  
import java.util.List;  
  
import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase; 
import android.util.Log;
public class DBManager {
	private DBHelper helper;  
    private SQLiteDatabase db;  
    
    public DBManager(Context context) {  
        helper = new DBHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();  
    }  
    public void add(List<LuckPerson> LuckPersons) {  
        db.beginTransaction();  //开始事务  
        try { 
        	String msg ="";
        	int  ilen =LuckPersons.size();
        	msg = " insert db len =" + String.valueOf(ilen);
        	Log.e("wolf",msg);
            for (LuckPerson person : LuckPersons) {  
                db.execSQL("INSERT INTO luckDetail(title,desc,personName,money,time) VALUES(?, ?, ?, ?,?)", new Object[]{person.title, person.desc, person.personName,person.money,person.time});  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    }  

    public List<LuckPerson> query() {  
        ArrayList<LuckPerson> persons = new ArrayList<LuckPerson>();  
        Cursor c = queryTheCursor();  
        while (c.moveToNext()) {  
        	LuckPerson person = new LuckPerson();  
            person._id = c.getInt(c.getColumnIndex("_id"));  
            person.title = c.getString(c.getColumnIndex("title"));  
            person.personName = c.getString(c.getColumnIndex("personName"));  
            person.desc = c.getString(c.getColumnIndex("desc"));  
            person.time = c.getString(c.getColumnIndex("time"));  
            person.money = c.getString(c.getColumnIndex("money"));  
            persons.add(person);  
        }  
        c.close();  
        return persons;  
    }  
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM luckDetail", null);  
        return c;  
    }    
    public void closeDB() {  
        db.close();  
    }   
    public void onDeleteTable( String tab) {  
    	String strSql = "delete from " +tab;
    	db.execSQL(strSql);

    }  
}
