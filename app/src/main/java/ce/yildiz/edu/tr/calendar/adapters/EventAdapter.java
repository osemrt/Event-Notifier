package ce.yildiz.edu.tr.calendar.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.views.CalendarFragment;
import ce.yildiz.edu.tr.calendar.views.EditEventActivity;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    private Context context;
    private List<Event> eventList;
    private DBHelper dbHelper;
    private CalendarFragment calendarFragment;
    private AlertDialog alertDialog;

    public EventAdapter(Context context, List<Event> eventList, AlertDialog alertDialog, CalendarFragment calendarFragment) {
        this.context = context;
        this.eventList = eventList;
        this.calendarFragment = calendarFragment;
        this.alertDialog = alertDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_event_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Event event = eventList.get(position);

        holder.eventColorImageView.setBackgroundColor(event.getColor());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.eventTimeTextView.setText(event.getTime());
        holder.eventNoteTextView.setText(event.getNote());

        holder.optionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.optionsImageButton, position);
            }
        });

        if (!isAlarmed(event.getTitle(), event.getDate(), event.getTime())) {
            holder.notificationImageButton.setVisibility(View.GONE);
        }
        if (isAllDay(event.getTitle(), event.getDate(), event.getTime())) {
            holder.eventTimeLinearLayout.setVisibility(View.GONE);
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
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventColorImageView;
        private TextView eventTitleTextView;
        private TextView eventTimeTextView;
        private TextView eventNoteTextView;
        private ImageButton optionsImageButton;
        private ImageButton notificationImageButton;
        private LinearLayout eventTimeLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventColorImageView = (ImageView) itemView.findViewById(R.id.LayoutCell_ImageView_EventColor);
            eventTitleTextView = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTitle);
            eventTimeTextView = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTime);
            eventNoteTextView = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventNote);
            optionsImageButton = (ImageButton) itemView.findViewById(R.id.LayoutCell_ImageButton_Options);
            notificationImageButton = (ImageButton) itemView.findViewById(R.id.LayoutCell_ImageButton_Notification);
            eventTimeLinearLayout = (LinearLayout) itemView.findViewById(R.id.LayoutCell_LinearLayout_EventTime);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        private Event mEvent;

        public MyMenuItemClickListener(int position) {
            this.position = position;
            this.mEvent = eventList.get(position);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = null;
            switch (menuItem.getItemId()) {
                case R.id.Popup_Item_Edit:
                    intent = new Intent(context, EditEventActivity.class);
                    intent.putExtra("eventTitle", mEvent.getTitle());
                    intent.putExtra("eventDate", mEvent.getDate());
                    intent.putExtra("eventTime", mEvent.getTime());
                    calendarFragment.startActivityForResult(intent, EDIT_EVENT_ACTIVITY_REQUEST_CODE);
                    return true;
                case R.id.Popup_Item_Delete:
                    deleteEvent(mEvent.getId());
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, eventList.size());
                    notifyDataSetChanged();
                    calendarFragment.setUpCalendar();
                    Toast.makeText(context, "Event removed!", Toast.LENGTH_SHORT).show();
                    if (eventList.isEmpty()) {
                        alertDialog.dismiss();
                    }
                    return true;
                case R.id.Popup_Item_Share:
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, mEvent.toString());
                    intent.setType("text/plain");
                    calendarFragment.startActivity(Intent.createChooser(intent, null));
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

                    calendarFragment.startActivity(Intent.createChooser(intent, "Send Email"));
                    return true;
            }

            return false;
        }
    }

    private void deleteEvent(int eventId) {
        dbHelper = new DBHelper(context);
        dbHelper.deleteEvent(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteNotificationByEventId(dbHelper.getWritableDatabase(), eventId);
        dbHelper.close();
    }

    private boolean isAlarmed(String eventTitle, String date, String time) {
        boolean alarmed = false;
        dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEvent(sqLiteDatabase, eventTitle, date, time);
        while (cursor.moveToNext()) {
            String notify = cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTIFY));
            if (notify.equals("true")) {
                alarmed = true;
            } else {
                alarmed = false;
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return alarmed;
    }

    private boolean isAllDay(String eventTitle, String date, String time) {
        boolean isAllDay = false;
        dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEvent(sqLiteDatabase, eventTitle, date, time);
        while (cursor.moveToNext()) {
            String notify = cursor.getString(cursor.getColumnIndex(DBTables.EVENT_ALL_DAY));
            if (notify.equals("true")) {
                isAllDay = true;
            } else {
                isAllDay = false;
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return isAllDay;
    }
}
