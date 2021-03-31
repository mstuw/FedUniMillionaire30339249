package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a valued multiple-choice question. It implements {@link Parcelable} allowing it to be used as an extra for Intents.
 */
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
    private final double value;

    public GameQuestion(String category, Difficulty difficulty, String questionText, List<Answer> answers, double value, boolean isSafeMoney) {
        super(category, difficulty, questionText, answers);
        this.value = value;
        this.isSafeMoney = isSafeMoney;
    }

    public GameQuestion(Question question, double value, boolean isSafeMoney) {
        this(question.getCategory(), question.getDifficulty(), question.getQuestionText(), question.getAnswers(), value, isSafeMoney);
    }

    protected GameQuestion(Parcel in) {
        super("", Difficulty.EASY, "", null);

        category = in.readString();
        difficulty = Difficulty.values()[in.readInt()];
        questionText = in.readString();

        answers = new ArrayList<>();
        in.readTypedList(answers, Answer.CREATOR);

        isSafeMoney = in.readByte() != 0;
        value = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeInt(difficulty.ordinal());
        dest.writeString(questionText);
        dest.writeTypedList(answers);
        dest.writeByte((byte) (isSafeMoney ? 1 : 0));
        dest.writeDouble(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Returns true if this question is considered "safe money".
     * @return true if this question is considered "safe money".
     */
    public boolean isSafeMoney() {
        return isSafeMoney;
    }

    /**
     * The money value of this question.
     *
     * @return money value of this question.
     */
    public double getValue() {
        return value;
    }

}
