package au.edu.federation.itech3107.fedunimillionaire30339249;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import au.edu.federation.itech3107.fedunimillionaire30339249.database.HighscoresDataSource;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class GameEndActivity extends AppCompatActivity implements TextWatcher {

    public static final String EXTRA_WINNINGS_AMOUNT = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_WINNINGS_AMOUNT";
    public static final String EXTRA_TOTAL_CORRECT = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_TOTAL_CORRECT";
    public static final String EXTRA_TOTAL_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_TOTAL_QUESTIONS";

    private EditText txtPlayerName;
    private Button btnReturn;

    private double winningsAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        TrySaveHighscore();

        startActivity(intent);
    }

    // Save the highscore to the database, if the player name is specified.
    private void TrySaveHighscore() {
        String playerName = txtPlayerName.getText().toString().trim();

        if (playerName.isEmpty())
            return;

        try (HighscoresDataSource ds = new HighscoresDataSource(this)) {

            Highscore highscore = new Highscore();
            highscore.playerName = playerName;
            highscore.completedOn = Calendar.getInstance().getTime();
            highscore.moneyWon = winningsAmount;

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
        btnReturn.setText(!editable.toString().trim().isEmpty() ? R.string.btnReturnAndSave : R.string.btnReturn);
    }

}