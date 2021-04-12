package au.edu.federation.itech3107.fedunimillionaire30339249.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.R;
import au.edu.federation.itech3107.fedunimillionaire30339249.database.data.Highscore;

public class HighscoresListAdapter extends ArrayAdapter<Highscore> {

    private final int resource;
    private final SimpleDateFormat dateFormat;

    public HighscoresListAdapter(Context context, int resource, List<Highscore> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // TODO: Use current locale
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(resource, null);

        Highscore highscore = getItem(index);
        if (highscore != null) {
            TextView txtName = view.findViewById(R.id.txtName);
            TextView txtMoney = view.findViewById(R.id.txtMoney);
            TextView txtDate = view.findViewById(R.id.txtDate);

            txtName.setText(highscore.playerName);
            txtMoney.setText(String.format("$%.2f", highscore.moneyWon)); // TODO: Use current locale
            txtDate.setText(dateFormat.format(highscore.completedOn.getTime()));
        }

        return view;
    }

}
