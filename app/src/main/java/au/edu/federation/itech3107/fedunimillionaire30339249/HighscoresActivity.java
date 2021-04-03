package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.database.HighscoresDataSource;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class HighscoresActivity extends AppCompatActivity {

    private static final String TAG = "HighscoresActivity";

    private final List<Highscore> highscores = new ArrayList<>();
    private HighscoresListAdapter adapter;

    // The current selected item for the "order by" and "sort by" dialogs.
    private int orderBySelection = 0;
    private int sortBySelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        adapter = new HighscoresListAdapter(this, R.layout.highscores_item, highscores);

        ListView lvHighscores = findViewById(R.id.lvHighscores);
        lvHighscores.setAdapter(adapter);

        refreshHighscores();
    }

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
            adapter.notifyDataSetInvalidated();

        } catch (Exception e) {
            Log.e(TAG, "Failed refreshing highscores!", e);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.highscores_menu, menu);
        return true;
    }

    public void btnReturnClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}