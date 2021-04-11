package au.edu.federation.itech3107.fedunimillionaire30339249;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Question;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class QuestionLibraryActivity extends AppCompatActivity {
    private static final String TAG= "QuestionLibraryActivity";

    public static final String EXTRA_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS";
    private ArrayList<Question> questions;

    private ListView lvQuestions;
    private QuestionsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_library);

        questions = getIntent().getParcelableArrayListExtra(EXTRA_QUESTIONS);

        adapter = new QuestionsListAdapter(this, R.layout.question_item, questions);
        lvQuestions = findViewById(R.id.lvQuestions);
        lvQuestions.setAdapter(adapter);

        registerForContextMenu(lvQuestions);

    }

    private void addQuestion(){
        Log.d(TAG, "Adding question...");

    }

    private void deleteQuestion(int index){
        Log.d(TAG, "Deleting question item #" + index);

        Question question = adapter.getItem(index);

        // TODO: Delete question from manager.

        adapter.remove(question);
        adapter.notifyDataSetInvalidated();
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
            case R.id.action_add:
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

    public void btnReturnClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}