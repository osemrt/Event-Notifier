package ce.yildiz.edu.tr.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_event_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventTitle.setText(event.getEVENT());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventColor;
        private TextView eventTitle;
        private TextView eventTime;
        private TextView eventNote;
        private ImageView options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventColor = (ImageView) itemView.findViewById(R.id.LayoutCell_ImageView_EventColor);
            eventTitle = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTitle);
            eventTime = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTime);
            eventNote = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventNote);
            options = (ImageView) itemView.findViewById(R.id.LayoutCell_ImageView_Options);
        }
    }
}
