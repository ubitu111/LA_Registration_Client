package ru.kireev.mir.laregistrationclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.kireev.mir.laregistrationclient.adapters.DepartureAdapter;
import ru.kireev.mir.laregistrationclient.databinding.ActivityAllActiveDeparturesBinding;
import ru.kireev.mir.laregistrationclient.pojo.Departure;
import ru.kireev.mir.laregistrationclient.viewmodels.DeparturesViewModel;

public class AllActiveDeparturesActivity extends AppCompatActivity {
    private ActivityAllActiveDeparturesBinding binding;
    private DeparturesViewModel viewModel;
    private DepartureAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllActiveDeparturesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(DeparturesViewModel.class);
        binding.rvAllActiveDepartures.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DepartureAdapter();
        binding.rvAllActiveDepartures.setAdapter(adapter);
        viewModel.getDepartures().observe(this, departures -> adapter.setDepartures(departures));
        adapter.setOnDepartureClickListener(position -> {
            Intent intent = new Intent(this, DetailDepartureActivity.class);
            Departure departure = adapter.getDepartures().get(position);
            int id = departure.getId();
            intent.putExtra("departure_id", id);
            startActivity(intent);
        });

        //добавляем свайп (при свайпе влево или вправо удаляем запись)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Departure departure = adapter.getDepartures().get(position);
                viewModel.deleteDeparture(departure);
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.rvAllActiveDepartures);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fill_out_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_action_profile_volunteer:
                Intent intent = new Intent(this, VolunteerProfileActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
