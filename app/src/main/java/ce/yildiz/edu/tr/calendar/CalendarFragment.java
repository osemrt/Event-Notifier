package ce.yildiz.edu.tr.calendar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements View.OnClickListener {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {

        nextButton = view.findViewById(R.id.CalenderFragment_Button_Next);
        prevButton = view.findViewById(R.id.CalenderFragment_Button_Prev);
        currentDate = view.findViewById(R.id.CalenderFragment_TextView_CurrentDate);
        gridView = view.findViewById(R.id.CalenderFragment_GridView_Dates);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.CalenderFragment_Button_Prev:
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
                break;
            case R.id.CalenderFragment_Button_Next:
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
                break;

        }

    }

    public void setUpCalendar() {
        currentDate.setText(dateFormat.format(calendar.getTime()));
    }
}
