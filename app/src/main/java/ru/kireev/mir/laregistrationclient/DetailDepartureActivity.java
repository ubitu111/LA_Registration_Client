package ru.kireev.mir.laregistrationclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.kireev.mir.laregistrationclient.databinding.ActivityDetailDepartureBinding;
import ru.kireev.mir.laregistrationclient.pojo.Departure;
import ru.kireev.mir.laregistrationclient.viewmodels.DeparturesViewModel;

public class DetailDepartureActivity extends AppCompatActivity {
    private ActivityDetailDepartureBinding binding;
    private DeparturesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDepartureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(DeparturesViewModel.class);
        fillInTheFields();
    }

    public void onClickCallInforg(View view) {
        String toDial = "tel:" + binding.tvDetailDepartureInforgPhone.getText().toString();
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
    }

    public void onClickGoToForum(View view) {
        String toBrowser = binding.tvDetailDepartureForumTopic.getText().toString();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(toBrowser)));
    }

    private void fillInTheFields(){
        Intent intent = getIntent();
        int id = intent.getIntExtra("departure_id", 0);
        Departure departure = viewModel.getDepartureById(id);
        if (departure != null) {
            binding.tvDetailDepartureTopic.setText(departure.getMessageTopic());
            binding.tvDetailDepartureTime.setText(departure.getDepartureTime());
            binding.tvDetailDeparturePlace.setText(departure.getDeparturePlace());
            binding.tvDetailDepartureCoordinator.setText(departure.getCoordinator());
            binding.tvDetailDepartureInforg.setText(departure.getInforg());
            binding.tvDetailDepartureInforgPhone.setText(departure.getInforgPhoneNumber());
            binding.tvDetailDepartureForumTopic.setText(departure.getForumTopic());
        } else {
            Toast.makeText(this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
        }
    }
}
