package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.os.Parcel;
import android.os.Parcelable;

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

    public double getValue() {
        return value;
    }

    public boolean isSafeMoney() {
        return isSafeMoney;
    }

    public String getQuestion() {
        return question;
    }

    public int getCorrectChoice() {
        return correctChoice;
    }

    public String[] getChoices() {
        return choices;
    }


}
