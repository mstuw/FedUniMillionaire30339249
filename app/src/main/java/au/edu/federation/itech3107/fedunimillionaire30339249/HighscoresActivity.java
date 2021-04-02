package au.edu.federation.itech3107.fedunimillionaire30339249;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.database.HighscoresDataSource;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class HighscoresActivity extends AppCompatActivity {

    private static final String TAG = "HighscoresActivity";

    private final List<Highscore> highscores = new ArrayList<>();
    private HighscoresListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        adapter = new HighscoresListAdapter(this, R.layout.highscores_item, highscores);

        /*
        Highscore hs = new Highscore();
        hs.playerName = "John Doe";
        hs.completedOn = Calendar.getInstance().getTime();
        hs.moneyWon = 6000;
        highscores.add(hs);
        adapter.notifyDataSetChanged();
        */


        ListView lvHighscores = findViewById(R.id.lvHighscores);
        lvHighscores.setAdapter(adapter);

        refreshHighscores();
    }

    private void refreshHighscores() {
        try (HighscoresDataSource ds = new HighscoresDataSource(this)) {

            adapter.clear();
            adapter.addAll(ds.getAll());

        } catch (Exception e) {
            Log.e(TAG, "Failed refreshing highscores!", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_order_by:
                //Toast.makeText(this, "Order by", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_sort_by:
                //Toast.makeText(this, "Sort by", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return true;
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