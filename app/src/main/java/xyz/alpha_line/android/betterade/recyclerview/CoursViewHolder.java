package xyz.alpha_line.android.betterade.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import xyz.alpha_line.android.betterade.R;

public class CoursViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    public TextView titre;
    public TextView prof;
    public TextView salle;
    public TextView heure;
    public View colorBar;

    public CoursViewHolder(View itemView) {
        super(itemView);
        titre = (TextView) itemView.findViewById(R.id.Title);
        prof = (TextView) itemView.findViewById(R.id.Teacher);
        salle = (TextView) itemView.findViewById(R.id.Rooms);
        heure = (TextView) itemView.findViewById(R.id.Hour);
    }

    public void bind(CoursInfos cours) {
        titre.setText(cours.titre);
        prof.setText(cours.prof);
        salle.setText(cours.salle);
        heure.setText(sdf.format(cours.heureDebut.getTime()) + " - " + sdf.format(cours.heureFin.getTime()));
    }
}
