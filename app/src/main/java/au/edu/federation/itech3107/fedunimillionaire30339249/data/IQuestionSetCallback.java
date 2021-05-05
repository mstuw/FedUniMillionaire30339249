package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import java.util.List;

public interface IQuestionSetCallback {
    void onQuestionSetCreated(List<GameQuestion> questions1, List<GameQuestion> questions2);
}