package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
    private static final String TAG = "Util";

    /**
     * Returns all text from the specified internal storage file.
     *
     * @param filepath the internal storage filepath.
     * @return all text from the specified internal storage file.
     * @throws IOException an IO exception occurred.
     */
    public static String readAllText(Context context, String filepath) throws IOException {
        return readAllText(context.openFileInput(filepath));
    }

    /**
     * Writes all the text specified into the specified internal storage file.
     *
     * @param filepath the filepath to save the text.
     * @param text     the string that will be written.
     * @throws IOException an IO exception occurred.
     */
    public static void writeAllText(Context context, String filepath, String text) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(filepath, Context.MODE_PRIVATE)) {
            fos.write(text.getBytes());
        }
    }

    /**
     * Reads all text from the specified {@link InputStream} and returns a String.
     *
     * @param inputStream the {@link InputStream} to read.
     * @return a string containing all text from the specified {@link InputStream}.
     * @throws IOException an IO exception occurred.
     */
    public static String readAllText(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * Copies the specified src file from assets and writes it into the specified internal storage location.
     */
    public static void copyAssetToStorage(Context context, String src, String dst) {
        try (InputStream is = context.getAssets().open(src)) {
            try (FileOutputStream fos = context.openFileOutput(dst, Context.MODE_PRIVATE)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1)
                    fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset to storage!", e);
        }
    }


    /**
     * Returns true if file exists within internal storage.
     *
     * @return true if file exists within internal storage.
     */
    public static boolean doesFileExist(Context context, String name) {
        for (String file : context.fileList()) {
            if (file.equals(name))
                return true;
        }
        return false;
    }

}
