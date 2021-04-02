package au.edu.federation.itech3107.fedunimillionaire30339249.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class HighscoresOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "HighscoresOpenHelper";

    public static final String DATABASE_NAME = "fedunimillionaire30339249-highscores";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "highscores";

    public HighscoresOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    playerName  TEXT    UNIQUE" +
                "                        NOT NULL," +
                "    moneyWon    DECIMAL NOT NULL" +
                "                        DEFAULT (0)," +
                "    completedOn INTEGER NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");

        onCreate(db);

        Log.d(TAG, "Upgraded database. All data lost!");
    }

}
