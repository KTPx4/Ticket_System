package com.example.ticketbooking.ticket.fragment;

import android.os.Bundle;

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

import com.example.ticketbooking.R;
import com.example.ticketbooking.ticket.adapter.EventAdapter;

import java.util.ArrayList;
import java.util.List;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView listEvent;
    private EditText edSearch;
    private ProgressBar proLoading;
    private EventAdapter ticketAdapter;  // Biến này sẽ không cần phải là final
    private List<MyTicket> allTickets = new ArrayList<>();
    private List<MyTicket> filteredTickets = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 300; // Delay 300ms

    public TicketFragment() {
        // Required empty public constructor
    }

    public static TicketFragment newInstance(String param1, String param2) {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        ticketAdapter = new EventAdapter(getContext(), new ArrayList<>());
        TicketService ticketService = new TicketService(getContext());

        ticketService.GetMyTicket( new TicketService.ResponseCallback() {
            @Override
            public void onSuccess(List<MyTicket> MyTicket) {
                // Set the data to RecyclerView with adapter
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        EventAdapter ticketAdapter = new EventAdapter(getContext(), MyTicket);
//                        listEvent.setAdapter(ticketAdapter);
//                        proLoading.setVisibility(View.GONE); // Hide the progress bar when data is loaded
//                        ticketAdapter.notifyDataSetChanged();
//                    }
//                });
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
                        filterTickets(query);
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
}