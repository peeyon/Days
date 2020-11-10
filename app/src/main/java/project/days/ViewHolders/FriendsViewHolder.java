package project.days.ViewHolders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import project.days.R;

public class FriendsViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setName(String name) {
        TextView nameField = (TextView) mView.findViewById(R.id.add_him_name);
        nameField.setText(name);
    }
}
