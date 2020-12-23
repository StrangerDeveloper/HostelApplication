package com.example.hostelrecommendationsystem.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.admin.Adapter.HostelAdapter;
import com.example.hostelrecommendationsystem.admin.model.Hostel;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.UtilClass;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdminHomeFragment extends Fragment {


    public AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private AppCompatActivity mActivity;
    private RecyclerView rvHostel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        initViews();
        return view;
    }

    private void initViews() {
        mActivity = (AppCompatActivity) getActivity();
        rvHostel = mActivity.findViewById(R.id.rv_hostels);
        rvHostel.setLayoutManager(new LinearLayoutManager(mActivity));

        getHostels();

        ExtendedFloatingActionButton extendedFab = mActivity.findViewById(R.id.ext_fab_add_hotel);
        extendedFab.setOnClickListener(v -> {
            UtilClass.loadFragment(new AddHostelFragment(), mActivity, R.id.admin_frame_layout);
        });

    }

    private List<Hostel> mHostelList;

    private void getHostels() {
        mHostelList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.HOSTEL);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Hostel hostel = child.getValue(Hostel.class);
                        assert hostel != null;
                        mHostelList.add(hostel);
                        if (mHostelList.size() > 0) {
                            HostelAdapter hostelAdapter = new HostelAdapter(mActivity, mHostelList);
                            rvHostel.setAdapter(hostelAdapter);
                        } else {
                            Toast.makeText(mActivity, "no hostel has been added yet!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}