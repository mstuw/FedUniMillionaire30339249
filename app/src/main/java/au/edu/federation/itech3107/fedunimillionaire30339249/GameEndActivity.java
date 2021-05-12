package au.edu.federation.itech3107.fedunimillionaire30339249;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import au.edu.federation.itech3107.fedunimillionaire30339249.database.HighscoresDataSource;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class GameEndActivity extends AppCompatActivity implements TextWatcher {

    public static final String EXTRA_WINNINGS_AMOUNT = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_WINNINGS_AMOUNT";
    public static final String EXTRA_TOTAL_CORRECT = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_TOTAL_CORRECT";
    public static final String EXTRA_TOTAL_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_TOTAL_QUESTIONS";

    private static final int PERMISSION_REQUEST_LOCATION = 1;

    private EditText txtPlayerName;
    private Button btnReturn;

    private double winningsAmount;

    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get extras.
        Intent intent = getIntent();
        winningsAmount = intent.getDoubleExtra(EXTRA_WINNINGS_AMOUNT, 0);
        int totalCorrect = intent.getIntExtra(EXTRA_TOTAL_CORRECT, 0);
        int totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0);

        // Find views.
        btnReturn = findViewById(R.id.btnReturn);
        TextView txtStats = findViewById(R.id.txtStats);
        TextView txtBanner = findViewById(R.id.txtBanner);
        TextView txtWinnings = findViewById(R.id.txtWinnings);
        ImageView imageView = findViewById(R.id.imageView);

        // Detect when user changes text box, so we can update the "return button" text.
        txtPlayerName = findViewById(R.id.etPlayerName);

        if (totalCorrect > 0) {
            txtPlayerName.addTextChangedListener(this);
        } else {
            // If the user didn't get any questions correct, hide the highscores text box.
            txtPlayerName.setVisibility(View.INVISIBLE);

            TextView txtHighscoresLabel = findViewById(R.id.txtHighscoresLabel);
            txtHighscoresLabel.setVisibility(View.INVISIBLE);
        }

        // Update views.
        txtStats.setText(getString(R.string.game_end_stats_format, totalCorrect, totalQuestions));
        txtBanner.setText(totalCorrect == 0 ? R.string.game_end_lose : R.string.game_end_win);

        int winningsFormatId = totalCorrect == 0 ? R.string.game_end_lose_format : R.string.game_end_format;
        txtWinnings.setText(getString(winningsFormatId, winningsAmount));

        // Won the jackpot. Answered all questions correctly.
        if (totalCorrect == totalQuestions) {
            imageView.setImageResource(R.drawable.ic_trophy); // Use gold trophy image.
            imageView.setContentDescription(getString(R.string.img_desc_trophy));

        } else {
            imageView.setImageResource(R.drawable.ic_bronze_medal); // Use bronze medal image.
            imageView.setContentDescription(getString(R.string.img_desc_bronze_medal));

        }

    }

    public void btnReturnClicked(View view) {

        // Check/request location permissions
        boolean useLocation = checkPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, R.string.perm_rationale_location, PERMISSION_REQUEST_LOCATION);

        returnToMenuAndSaveHighscore(useLocation);

    }

    @SuppressLint("MissingPermission")
    private void returnToMenuAndSaveHighscore(boolean useLocation) {
        final Intent intent = new Intent(GameEndActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        if (useLocation) { // If we can use location info, request last location.
            if (isHighscoreNameValid()) { // Only request location if we can save the highscore.
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    saveHighscore(location); // Location may be null

                    startActivity(intent);
                });
            } else {
                startActivity(intent);
            }
        } else {
            if (isHighscoreNameValid())
                saveHighscore(null);
            startActivity(intent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                returnToMenuAndSaveHighscore(true); // Permission granted, use location.
            } else {
                Toast.makeText(this, getString(R.string.toast_highscore_permission_denied), Toast.LENGTH_LONG).show();
                returnToMenuAndSaveHighscore(false); // No permission, ignore location.
            }
        }
    }

    /**
     * Check and request the given permissions. Returns true if all permissions specified are granted, false otherwise, permissions will be requested, and rationale will be displayed if needed.
     */
    private boolean checkPermissions(String[] permissions, int permissionRationale, int requestCode) {
        boolean allGranted = true;

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {

            boolean shouldShowPermissionRationale = false;
            for (String permission : permissions) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    shouldShowPermissionRationale = true;
                    break;
                }
            }

            if (shouldShowPermissionRationale) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setMessage(permissionRationale);
                alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    ActivityCompat.requestPermissions(GameEndActivity.this, permissions, requestCode);
                });
            } else {
                ActivityCompat.requestPermissions(this, permissions, requestCode);
            }

            return false;
        }

        return true;
    }

    private boolean isHighscoreNameValid() {
        String playerName = txtPlayerName.getText().toString().trim();
        return !playerName.isEmpty();
    }

    // Save the highscore to the database, if the player name is specified.
    private void saveHighscore(Location location) {
        String playerName = txtPlayerName.getText().toString().trim();

        try (HighscoresDataSource ds = new HighscoresDataSource(this)) {

            Highscore highscore = new Highscore();
            highscore.playerName = playerName;
            highscore.completedOn = Calendar.getInstance().getTime();
            highscore.moneyWon = winningsAmount;
            if (location != null) {
                highscore.hasLocation = true;
                highscore.lat = location.getLatitude();
                highscore.lng = location.getLongitude();
            }

            if (ds.insert(highscore))
                Toast.makeText(this, R.string.toast_highscore_saved, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // Change button text depending on if the text box is empty.
        btnReturn.setText(!editable.toString().trim().isEmpty() ? R.string.btn_return_and_save : R.string.btn_return);
    }


}