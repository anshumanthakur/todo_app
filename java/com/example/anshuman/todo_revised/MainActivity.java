package com.example.anshuman.todo_revised;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHandler dbHandler;
    private ArrayAdapter mAdapter;
    private ListView myListView;
    int count;

    NotificationCompat.Builder notification= new NotificationCompat.Builder(this);
    private static final int uniqueID = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myListView = (ListView)findViewById(R.id.list_todo);


        dbHandler = new DBHandler(this,null,null,1);

        UpdateUI();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                final EditText taskEditText = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                String message = String.valueOf(taskEditText.getText());
                                Toast.makeText(MainActivity.this, "task added: " + message, Toast.LENGTH_SHORT).show();
                                SQLiteDatabase db = dbHandler.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(DBHandler.COLUMN_TASK, message);
                                db.insertWithOnConflict(DBHandler.TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                UpdateUI();

                                NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                nm.notify(uniqueID, notification.build());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();


            }


        });
    }

    public void deleteTask(View view){
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.delete(DBHandler.TABLE_PRODUCTS, DBHandler.COLUMN_TASK + "=?", new String[]{task});
        db.close();
        UpdateUI();
        count=myListView.getAdapter().getCount();
        Toast.makeText(getApplicationContext(), count +" tasks ramaining", Toast.LENGTH_SHORT).show();
    }




    private void UpdateUI(){
        ArrayList<String> tasklist = new ArrayList<>();
        SQLiteDatabase db =  dbHandler.getWritableDatabase();
        Cursor cursor = db.query(DBHandler.TABLE_PRODUCTS,new String[]{DBHandler.COLUMN_ID,DBHandler.COLUMN_TASK},
                null,null,null,null,null);
        while (cursor.moveToNext()){
            int idx = cursor.getColumnIndex(DBHandler.COLUMN_TASK);
            tasklist.add(cursor.getString(idx));
        }
        if (mAdapter == null){
            mAdapter = new ArrayAdapter<>(this,R.layout.item_todo,R.id.task_title,tasklist);
        }

        else {
            mAdapter.clear();
            mAdapter.addAll(tasklist);
            mAdapter.notifyDataSetChanged();
        }
        mAdapter = new ArrayAdapter<>(this,R.layout.item_todo,R.id.task_title,tasklist);
        myListView.setAdapter(mAdapter);
        cursor.close();
        db.close();
        //for notification
        count=myListView.getAdapter().getCount();
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentTitle("todo reminder");
        notification.setContentText(count + " tasks remaining");
        notification.setOngoing(true);

        notification.setPriority(2);
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);


        //builds and issues notification
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }
}
