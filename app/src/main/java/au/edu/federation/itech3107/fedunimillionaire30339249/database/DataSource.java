package au.edu.federation.itech3107.fedunimillionaire30339249.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Closeable;
import java.util.List;

/**
 * An abstract base class for {@link SQLiteDatabase} and {@link SQLiteOpenHelper} that provides simple CRUD methods.
 *
 * @param <T> the type for CRUD methods.
 */
public abstract class DataSource<T> implements Closeable {
    private static final String TAG = "DataSource";

    protected final SQLiteDatabase database;
    protected final SQLiteOpenHelper helper;

    /**
     * Create a new data source object and opens the {@link #database} using the provided {@link SQLiteOpenHelper}.
     */
    public DataSource(SQLiteOpenHelper helper) throws SQLException {
        this.helper = helper;

        try {
            database = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(TAG, "Couldn't open database for data source instance!", e);
            throw e;
        }

    }

    public abstract boolean insert(T t);

    public abstract boolean update(T t);

    public abstract List<T> getAll();

    public abstract boolean delete(T t);

    @Override
    public void close() {
        database.close();
        helper.close();
    }

}
