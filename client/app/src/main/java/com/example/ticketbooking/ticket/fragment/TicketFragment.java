package com.example.ticketbooking.ticket.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ticketbooking.R;
import com.example.ticketbooking.order.CheckOutActivity;
import com.example.ticketbooking.ticket.EditPendingActivity;
import com.example.ticketbooking.ticket.adapter.event.EventAdapter;
import com.example.ticketbooking.ticket.adapter.pending.PendingAdapter;

import java.util.ArrayList;
import java.util.List;

import model.ticket.MyPending;
import model.ticket.MyTicket;
import services.OrderService;
import services.TicketService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OPTION = "option";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int optionFragment;


    private RecyclerView listEvent;
    private EditText edSearch;
    private ProgressBar proLoading;
    private EventAdapter ticketAdapter;  // Biến này sẽ không cần phải là final
    private PendingAdapter pendingAdapter;  // Biến này sẽ không cần phải là final

    private List<MyTicket> allTickets = new ArrayList<>();
    private List<MyTicket> filteredTickets = new ArrayList<>();

    private List<MyPending> allPendings = new ArrayList<>();
    private List<MyPending> filteredPendings = new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private TicketService ticketService;
    private static final int SEARCH_DELAY = 300; // Delay 300ms


    public TicketFragment( ) {

    }

    public static TicketFragment newInstance(int param1) {
        /* Option:
         *  0: Ticket has been bought
         *  1: Pendding ticket
         * */
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_OPTION, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            optionFragment = getArguments().getInt(ARG_OPTION);
        }
    }

    private ActivityResultLauncher<Intent> ActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Lấy dữ liệu trả về từ Activity
                    String updatedTicketId = result.getData().getStringExtra("idBuyTicket");
                    boolean isDelete = result.getData().getBooleanExtra("isDelete", false);
                    if (updatedTicketId != null) {
                        // Gọi hàm cập nhật adapter với ID vé đã được thay đổi
                        updateTicketData(updatedTicketId);
                    }
//                    if(isDelete == true)
//                    {
//                        for (int i = 0; i < allPendings.size(); i++) {
//                            MyPending pending = allPendings.get(i);
//                            if (pending.get_id().equals(updatedTicketId)) {
//                                // Fetch updated data for this ticket from server or local cache
//                                allPendings.remove(i);
//                                filteredPendings.clear();
//                                filteredPendings.addAll(allPendings);
//                                break;
//                            }
//                        }
//                        pendingAdapter.notifyDataSetChanged();
//                        return;
//                    }

                }
            }
    );



    private void updateTicketData(String updatedTicketId) {
        if(ticketService != null)
        {
            ticketService.GetPendingById(updatedTicketId, new TicketService.PendingCallback() {
                @Override
                public void onSuccess(MyPending newPendings) {
                    getActivity().runOnUiThread(()->{
                        for (int i = 0; i < allPendings.size(); i++) {
                            MyPending pending = allPendings.get(i);
                            if (pending.get_id().equals(updatedTicketId)) {
                                // Fetch updated data for this ticket from server or local cache
                                allPendings.set(i, newPendings);
                                filteredPendings.clear();
                                filteredPendings.addAll(allPendings);
                                break;
                            }
                        }
                        pendingAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onFailure(String error) {

                }
            });
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ticket_fragment_tickets, container, false);
        listEvent = view.findViewById(R.id.listEvent);
        listEvent.setLayoutManager(new LinearLayoutManager(getContext()));
        edSearch = view.findViewById(R.id.edSearch);
        proLoading = view.findViewById(R.id.proLoading);
        ticketService = new TicketService(getContext());
        // init adapter
        ticketAdapter = new EventAdapter(getContext(), new ArrayList<>());
        pendingAdapter = new PendingAdapter(getContext(), new ArrayList<>());


        TicketService ticketService = new TicketService(getContext());
        if(optionFragment == 0)
        {
            ticketService.GetMyTicket( new TicketService.MyTicketCallback() {
                @Override
                public void onSuccess(List<MyTicket> MyTicket) {
                    // Set the data to RecyclerView with adapter

                    getActivity().runOnUiThread(() -> {
                        allTickets.clear();
                        allTickets.addAll(MyTicket);
                        filteredTickets.addAll(MyTicket);
                        ticketAdapter = new EventAdapter(getContext(), filteredTickets); // Không cần final
                        listEvent.setAdapter(ticketAdapter);
                        proLoading.setVisibility(View.GONE); // Hide the progress bar when data is loaded
                        ticketAdapter.notifyDataSetChanged();
                    });

                }

                @Override
                public void onFailure(String error) {

                }
            });
        }
        else if(optionFragment == 1)
        {
            ticketService.GetMyPending( new TicketService.MyPendingCallback() {
                @Override
                public void onSuccess(List<MyPending> MyPending) {
                    // Set the data to RecyclerView with adapter

                    getActivity().runOnUiThread(() -> {
                        allPendings.clear();
                        allPendings.addAll(MyPending);
                        filteredPendings.addAll(MyPending);

                        pendingAdapter = new PendingAdapter(getContext(), filteredPendings, new PendingAdapter.OnEditTicketListener() {
                            @Override
                            public void onEditTicket(String buyTicketId) {
                                Log.d("IDBUY", "onSuccess: " + buyTicketId);
                                Intent intent = new Intent(getContext(), EditPendingActivity.class);
                                intent.putExtra("idBuyTicket", buyTicketId);
                                ActivityLauncher.launch(intent);
                            }

                            @Override
                            public void onCheckOutTicket(String buyTicketId, List<String> lisInfo) {
                                Intent intent = new Intent(getContext(), CheckOutActivity.class);
                                intent.putExtra("idBuyTicket", buyTicketId);
                                Log.d("onCheckOutTicket", "count: " + lisInfo.stream().count());

                                intent.putStringArrayListExtra("listInfo", new ArrayList<>(lisInfo));
                                ActivityLauncher.launch(intent);
                            }

                            @Override
                            public void onDeleteBuyTicket(String buyTicketId) {
                                OrderService orderService = new OrderService(getContext());
                                orderService.DeleteInfo(buyTicketId, "", new OrderService.ValidCallback() {
                                    @Override
                                    public void onSuccess(String success) {

                                        getActivity().runOnUiThread(()->{
                                            Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                                            for (int i = 0; i < allPendings.size(); i++) {
                                                MyPending pending = allPendings.get(i);
                                                if (pending.get_id().equals(buyTicketId)) {
                                                    // Fetch updated data for this ticket from server or local cache
                                                    allPendings.remove(i);
                                                    filteredPendings.clear();
                                                    filteredPendings.addAll(allPendings);
                                                    break;
                                                }
                                            }
                                            pendingAdapter.notifyDataSetChanged();
                                        });
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        getActivity().runOnUiThread(()->{
                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

                                        });
                                    }
                                });
                            }

                        });

                        listEvent.setAdapter(pendingAdapter);

                        proLoading.setVisibility(View.GONE); // Hide the progress bar when data is loaded
                        pendingAdapter.notifyDataSetChanged();
                    });

                }

                @Override
                public void onFailure(String error) {

                }
            });
        }

        // Add TextWatcher to the search EditText
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Cancel the previous search if it's still waiting
                handler.removeCallbacks(searchRunnable);

                // Schedule the search after 300ms
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String query = editable.toString().toLowerCase().trim();
                        if(optionFragment == 0)
                        {
                            filterTickets(query);
                        }
                        else if(optionFragment == 1)
                        {
                            filterPendings(query);
                        }
                    }
                };
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });



        return view;
    }

    private void filterTickets(String query) {
        filteredTickets.clear();
        if (query.isEmpty()) {
            filteredTickets.addAll(allTickets);
        } else {
            for (MyTicket ticket : allTickets) {
                if (ticket.getEvent().getName().toLowerCase().contains(query)) { // Replace with your filtering logic
                    filteredTickets.add(ticket);
                }
            }
        }
        ticketAdapter.notifyDataSetChanged();
    }

    private void filterPendings(String query) {
        filteredPendings.clear();
        if (query.isEmpty()) {
            filteredPendings.addAll(allPendings);
        } else {
            for (MyPending pending : allPendings) {
                if (pending.getEvent().getName().toLowerCase().contains(query)) { // Replace with your filtering logic
                    filteredPendings.add(pending);
                }
            }
        }
        pendingAdapter.notifyDataSetChanged();
    }
}