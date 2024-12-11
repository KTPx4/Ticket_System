package com.example.ticketbooking.ticket.adapter.pending;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;


import java.util.ArrayList;
import java.util.List;

import model.ticket.MyPending;
import modules.LocalStorageManager;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.EventViewHolder> {
    private final Context context;
    private final List<MyPending> myTicketList;
    private final RecyclerView.RecycledViewPool sharedPool; // Tạo pool dùng chung

    private  final List<String> listInfo = new ArrayList<>();
    private OnEditTicketListener listener;
    private LocalStorageManager localStorageManager;

    public interface OnEditTicketListener {
        void onEditTicket(String ticketId);
    }

    public PendingAdapter(Context context, List<MyPending> myTicketList,OnEditTicketListener listener) {
        this.context = context;
        this.myTicketList = myTicketList;
        this.sharedPool = new RecyclerView.RecycledViewPool(); // Khởi tạo pool
        this.listener = listener;
        localStorageManager = new LocalStorageManager(context);
    }
    public PendingAdapter(Context context, List<MyPending> myTicketList) {
        this.context = context;
        this.myTicketList = myTicketList;
        this.sharedPool = new RecyclerView.RecycledViewPool(); // Khởi tạo pool
        localStorageManager = new LocalStorageManager(context);

    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item_pending, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        MyPending myPending = myTicketList.get(position);
        String typePay = myPending.getTypePayment().toLowerCase().equals("all") ? "Tất cả" : "Từng thành viên";
        String myId= localStorageManager.getIdUser();
        if(!myId.equals(myPending.getAccCreate().get_id()))
        {
            holder.btnEdit.setVisibility(View.GONE);
        }
        else{
            holder.btnEdit.setVisibility(View.VISIBLE);

        }

        holder.tvEventName.setText(myPending.getEvent().getName());
        holder.tvEventDate.setText(myPending.getEvent().getDate().getStart());
        holder.tvNameCreate.setText("Người tạo: "+myPending.getAccCreate().getName());
        holder.tvMembers.setText( "Thành viên: "+ myPending.getMembers().stream().count());
        holder.tvTypePay.setText("Loại mua vé: "+ typePay);

        // Set the TicketItemAdapter for the nested RecyclerView (listTicket)
        TicketPendingAdapter ticketItemAdapter = new TicketPendingAdapter(context, myPending.getTicketInfo(), new TicketPendingAdapter.OnTicketClickListener() {
            @Override
            public void onTicketClick(String infoId) {
                if(listInfo.contains(infoId))
                {
                    listInfo.remove(infoId);
                }
                else{
                    listInfo.add(infoId);
                }
            }

            @Override
            public void onDelTicket(String infoId) {
                // delete ticket from other list
                Log.d("ID INFO DEL" , "id: "+ infoId);
                // Tạo AlertDialog
                AlertDialog dialog =new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa vé ra khỏi thanh toán?")
                        .setPositiveButton("Hủy", (dialog1, which) -> { dialog1.dismiss();})

                        .setNegativeButton("Xóa", (dialog2, which) -> {

                            Toast.makeText(context, "Đã xóa vé: " + infoId, Toast.LENGTH_SHORT).show();
                        }).create();

                dialog.show();
                // Tùy chỉnh màu văn bản của các nút
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // Màu cho nút "Hủy"
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.inValid_Ticket));  // Màu cho nút "Xóa"

            }
        });
        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listInfo.stream().count() < 1)
                {
                    Toast.makeText(context, "Vui lòng chọn vé", Toast.LENGTH_SHORT).show();
                    return;
                }
                listInfo.forEach(id -> {
                    Log.d("ID INFO", "id: "+ id);
                });

            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditTicket(myPending.get_id());
                }
            }
        });
        holder.listTicket.setLayoutManager(new LinearLayoutManager(context));
        holder.listTicket.setAdapter(ticketItemAdapter);

        // Set shared RecycledViewPool
        holder.listTicket.setRecycledViewPool(sharedPool);

        ticketItemAdapter.notifyDataSetChanged();
        holder.idBuyTicket = myPending.get_id();

    }



    @Override
    public int getItemCount() {
        return myTicketList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String idBuyTicket;
        ConstraintLayout mainLayout;
        TextView tvEventName, tvEventDate, tvNameCreate, tvMembers, tvTypePay;
        RecyclerView listTicket;
        ImageButton btnDrop;
        Button btnEdit, btnPay;
        LinearLayout layoutTickets;
        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvName);
            tvEventDate = itemView.findViewById(R.id.tvDate);
            tvNameCreate = itemView.findViewById(R.id.tvNameCreate);
            tvMembers = itemView.findViewById(R.id.tvMembers);
            tvTypePay = itemView.findViewById(R.id.tvTypePay);
            listTicket = itemView.findViewById(R.id.listTicket);
            btnDrop = itemView.findViewById(R.id.btnDrop);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnPay = itemView.findViewById(R.id.btnPay);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            layoutTickets = itemView.findViewById(R.id.layoutTicket);
            setupLayoutTransition((ViewGroup) layoutTickets.getParent());

            btnDrop = itemView.findViewById(R.id.btnDrop);

            btnDrop.setOnClickListener(this);
            tvEventDate.setOnClickListener(this);
            tvEventName.setOnClickListener(this);

            // Set touch listener
            mainLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Change background color or apply other visual effect
                            animateBackgroundColor(v, itemView.getContext().getColor(R.color.Wine_Red));
                            setTextColor(Color.WHITE);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            // Revert to original background color
                            animateBackgroundColor(v, Color.WHITE);
                            setTextColor(itemView.getContext().getColor(R.color.grey));
                            break;
                    }
                    return false; // Return false to allow other click events like long click
                }
            });

            mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Tạo AlertDialog
                    AlertDialog dialog =new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Xác nhận")
                            .setMessage("Bạn có chắc chắn muốn xóa?")
                            .setPositiveButton("Hủy", (dialog1, which) -> {
                                // Xử lý khi người dùng nhấn "Xóa"
                                dialog1.dismiss();
                            })
                            .setNegativeButton("Xóa", (dialog2, which) -> {
                                // Xử lý khi người dùng nhấn "Hủy"
                                Toast.makeText(itemView.getContext(), "Đã xóa: " + idBuyTicket, Toast.LENGTH_SHORT).show();
                            }).create();

                    dialog.show();
                    // Tùy chỉnh màu văn bản của các nút
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK); // Màu cho nút "Hủy"
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(itemView.getContext().getColor(R.color.inValid_Ticket));  // Màu cho nút "Xóa"
                    return true; // Trả về true để ngăn chặn các sự kiện khác
                }
            });
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
            else if( id == R.id.btnPay)
            {
            }
            else if (id == R.id.btnEdit)
            {

            }

        }
        void setTextColor(int color)
        {
            if(itemView.getContext().getColor(R.color.grey) == color)
            {
                tvEventName.setTextColor(Color.BLACK);
            }
            else tvEventName.setTextColor(color);

            tvMembers.setTextColor(color);
            tvTypePay.setTextColor(color);
            tvNameCreate.setTextColor(color);
        }

        private void animateBackgroundColor(View view, int color) {
            int currentColor = Color.WHITE;
            if (view.getBackground() instanceof ColorDrawable) {
//                currentColor = ((ColorDrawable) view.getBackground()).getColor();
                currentColor =  Color.WHITE;
            }
            ObjectAnimator colorFade = ObjectAnimator.ofArgb(view, "backgroundColor", currentColor, color);
            colorFade.setDuration(450); // 400ms duration
            colorFade.start();

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



    }



}