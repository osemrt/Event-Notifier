package ce.yildiz.edu.tr.calendar.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBStructure;
import ce.yildiz.edu.tr.calendar.models.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;
    private AlertDialog alertDialog;
    private DBHelper dbHelper;

    public EventAdapter(Context context, List<Event> eventList, AlertDialog alertDialog) {
        this.context = context;
        this.eventList = eventList;
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
        holder.eventTitle.setText(event.getEVENT());

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.options, position);
            }
        });

        if (isAlarmed(event.getDATE(), event.getEVENT(), event.getTIME())) {
            // TODO: Show the notification icon
        } else {

        }

        //notifyDataSetChanged();

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

        public View mImageButton;
        private ImageView eventColor;
        private TextView eventTitle;
        private TextView eventTime;
        private TextView eventNote;
        private ImageButton options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventColor = (ImageView) itemView.findViewById(R.id.LayoutCell_ImageView_EventColor);
            eventTitle = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTitle);
            eventTime = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTime);
            eventNote = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventNote);
            options = (ImageButton) itemView.findViewById(R.id.LayoutCell_ImageButton_Options);
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
                    deleteCalendarEvent(mEvent.getEVENT(), mEvent.getDATE(), mEvent.getTIME());
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, eventList.size());
                    notifyDataSetChanged();
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

    private void deleteCalendarEvent(String eventTitle, String date, String time) {
        dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.deleteEvent(eventTitle, date, time, sqLiteDatabase);
        dbHelper.close();
    }

    private boolean isAlarmed(String date, String eventTitle, String time) {
        boolean alarmed = false;
        dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readIDEvents(eventTitle, date, time, sqLiteDatabase);
        while (cursor.moveToNext()) {
            String notify = cursor.getString(cursor.getColumnIndex(DBStructure.NOTIFY));
            if (notify.equals("on")) {
                alarmed = true;
            } else {
                alarmed = false;
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return false;
    }
}
