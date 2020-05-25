package ce.yildiz.edu.tr.calendar.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.views.EditEventActivity;
import ce.yildiz.edu.tr.calendar.views.UpcomingEventsFragment;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    private Context context;
    private List<Event> events;
    private DBHelper dbHelper;
    private UpcomingEventsFragment upcomingEventsFragment;

    public UpcomingEventAdapter(Context context, List<Event> events, UpcomingEventsFragment upcomingEventsFragment) {
        this.context = context;
        this.events = events;
        this.upcomingEventsFragment = upcomingEventsFragment;

        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_upcoming_events_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Event event = events.get(position);

        holder.eventColorImageView.setBackgroundColor(event.getColor());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.eventDateTextView.setText(event.getDate());
        holder.eventTimeTextView.setText(event.getTime());
        holder.eventNoteTextView.setText(event.getNote());

        holder.optionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.optionsImageButton, position);
            }
        });

        if (!event.isNotify()) {
            holder.notificationImageButton.setVisibility(View.GONE);
        }
        if (event.isAllDay()) {
            holder.eventTimeLinearLayout.setVisibility(View.GONE);
        }

        if (!event.isRecurring()) {
            holder.recurringEventLinearLayout.setVisibility(View.GONE);
        }

    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventColorImageView;
        private TextView eventTitleTextView;
        private TextView eventDateTextView;
        private TextView eventTimeTextView;
        private TextView eventNoteTextView;
        private ImageButton optionsImageButton;
        private ImageButton notificationImageButton;
        private LinearLayout eventTimeLinearLayout;
        private LinearLayout recurringEventLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventColorImageView = (ImageView) itemView.findViewById(R.id.UpcomingLayoutCell_ImageView_EventColor);
            eventTitleTextView = (TextView) itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventTitle);
            eventDateTextView = (TextView) itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventDate);
            eventTimeTextView = (TextView) itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventTime);
            eventNoteTextView = (TextView) itemView.findViewById(R.id.UpcomingLayoutCell_TextView_EventNote);
            optionsImageButton = (ImageButton) itemView.findViewById(R.id.UpcomingLayoutCell_ImageButton_Options);
            notificationImageButton = (ImageButton) itemView.findViewById(R.id.UpcomingLayoutCell_ImageButton_Notification);
            eventTimeLinearLayout = (LinearLayout) itemView.findViewById(R.id.UpcomingLayoutCell_LinearLayout_Time);
            recurringEventLinearLayout = (LinearLayout) itemView.findViewById(R.id.UpcomingLayoutCell_LinearLayout_Loop);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        private Event mEvent;

        public MyMenuItemClickListener(int position) {
            this.position = position;
            this.mEvent = events.get(position);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = null;
            switch (menuItem.getItemId()) {
                case R.id.Popup_Item_Edit:
                    intent = new Intent(context, EditEventActivity.class);
                    intent.putExtra("eventId", mEvent.getId());
                    intent.putExtra("eventDate", mEvent.getDate());
                    upcomingEventsFragment.startActivityForResult(intent, EDIT_EVENT_ACTIVITY_REQUEST_CODE);
                    return true;
                case R.id.Popup_Item_Delete:
                    new AlertDialog.Builder(context)
                            .setTitle("Deleting a Recurring Event")
                            .setMessage("Are you sure you want to delete this recurring event? All occurrences of this event will also be deleted.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteEvent(mEvent.getId());
                                    events.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, events.size());
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Event removed!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.ic_warning)
                            .show();
                    return true;
                case R.id.Popup_Item_Share:
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, mEvent.toString());
                    intent.setType("text/plain");
                    upcomingEventsFragment.startActivity(Intent.createChooser(intent, null));
                    return true;
                case R.id.Popup_Item_Mail:
                    // String receiver_email = receiver_editText.getText().toString();
                    String subject = mEvent.getTitle();
                    String message = mEvent.toString();

                    // String[] addresses = receiver_email.split(", ");

                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    // intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);

                    upcomingEventsFragment.startActivity(Intent.createChooser(intent, "Send Email"));
                    return true;
            }

            return false;
        }

    }

    private void deleteEvent(int eventId) {
        dbHelper = new DBHelper(context);
        dbHelper.deleteEvent(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteRecurringPattern(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteEventInstanceException(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteNotificationByEventId(dbHelper.getWritableDatabase(), eventId);
        dbHelper.close();
    }

}
