package au.edu.federation.itech3107.fedunimillionaire30339249;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Answer;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.Difficulty;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.Question;

public class QuestionEditorActivity extends AppCompatActivity implements TextWatcher {

    public static final String EXTRA_QUESTION = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTION";

    private List<String> incorrectAnswers;
    private ArrayAdapter<String> incorrectAnswersAdapter;

    private EditText etQuestion;
    private Spinner spinnerDifficulty;
    private EditText etCorrectAnswer;
    private Button btnConfirmQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_editor);

        etQuestion = findViewById(R.id.etQuestion);
        etQuestion.addTextChangedListener(this);

        etCorrectAnswer = findViewById(R.id.etCorrectAnswer);
        etCorrectAnswer.addTextChangedListener(this);

        btnConfirmQuestion = findViewById(R.id.btnConfirmQuestion);

        // Use the values from the Difficulty enum within the spinner.
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerDifficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Difficulty.values()));

        incorrectAnswers = new ArrayList<>();
        incorrectAnswersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, incorrectAnswers);

        ListView lvIncorrectAnswers = findViewById(R.id.lvIncorrectAnswers);
        lvIncorrectAnswers.setAdapter(incorrectAnswersAdapter);

        registerForContextMenu(lvIncorrectAnswers);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_QUESTION)) {
            // Question question = intent.getParcelableExtra(EXTRA_QUESTION);
            // TODO: Set view values for editing questions.
        }
    }

    // Update the enabled status of certain view objects depending on the state of other view objects.
    private void updateViewEnabled() {
        boolean isValidQuestion = etQuestion.getText().length() > 0 && etCorrectAnswer.getText().length() > 0 && incorrectAnswers.size() > 0;
        btnConfirmQuestion.setEnabled(isValidQuestion);
    }

    private void addIncorrectAnswer(String answer) {
        if (!incorrectAnswers.contains(answer)) {
            incorrectAnswers.add(answer);
            incorrectAnswersAdapter.notifyDataSetChanged();
            updateViewEnabled();
        }
    }

    private void deleteAnswer(int index) {
        incorrectAnswers.remove(index);
        incorrectAnswersAdapter.notifyDataSetChanged();
        updateViewEnabled();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.item_actions_menu, menu); // Long click context menu for list view.
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteAnswer(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Show a popup dialog with a text input for adding an incorrect answer.
    public void btnAddIncorrectAnswerClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setTitle(R.string.dialog_add_incorrect_answer_title).setMessage(R.string.dialog_add_incorrect_answer_msg).setView(editText);

        builder.setPositiveButton(R.string.btn_okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addIncorrectAnswer(editText.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);

        builder.show();
    }

    // Set the activity return result, if RESULT_OK add a question extra (EXTRA_QUESTION) representing the question that we are editing/creating.
    private void setResultData(int result) {
        Intent resultIntent = new Intent();

        if (result == RESULT_OK) {
            String questionText = etQuestion.getText().toString();
            String correctAnswer = etCorrectAnswer.getText().toString();
            Difficulty difficulty = (Difficulty) spinnerDifficulty.getSelectedItem();

            // Build answer list.
            List<Answer> answers = new ArrayList<>();

            answers.add(new Answer(correctAnswer, true));
            for (String incorrectAnswer : incorrectAnswers)
                answers.add(new Answer(incorrectAnswer, false));


            // Create question and add it to the result intent.
            Question question = new Question("General Knowledge", difficulty, questionText, answers);
            resultIntent.putExtra(EXTRA_QUESTION, question);
        }

        setResult(result, resultIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Make sure the back button in the action bar cancels the edit operation.
                setResultData(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResultData(RESULT_CANCELED); // Make sure the back button cancels the edit operation.
        super.onBackPressed();
    }

    public void btnConfirmQuestionClicked(View view) {
        setResultData(RESULT_OK);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        updateViewEnabled();
    }

}