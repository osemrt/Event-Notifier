package ce.yildiz.edu.tr.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyCalendarView extends LinearLayout implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private static final int MAX_CALENDAR_DAYS = 42;

    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);

    private List<Date> dates = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private Context context;
    private ImageButton prevButton, nextButton;
    private TextView currentDate;
    private GridView gridView;


    public MyCalendarView(Context context) {
        super(context);
        this.context = context;
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeViews();

    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initializeViews() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.calendar_layout, this);

        nextButton = view.findViewById(R.id.CalendarLayout_Button_Next);
        prevButton = view.findViewById(R.id.CalendarLayout_Button_Prev);
        currentDate = view.findViewById(R.id.CalendarLayout_TextView_CurrentDate);
        gridView = view.findViewById(R.id.CalendarLayout_GridView_Dates);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.CalendarLayout_Button_Prev:
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
                break;
            case R.id.CalendarLayout_Button_Next:
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
                break;

        }

    }

    public void setUpCalendar() {
        currentDate.setText(dateFormat.format(calendar.getTime()));
    }

}
