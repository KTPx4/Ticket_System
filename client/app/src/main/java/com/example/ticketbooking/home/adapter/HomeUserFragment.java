package com.example.ticketbooking.home.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbooking.R;
import com.example.ticketbooking.Event;
import services.HomeService;

import java.util.List;

public class HomeUserFragment extends Fragment {
    private RecyclerView recyclerSpecialEvents, recyclerMusicEvents, recyclerArtEvents, recyclerComedyEvents;
    private EventAdapter specialEventAdapter, musicEventAdapter, artEventAdapter, comedyEventAdapter;
    private HomeService homeService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_user, container, false);

        // Set up the toolbar
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        // Enable the fragment to handle menu items
        setHasOptionsMenu(true);

        // Initialize RecyclerViews
        recyclerSpecialEvents = rootView.findViewById(R.id.recycler_special_events);
        recyclerMusicEvents = rootView.findViewById(R.id.recycler_music_events);
        recyclerArtEvents = rootView.findViewById(R.id.recycler_art_events);
        recyclerComedyEvents = rootView.findViewById(R.id.recycler_comedy_events);

        // Set up RecyclerViews layout
        recyclerSpecialEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerMusicEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerArtEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerComedyEvents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize HomeService
        homeService = new HomeService(getActivity());

        // Load events
        loadEvents();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, android.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            Intent intent = new Intent(getActivity(), SearchUserActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.follow) {
            Intent intent = new Intent(getActivity(), FollowListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadEvents() {
        homeService.loadEvents(new HomeService.EventCallback() {
            @Override
            public void onEventsLoaded(List<Event> specialEvents, List<Event> musicEvents, List<Event> artEvents, List<Event> comedyEvents) {
                // Set data in RecyclerViews
                specialEventAdapter = new EventAdapter(specialEvents);
                recyclerSpecialEvents.setAdapter(specialEventAdapter);

                musicEventAdapter = new EventAdapter(musicEvents);
                recyclerMusicEvents.setAdapter(musicEventAdapter);

                artEventAdapter = new EventAdapter(artEvents);
                recyclerArtEvents.setAdapter(artEventAdapter);

                comedyEventAdapter = new EventAdapter(comedyEvents);
                recyclerComedyEvents.setAdapter(comedyEventAdapter);
            }

        });
    }
}
