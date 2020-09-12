package project.days;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DiariesViewHolder extends RecyclerView.ViewHolder {

    public TextView txt;

    public DiariesViewHolder(@NonNull View itemView) {
        super(itemView);
        txt = (TextView) itemView.findViewById(R.id.diary_view_name);

    }
}
