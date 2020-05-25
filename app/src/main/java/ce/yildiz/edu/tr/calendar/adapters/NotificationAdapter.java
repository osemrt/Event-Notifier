package ce.yildiz.edu.tr.calendar.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.models.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;

    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_remainder_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);

        holder.cardView.setVisibility(View.VISIBLE);

        holder.reminderTimeTextView.setText(notification.getTime());
        holder.cancelNotificationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifications.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notifications.size());
                notifyDataSetChanged();
                holder.cardView.setVisibility(View.GONE);

            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_alert_dialog_notification, null, false);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        final RadioGroup notificationPreferenceRadioGroup = (RadioGroup) dialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        Button backButton = (Button) dialogView.findViewById(R.id.AlertDialogLayout_Button_Back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        holder.reminderTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationPreferenceRadioGroup.check(getIdOfRadioButton(holder.reminderTimeTextView.getText().toString()));
                alertDialog.show();
            }
        });

        ((RadioGroup) dialogView.findViewById(R.id.AlertDialogLayout_RadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                RadioButton selectedPreferenceRadioButton = (RadioButton) dialogView.findViewById(buttonId);
                notifications.set(position, new Notification(notification.getId(), notification.getChannelId(), selectedPreferenceRadioButton.getText().toString()));
                notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

    }

    private int getIdOfRadioButton(String text) {
        switch (text) {
            case "10 minutes before":
                return R.id.AlertDialogLayout_Notification_RadioButton_10minBefore;
            case "1 hour before":
                return R.id.AlertDialogLayout_Notification_RadioButton_1hourBefore;
            case "1 day before":
                return R.id.AlertDialogLayout_Notification_RadioButton_1dayBefore;
            case "At the time of event":
                return R.id.AlertDialogLayout_Notification_RadioButton_AtTheTimeOfEvent;
        }

        return R.id.AlertDialogLayout_Notification_RadioButton_AtTheTimeOfEvent;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView reminderTimeTextView;
        private ImageButton cancelNotificationImageButton;
        private LinearLayout rootLinearLayout;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderTimeTextView = (TextView) itemView.findViewById(R.id.ReminderListLayout_TextView_Notification);
            cancelNotificationImageButton = (ImageButton) itemView.findViewById(R.id.ReminderListLayout_ImageButton_Cancel);
            rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.ReminderListLayout_LinearLayout_Root);
            cardView = (CardView) itemView.findViewById(R.id.ReminderListLayout_CardView);
        }

    }


}
