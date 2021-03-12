package au.edu.federation.itech3107.fedunimillionaire30339249;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GameEndActivity extends AppCompatActivity {

    public static final String EXTRA_WINNINGS_AMOUNT = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_WINNINGS_AMOUNT";
    public static final String EXTRA_TOTAL_CORRECT = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_TOTAL_CORRECT";
    public static final String EXTRA_TOTAL_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_TOTAL_QUESTIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        // Get extras.
        Intent intent = getIntent();
        double winningsAmount = intent.getDoubleExtra(EXTRA_WINNINGS_AMOUNT, 0);
        int totalCorrect = intent.getIntExtra(EXTRA_TOTAL_CORRECT, 0);
        int totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0);

        // Find views.
        TextView txtStats = findViewById(R.id.txtStats);
        TextView txtBanner = findViewById(R.id.txtBanner);
        TextView txtWinnings = findViewById(R.id.txtWinnings);
        ImageView imageView = findViewById(R.id.imageView);

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

        startActivity(intent);
    }

}