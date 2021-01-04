package com.mycornership.betlink.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mycornership.betlink.R;
import com.mycornership.betlink.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NHolder> {
    List<Notification> notifications;
    Context context;

    public NotificationAdapter(Context c, List<Notification> notificationList) {
        this.context = c;
        this.notifications = notificationList;
    }

    @NonNull
    @Override
    public NHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_dialog, parent, false);
        return new NHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.tv_date.setText(notification.getDate());
        holder.tv_msg.setText(notification.getMessage());
        if(position == notifications.size())
            holder.div.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NHolder extends RecyclerView.ViewHolder{
        TextView tv_msg, tv_date;
        View div;

        public NHolder(@NonNull View itemView) {
            super(itemView);
            tv_msg = itemView.findViewById(R.id.mssg);
            tv_date = itemView.findViewById(R.id.date);
        }
    }
}
