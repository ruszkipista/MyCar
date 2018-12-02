package io.ruszkipista.mycar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityCarDetail extends AppCompatActivity {
    private TextView mCarNameTextView;
    private TextView mPlateNumberTextView;
    private TextView mCarImageUrlTextView;
    private CollectionReference carCollRef;
    private String mCarId;
    private DocumentSnapshot mCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cardetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCarNameTextView = findViewById(R.id.cardetail_carname_field);
        mPlateNumberTextView = findViewById(R.id.cardetail_platenumber_field);
        mCarImageUrlTextView = findViewById(R.id.cardetail_carimageurl_field);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCarInput dialog = new DialogCarInput();
                dialog.show(getSupportFragmentManager(),getString(R.string.cardetail_name));
                // null
            }
        });

        carCollRef = FirebaseFirestore.getInstance().collection(Constants.firebase_collection_car);
        mCarId = getIntent().getStringExtra(Constants.EXTRA_DOC_ID);
        carCollRef.document(mCarId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mCar = document;
                        displayCar();
                    } else {
                        Log.d(Constants.log_tag, "No such document");
                    }
                } else {
                    Log.d(Constants.log_tag, "get failed with ", task.getException());
                }
            }
        });
    }

    private void displayCar() {
        if (mCar != null) {
            mCarNameTextView.setText((String) mCar.get(Constants.KEY_CARNAME));
            mPlateNumberTextView.setText((String) mCar.get(Constants.KEY_PLATENUMBER));
            mCarImageUrlTextView.setText((String) mCar.get(Constants.KEY_CARIMAGEURL));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_cardetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_modify_car:
                DialogCarInput dialog = new DialogCarInput();
                dialog.show(getSupportFragmentManager(),getString(R.string.cardetail_name));
                // mCarId
                return true;

            case R.id.action_delete_car:
                carCollRef.document(mCarId).delete();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.log_tag, "ActivityCarDetail RESUMED ");
        displayCar();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Constants.log_tag, "ActivityCarDetail STARTED ");
        displayCar();
    }
}
