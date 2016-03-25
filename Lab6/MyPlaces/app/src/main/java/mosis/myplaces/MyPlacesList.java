package mosis.myplaces;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyPlacesList extends AppCompatActivity {

    private android.os.Handler guiThread;
    Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);

        guiThread = new android.os.Handler();

        context = this;
        progressDialog = new ProgressDialog(this);

        ListView myPlacesList = (ListView) findViewById(R.id.my_places_list);
        // !!!! Baca Exception na sledecu liniju !!!!
        myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getIntance().getMyPlaces()));
        myPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*MyPlace place = (MyPlace) parent.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), place.getName() + " selected", Toast.LENGTH_SHORT).show();*/
                Bundle positionBundle = new Bundle();
                positionBundle.putInt("position", position);
                Intent i = new Intent(MyPlacesList.this, ViewMyPlaceActivity.class);
                i.putExtras(positionBundle);
                startActivity(i);
            }
        });

        myPlacesList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                MyPlace place = MyPlacesData.getIntance().getPlace(info.position);
                menu.setHeaderTitle(place.getName());
                menu.add(0, 1, 1, "View place");
                menu.add(0, 2, 2, "Edit place");
                menu.add(0, 3, 3, "Delete place");
                menu.add(0, 4, 4, "Show on map");
                menu.add(0, 5, 5, "Upload place");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_places_list, menu);
        return true;
    }

    static int NEW_PLACE = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_map_item) {
            //Toast.makeText(this, "Show Map!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MyPlacesMapActivity.class);
            i.putExtra("state", MyPlacesMapActivity.SHOW_MAP);
            startActivity(i);
        } else if (id == R.id.new_place_item) {
            // Toast.makeText(this, "New Place!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, EditMyPlaceActivity.class);
            startActivityForResult(i, NEW_PLACE);
        } else if (id == R.id.server_list_item){
            Dialog v = new MyPlacesSeverList(this);
            v.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setList();
                }
            });
            v.show();
        } else if (id == R.id.about_item) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Bundle positionBundle = new Bundle();
        positionBundle.putInt("position", info.position);
        Intent i = null;
        if (item.getItemId() == 1) {
            i = new Intent(this, ViewMyPlaceActivity.class);
            i.putExtras(positionBundle);
            startActivity(i);
        } else if (item.getItemId() == 2) {
            i = new Intent(this, EditMyPlaceActivity.class);
            i.putExtras(positionBundle);
            startActivity(i);
        } else if (item.getItemId() == 3) {
            MyPlacesData.getIntance().deletePlace(info.position);
            setList();
        } else if (item.getItemId() == 4) {
            i = new Intent(this, MyPlacesMapActivity.class);
            i.putExtra("state", MyPlacesMapActivity.CENTER_PLACE_ON_MAP);
            MyPlace place = MyPlacesData.getIntance().getPlace(info.position);
            i.putExtra("lat", place.getLatitude());
            i.putExtra("lon", place.getLongitude());
            startActivityForResult(i, 2);
        } else if (item.getItemId() == 5) {
            ExecutorService transThread = Executors.newSingleThreadExecutor();
            final int position = info.position;
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        MyPlace place = MyPlacesData.getIntance().getPlace(position);
                        guiStartProgressDialog("Sending place", "Sending " + place.getName());
                        final String message = MyPlacesHTTPHelper.sendMyPlace(place);
                        guiNotifyUser(message);
                    } catch (Exception ec) {
                        ec.printStackTrace();
                    }
                    guiDismissProgressDialog();
                }
            });
        }
        return super.onContextItemSelected(item);
    }

    private void guiNotifyUser(final String message) {
        guiThread.post(new Runnable(){
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guiStartProgressDialog(final String title, final String message) {
        guiThread.post(new Runnable() {
            public void run() {
                progressDialog.setTitle(title);
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void guiDismissProgressDialog() {
        guiThread.post(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    private void setList(){
        ListView myPlacesList = (ListView)findViewById(R.id.my_places_list);
        myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getIntance().getMyPlaces()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this, "New place added", Toast.LENGTH_SHORT).show();
            // ???? Ne znam da li ovo ovde treba
            ListView myPlacesList = (ListView)findViewById(R.id.my_places_list);
            myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getIntance().getMyPlaces()));
        }
        else if (resultCode == -2)
        {
            Toast.makeText(MyPlacesApplication.getContext(), "New place not added!", Toast.LENGTH_SHORT).show();
        }
    }
}
