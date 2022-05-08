package com.example.mobilepackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

public class ProductsActivity extends AppCompatActivity {
    public static final String TAG = "LOG_TAG";
    Button ebuy1, ebuy2, ebuy3;
    ListView simpleList;
    private AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        ebuy1 = findViewById(R.id.buy1);
        ebuy2 = findViewById(R.id.buy2);
        ebuy3 = findViewById(R.id.buy3);

        ebuy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductsActivity.this, "Sikeres csomagválasztás", Toast.LENGTH_SHORT).show();
            }
        });
        ebuy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductsActivity.this, "Sikeres csomagválasztás", Toast.LENGTH_SHORT).show();
            }
        });

        ebuy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductsActivity.this, "Sikeres csomagválasztás", Toast.LENGTH_SHORT).show();
            }
        });
        setAlarmManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.profil:
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                return true;
            case R.id.search_bar:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAlarmManager() {
        long repeatInterval = 60000; // AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReciver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);


        mAlarmManager.cancel(pendingIntent);
    }


}
