package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Question;

public class QuestionsListAdapter extends ArrayAdapter<Question> {

    private final int resource;

    public QuestionsListAdapter(Context context, int resource, List<Question> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(resource, null);

        Question question = getItem(index);
        if (question != null) {
            TextView txtQuestionText = view.findViewById(R.id.txtQuestionText);
            TextView txtAnswerInfo = view.findViewById(R.id.txtAnswerInfo);
            TextView txtDifficulty = view.findViewById(R.id.txtDifficulty);

            txtQuestionText.setText(question.getQuestionText());
            txtDifficulty.setText(question.getDifficulty().toString());
            txtAnswerInfo.setText(getContext().getResources().getString(R.string.question_answers, question.getAnswers().size()));
        }

        return view;
    }

}
