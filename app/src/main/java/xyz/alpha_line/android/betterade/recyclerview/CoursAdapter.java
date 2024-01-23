package xyz.alpha_line.android.betterade.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import xyz.alpha_line.android.betterade.R;

public class CoursAdapter extends RecyclerView.Adapter<CoursViewHolder> {

    private List<CoursInfos> cours;

    public CoursAdapter(List<CoursInfos> cours) {
        this.cours = cours;
    }

    @Override
    public CoursViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = LayoutInflater.from(
                viewGroup.getContext())
                .inflate(R.layout.item_recycler_view, viewGroup, false);
        return new CoursViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CoursViewHolder myViewHolder, int position) {
        CoursInfos myObject = cours.get(position);
        myViewHolder.bind(myObject);
    }

    @Override
    public int getItemCount() {
        return cours.size();
    }
}
