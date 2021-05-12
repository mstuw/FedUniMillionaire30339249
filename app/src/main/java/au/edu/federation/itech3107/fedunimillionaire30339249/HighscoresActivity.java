package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.adapter.HighscoresListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.HighscoresDataSource;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class HighscoresActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final float SHOW_IN_MAP_ZOOM_LEVEL = 18;

    private static final String TAG = "HighscoresActivity";

    private final List<Highscore> highscores = new ArrayList<>();
    private HighscoresListAdapter highscoresAdapter;

    // The current selected item for the "order by" and "sort by" dialogs.
    private int orderBySelection = 0;
    private int sortBySelection = 0;

    private ListView lvHighscores;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        // Load the Google Map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        highscoresAdapter = new HighscoresListAdapter(this, R.layout.highscores_item, highscores);
        lvHighscores = findViewById(R.id.lvHighscores);
        lvHighscores.setAdapter(highscoresAdapter);

        // Register list view for long click context menu.
        registerForContextMenu(lvHighscores);

        refreshHighscores();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setIndoorEnabled(false);

        // Default map location
        LatLng markerPosition = new LatLng(-37.626044, 143.891659);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15));

        // Add map markers for highscores.
        try (HighscoresDataSource ds = new HighscoresDataSource(this)) {
            List<Highscore> list = ds.getAll(null, false);

            for (Highscore highscore : list) {
                if (!highscore.hasLocation)
                    continue;

                LatLng latLng = new LatLng(highscore.lat, highscore.lng);
                map.addMarker(new MarkerOptions().position(latLng).title(highscore.playerName));
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed creating highscore markers!", e);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.item_highscore_actions_menu, menu); // Long click context menu for list view.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.highscores_menu, menu);
        return true;
    }

    /**
     * Refresh all items in list view with items in database. Sorted by column in ascending or descending order.
     */
    private void refreshHighscores() {
        try (HighscoresDataSource ds = new HighscoresDataSource(this)) {

            boolean isAscending = orderBySelection == 0;

            String sortByColumn = "completedOn";
            switch (sortBySelection) {
                case 0:
                    sortByColumn = "playerName";
                    break;
                case 1:
                    sortByColumn = "moneyWon";
                    break;
                default:
                    break;
            }

            highscores.clear();
            highscores.addAll(ds.getAll(sortByColumn, isAscending));
            highscoresAdapter.notifyDataSetInvalidated();

        } catch (Exception e) {
            Log.e(TAG, "Failed refreshing highscores!", e);
        }
    }

    /**
     * Deletes the specified highscore from the database and list view (the specified index is the adapter index).
     */
    private void deleteHighscore(int index) {
        Log.d(TAG, "Deleting highscore item #" + index);

        try (HighscoresDataSource ds = new HighscoresDataSource(this)) {
            Highscore highscore = highscoresAdapter.getItem(index);

            if (ds.delete(highscore)) {
                // Remove item from list view, if item was successfully removed from database.
                highscoresAdapter.remove(highscore);
                highscoresAdapter.notifyDataSetInvalidated();
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed deleting highscore!", e);
        }


    }

    /**
     * Pan the map to the given highscore location.
     */
    private void showInMap(int index) {
        Highscore highscore = highscoresAdapter.getItem(index);
        if (highscore != null && highscore.hasLocation) {
            LatLng position = new LatLng(highscore.lat, highscore.lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, SHOW_IN_MAP_ZOOM_LEVEL));
        }
    }

    // Called when the "three dot" button is clicked for any item in the list view.
    public void showItemPopup(View view) {
        lvHighscores.showContextMenuForChild(view);
    }

    public void btnReturnClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder;
        String[] choices;

        switch (item.getItemId()) {
            case R.id.action_order_by:
                choices = getResources().getStringArray(R.array.arr_order_by);

                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_order_by);
                builder.setSingleChoiceItems(choices, orderBySelection, (dialog, which) -> {
                    orderBySelection = which;
                    dialog.cancel();
                    refreshHighscores();
                });
                builder.show();
                return true;

            case R.id.action_sort_by:
                choices = getResources().getStringArray(R.array.arr_sort_by);

                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_sort_by);
                builder.setSingleChoiceItems(choices, sortBySelection, (dialog, which) -> {
                    sortBySelection = which;
                    dialog.cancel();
                    refreshHighscores();
                });
                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteHighscore(info.position);
                return true;
            case R.id.action_show_in_map:
                showInMap(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}