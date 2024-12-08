package com.example.ticketbooking.ticket.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;

import java.util.List;

import model.ticket.MyTicket;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final Context context;
    private final List<MyTicket> myTicketList;

    public EventAdapter(Context context, List<MyTicket> myTicketList) {
        this.context = context;
        this.myTicketList = myTicketList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        MyTicket myTicket = myTicketList.get(position);
        holder.tvEventName.setText(myTicket.getEvent().getName());
        holder.tvEventDate.setText(myTicket.getEvent().getDate().getStart());

        // Set the TicketItemAdapter for the nested RecyclerView (listTicket)
        TicketItemAdapter ticketItemAdapter = new TicketItemAdapter(context, myTicket.getTickets());
        holder.listTicket.setLayoutManager(new LinearLayoutManager(context));
        holder.listTicket.setAdapter(ticketItemAdapter);
        ticketItemAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myTicketList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvEventName, tvEventDate;
        RecyclerView listTicket;
        ImageButton btnDrop;
        LinearLayout layoutTickets;
        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvName);
            tvEventDate = itemView.findViewById(R.id.tvDate);
            listTicket = itemView.findViewById(R.id.listTicket);
            btnDrop = itemView.findViewById(R.id.btnDrop);

            layoutTickets = itemView.findViewById(R.id.layoutTicket);
            setupLayoutTransition((ViewGroup) layoutTickets.getParent());

            btnDrop = itemView.findViewById(R.id.btnDrop);

            btnDrop.setOnClickListener(this);
            tvEventDate.setOnClickListener(this);
            tvEventName.setOnClickListener(this);

        }

        private void expand(final View view) {
            // Đo chiều cao chính xác
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            int targetHeight = view.getMeasuredHeight();

            // Đặt chiều cao ban đầu cho RecyclerView (nếu bên trong)
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.getLayoutParams().height = targetHeight;
                recyclerView.requestLayout();
            }

            // Animation từ 0 đến chiều cao đo được
            view.getLayoutParams().height = 0;
            view.setVisibility(View.VISIBLE);
            ValueAnimator animator = slideAnimator(0, targetHeight);
            animator.start();
        }


        private void collapse(final View view) {
            final int initialHeight = view.getHeight();

            // Animation từ chiều cao ban đầu về 0
            ValueAnimator animator = slideAnimator(initialHeight, 0);
            animator.addUpdateListener(valueAnimator -> {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Đặt visibility thành GONE sau khi animation kết thúc
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();
        }

        private ValueAnimator slideAnimator(int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.setDuration(300);
            return animator;
        }


        private void setupLayoutTransition(ViewGroup viewGroup) {
            LayoutTransition transition = new LayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.setDuration(400);
            viewGroup.setLayoutTransition(transition);
        }


        @Override
        public void onClick(View view) {
            int id = view.getId();
            if(id == R.id.btnDrop || id == R.id.tvName || id == R.id.tvDate)
            {
                if (layoutTickets.getVisibility() == View.GONE) {
                    expand(layoutTickets);
                    btnDrop.setImageResource(R.drawable.ic_drop_down);
                } else {
                    collapse(layoutTickets);
                    btnDrop.setImageResource(R.drawable.ic_drop_up);

                }
            }

        }
    }

}


