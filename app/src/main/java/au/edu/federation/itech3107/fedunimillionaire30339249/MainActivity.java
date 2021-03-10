package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final ArrayList<Question> questions = new ArrayList<Question>() {{
        add(new Question(1000, true, "In the UK, the abbreviation NHS stands for National what Service?", 1, "Humanity", "Health", "Honour", "Household"));
        add(new Question(2000, false, "Which Disney character famously leaves a glass slipper behind at a royal ball?", 2, "Pocahontas", "Sleeping Beauty", "Cinderella", "Elsa"));
        add(new Question(4000, false, "What name is given to the revolving belt machinery in an airport that delivers checked luggage from the plane to baggage reclaim?", 3, "Hangar", "Terminal", "Concourse", "Carousel"));
        add(new Question(8000, false, "Which of these brands was chiefly associated with the manufacture of household locks?", 2, "Phillips", "Flymo", "Chubb", "Ronseal"));
        add(new Question(16000, false, "The hammer and sickle is one of the most recognisable symbols of which political ideology?", 1, "Republicanism", "Communism", "Conservatism", "Liberalism"));
        add(new Question(32000, true, "Which toys have been marketed with the phrase “robots in disguise”?", 3, "Bratz Dolls", "Sylvanian Families", "Hatchimals", "Transformers"));
        add(new Question(64000, false, "What does the word loquacious mean?", 1, "Angry", "Chatty", "Beautiful", "Shy"));
        add(new Question(125000, false, "Obstetrics is a branch of medicine particularly concerned with what?", 0, "Childbirth", "Broken Bones", "Heart Conditions", "Old Age"));
        add(new Question(250000, false, "In Doctor Who, what was the signature look of the fourth Doctor, as portrayed by Tom Baker?", 1, "Bow-tie, braces, and tweed jacket", "Wide-brimmed hat and extra long scarf", "Pinstripe suit and trainers", "Cape, velvet jacket and frilly shirt"));
        add(new Question(500000, false, "Construction of which of these famous landmarks was completed first?", 3, "Empire State Building", "Royal Albert Hall", "Eiffel Tower", "Big Ben Clock Tower"));
        add(new Question(1000000, true, "In 1718, which pirate died in battle off the coast of what is now North Carolina?", 1, "Calico Jack", "Blackbeard", "Bartholomew Roberts", "Captain Kid"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    /**
     * Called when the "Start Game" button is clicked. Starts the {@link QuestionActivity}.
     */
    public void btnStartGameClicked(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putParcelableArrayListExtra(QuestionActivity.EXTRA_QUESTIONS, questions);
        intent.putExtra(QuestionActivity.EXTRA_CURRENT_QUESTION, 0);

        startActivity(intent);
    }

}