package ce.yildiz.edu.tr.calendar.adapters;

import android.app.AlertDialog;
import android.content.Context;
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

import java.util.Calendar;
import java.util.List;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBStructure;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.views.CalendarFragment;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;
    private DBHelper dbHelper;
    private CalendarFragment calendarFragment;
    private AlertDialog alertDialog;

    public EventAdapter(Context context, List<Event> eventList, AlertDialog alertDialog, CalendarFragment calendarFragment, AlertDialog dialog) {
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
            switch (menuItem.getItemId()) {
                case R.id.Popup_Item_Delete:
                    deleteEvent(mEvent.getTitle(), mEvent.getDate(), mEvent.getTime());
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
                case R.id.Popup_Item_Edit:
                    Toast.makeText(context, "Edit is clicked!", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    }

    private void deleteEvent(String eventTitle, String date, String time) {
        dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.deleteEvent(sqLiteDatabase, eventTitle, date, time);
        dbHelper.close();
    }

    private boolean isAlarmed(String eventTitle, String date, String time) {
        boolean alarmed = false;
        dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readNotification(sqLiteDatabase, eventTitle, date, time);
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
        Cursor cursor = dbHelper.readIsAllDay(sqLiteDatabase, eventTitle, date, time);
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
