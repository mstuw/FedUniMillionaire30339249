package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents an answer to a question that can be either correct or incorrect. It implements {@link Parcelable} allowing it to be used as an extra for Intents.
 */
public class Answer implements Parcelable {

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    private final String text;
    private final boolean isCorrect;

    public Answer(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }

    protected Answer(Parcel in) {
        text = in.readString();
        isCorrect = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeByte((byte) (isCorrect ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

}