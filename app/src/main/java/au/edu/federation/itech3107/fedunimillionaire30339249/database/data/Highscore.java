package au.edu.federation.itech3107.fedunimillionaire30339249.database.data;

import java.util.Date;

// No point in having getters/setters as this is a simple DTO.
public class Highscore {

    public long id;

    public String playerName;

    public double moneyWon;

    public Date completedOn;

    public boolean hasLocation;

    public double lat;

    public double lng;

}
