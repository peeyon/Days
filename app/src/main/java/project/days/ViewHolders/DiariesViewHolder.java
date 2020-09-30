package project.days.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import project.days.R;

public class DiariesViewHolder extends RecyclerView.ViewHolder {

    public TextView txt;

    public DiariesViewHolder(@NonNull View itemView) {
        super(itemView);
        txt = itemView.findViewById(R.id.diary_view_name);

    }
}
