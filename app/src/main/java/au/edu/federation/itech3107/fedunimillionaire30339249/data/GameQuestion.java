package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GameQuestion extends Question implements Parcelable {

    public static final Creator<GameQuestion> CREATOR = new Creator<GameQuestion>() {
        @Override
        public GameQuestion createFromParcel(Parcel in) {
            return new GameQuestion(in);
        }

        @Override
        public GameQuestion[] newArray(int size) {
            return new GameQuestion[size];
        }
    };

    private final boolean isSafeMoney;

    public GameQuestion(String category, Difficulty difficulty, String questionText, List<Answer> answers, boolean isSafeMoney) {
        super(category, difficulty, questionText, answers);
        this.isSafeMoney = isSafeMoney;
    }

    public GameQuestion(Question question, boolean isSafeMoney) {
        this(question.getCategory(), question.getDifficulty(), question.getQuestionText(), question.getAnswers(), isSafeMoney);
    }

    protected GameQuestion(Parcel in) {
        super(in.readString(), Difficulty.values()[in.readInt()], in.readString(), null);

        answers = new ArrayList<>();
        in.readTypedList(answers, Answer.CREATOR);

        isSafeMoney = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeInt(difficulty.ordinal());
        dest.writeString(questionText);
        dest.writeTypedList(answers);
        dest.writeByte((byte) (isSafeMoney ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isSafeMoney() {
        return isSafeMoney;
    }

}
