package com.example.ticketbooking.ticket.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.adapter.EventAdapter;
import com.example.ticketbooking.ticket.adapter.PendingAdapter;

import java.util.ArrayList;
import java.util.List;

import model.ticket.MyPending;
import model.ticket.MyTicket;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ticket_fragment_tickets, container, false);
        listEvent = view.findViewById(R.id.listEvent);
        listEvent.setLayoutManager(new LinearLayoutManager(getContext()));
        edSearch = view.findViewById(R.id.edSearch);
        proLoading = view.findViewById(R.id.proLoading);

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
                        pendingAdapter = new PendingAdapter(getContext(), filteredPendings); // Không cần final
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