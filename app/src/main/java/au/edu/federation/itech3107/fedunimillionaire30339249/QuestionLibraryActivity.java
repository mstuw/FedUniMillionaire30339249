package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.adapter.QuestionsListAdapter;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.Question;

public class QuestionLibraryActivity extends AppCompatActivity {
    private static final String TAG = "QuestionLibraryActivity";

    public static final String EXTRA_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS";
    public static final String EXTRA_DELETED_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_DELETED_QUESTIONS";
    public static final String EXTRA_ADDED_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_ADDED_QUESTIONS";

    public static final int RESULT_REFRESH_QUESTIONS = 3;

    private static final int REQUEST_ADD_QUESTION = 1;
    //private static final int REQUEST_EDIT_QUESTION = 2;

    private final List<Question> deletedQuestions = new ArrayList<>(); // A list of questions to be deleted from the question files, returned as a activity result.
    private final List<Question> addedQuestions = new ArrayList<>(); // A list of questions to be added written into the question files, returned as a activity result.

    private ListView lvQuestions;
    private QuestionsListAdapter questionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_library);

        List<Question> questions = getIntent().getParcelableArrayListExtra(EXTRA_QUESTIONS);
        questionsAdapter = new QuestionsListAdapter(this, R.layout.question_item, questions);

        lvQuestions = findViewById(R.id.lvQuestions);
        lvQuestions.setAdapter(questionsAdapter);

        registerForContextMenu(lvQuestions);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_QUESTION && resultCode == RESULT_OK) {
            Log.d(TAG, "Adding new question....");

            Question question = data.getParcelableExtra(QuestionEditorActivity.EXTRA_QUESTION);

            addedQuestions.add(question);

            questionsAdapter.add(question);
            questionsAdapter.notifyDataSetInvalidated();
        }
    }

    // Show the question editor activity for adding a question.
    private void addQuestion() {
        Intent intent = new Intent(this, QuestionEditorActivity.class);
        //  intent.putExtra(QuestionEditorActivity.EXTRA_QUESTION, question); // When editing a question...

        startActivityForResult(intent, REQUEST_ADD_QUESTION);
    }

    // Delete the a question at the specified index.
    private void deleteQuestion(int index) {
        Log.d(TAG, "Deleting question item #" + index);

        Question question = questionsAdapter.getItem(index);

        deletedQuestions.add(question); // Record the deleted question so we can return it as a result.

        questionsAdapter.remove(question);
        questionsAdapter.notifyDataSetInvalidated();
    }

    // Called when the "three dot" button is clicked for any item in the list view.
    public void showItemPopup(View view) {
        lvQuestions.showContextMenuForChild(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.item_actions_menu, menu); // Long click context menu for list view.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_library_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Make sure the "back arrow" in the action bar sets the correct return results.
                setResult();
                finish();
                return true;
            case R.id.action_add: // the plus symbol in the top-right of the action bar.
                addQuestion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteQuestion(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra(EXTRA_DELETED_QUESTIONS, (ArrayList<Question>) deletedQuestions);
        resultIntent.putParcelableArrayListExtra(EXTRA_ADDED_QUESTIONS, (ArrayList<Question>) addedQuestions);

        setResult(RESULT_REFRESH_QUESTIONS, resultIntent);
    }

    public void btnReturnClicked(View view) {
        setResult();
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult();// Refresh questions when back is pressed.
        super.onBackPressed();
    }

}