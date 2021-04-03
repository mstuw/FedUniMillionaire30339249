package au.edu.federation.itech3107.fedunimillionaire30339249.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class HighscoresDataSource extends DataSource<Highscore> {
    private static final String TAG = "HighscoresDataSource";

    public HighscoresDataSource(Context context) throws SQLException {
        super(new HighscoresOpenHelper(context));
    }

    @Override
    public boolean insert(Highscore highscore) {
        ContentValues values = new ContentValues();
        values.put("playerName", highscore.playerName);
        values.put("moneyWon", highscore.moneyWon);
        values.put("completedOn", highscore.completedOn.getTime());

        highscore.id = database.insert(HighscoresOpenHelper.TABLE_NAME, null, values);

        Log.d(TAG, "Inserted highscore into database!");

        return true;
    }

    @Override
    public boolean update(Highscore highscore) {
        throw new UnsupportedOperationException("Updating highscores isn't implemented yet. " + TAG + ".");
    }

    @Override
    public List<Highscore> getAll() {
        Log.d(TAG, "Getting all highscores from database!");

        List<Highscore> highscores = new ArrayList<>();

        Cursor cursor = database.query(HighscoresOpenHelper.TABLE_NAME, new String[]{"id", "playerName", "moneyWon", "completedOn"}, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Highscore highscore = new Highscore();

            highscore.id = cursor.getLong(0);
            highscore.playerName = cursor.getString(1);
            highscore.moneyWon = cursor.getDouble(2);
            highscore.completedOn = new Date(cursor.getLong(3));

            highscores.add(highscore);

            cursor.moveToNext();
        }

        cursor.close();

        return highscores;
    }

    @Override
    public boolean delete(Highscore highscore) {
        Log.d(TAG, "Deleted highscore from database!");

        return database.delete(HighscoresOpenHelper.TABLE_NAME, "id = ?", new String[]{Long.toString(highscore.id)}) > 0;
    }


}
