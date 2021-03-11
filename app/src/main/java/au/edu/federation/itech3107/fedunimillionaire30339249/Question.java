package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.os.Parcel;
import android.os.Parcelable;

/** This class represents a valued multiple-choice question. It implements {@link Parcelable} allowing it to be used as an extra for Intents. */
public class Question implements Parcelable {

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    private final double value;
    private final boolean isSafeMoney;

    private final String question;

    private final int correctChoice;
    private final String[] choices;

    /**
     * Create a new Question object.
     * @param value The value of this question in dollars.
     * @param isSafeMoney True, if the user will receive the value of this question if they answer correctly.
     * @param question The actual question in text form.
     * @param correctChoice The zero-based index for the correct answer.
     * @param choices An array of answers for this question.
     */
    public Question(double value, boolean isSafeMoney, String question, int correctChoice, String... choices) {
        this.value = value;
        this.isSafeMoney = isSafeMoney;
        this.question = question;
        this.correctChoice = correctChoice;
        this.choices = choices;
    }

    protected Question(Parcel in) {
        value = in.readDouble();
        isSafeMoney = in.readByte() != 0;
        question = in.readString();
        correctChoice = in.readInt();
        choices = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(value);
        parcel.writeByte((byte) (isSafeMoney ? 1 : 0));
        parcel.writeString(question);
        parcel.writeInt(correctChoice);
        parcel.writeStringArray(choices);
    }

    /** @return The value of this question in dollars. */
    public double getValue() {
        return value;
    }

    /** @return True, if the user will receive the value of this question if they answer correctly. */
    public boolean isSafeMoney() {
        return isSafeMoney;
    }

    /** @return The actual question in text form.*/
    public String getQuestion() {
        return question;
    }

    /** @return The zero-based index for the correct answer to this question. See {@link Question#getChoices()}. */
    public int getCorrectChoice() {
        return correctChoice;
    }

    /** @return An array of answers for this question. It is expected that at least one is correct. */
    public String[] getChoices() {
        return choices;
    }


}
