package ru.kireev.mir.volunteerlizaalert.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kireev.mir.volunteerlizaalert.R;
import ru.kireev.mir.volunteerlizaalert.interfaces.OnDepartureClickListener;
import ru.kireev.mir.volunteerlizaalert.pojo.Departure;

public class DepartureAdapter extends RecyclerView.Adapter<DepartureAdapter.DepartureViewHolder> {

    private List<Departure> departures;
    private OnDepartureClickListener onDepartureClickListener;

    public void setOnDepartureClickListener(OnDepartureClickListener onDepartureClickListener) {
        this.onDepartureClickListener = onDepartureClickListener;
    }

    public DepartureAdapter() { departures = new ArrayList<>();}

    @NonNull
    @Override
    public DepartureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.departure_item, parent, false);
        return new DepartureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartureViewHolder holder, int position) {
        Departure departure = departures.get(position);
        holder.tvDepartureMessageTopic.setText(departure.getMessageTopic());
        holder.tvDepartureTime.setText(departure.getDepartureTime());
        holder.tvDeparturePlace.setText(departure.getDeparturePlace());
    }

    @Override
    public int getItemCount() {
        return departures.size();
    }

    public void addDeparture(Departure departure) {
        departures.add(departure);
        notifyDataSetChanged();
    }

    public void setDepartures(List<Departure> departures){
        this.departures = departures;
        notifyDataSetChanged();
    }

    class DepartureViewHolder extends RecyclerView.ViewHolder{
        private TextView tvDepartureMessageTopic;
        private TextView tvDepartureTime;
        private TextView tvDeparturePlace;
        public DepartureViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartureMessageTopic = itemView.findViewById(R.id.tvDepartureMessageTopic);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvDeparturePlace = itemView.findViewById(R.id.tvDeparturePlace);
            itemView.setOnClickListener(v -> {
                if (onDepartureClickListener != null) {
                    onDepartureClickListener.onDepartureClick(getAdapterPosition());
                }
            });
        }
    }

    public List<Departure> getDepartures() {
        return departures;
    }
}
