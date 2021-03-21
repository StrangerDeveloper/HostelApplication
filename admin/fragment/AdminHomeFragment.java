package com.example.hostelrecommendationsystem.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.admin.Adapter.HostelAdapter;
import com.example.hostelrecommendationsystem.admin.model.AdminResponse;
import com.example.hostelrecommendationsystem.admin.model.Hostel;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.UtilClass;
import com.example.hostelrecommendationsystem.utils.sharedPref.SharedPrefHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mActivity = (AppCompatActivity) getActivity();
        rvHostel = view.findViewById(R.id.rv_hostels);
        rvHostel.setLayoutManager(new LinearLayoutManager(mActivity));
        getHostels();
        if (SharedPrefHelper.getmHelper().getAdminResponse() != null) {
            String adminId = new Gson().fromJson(SharedPrefHelper.getmHelper().getAdminResponse(), AdminResponse.class).getId();

        }

        Button btn = view.findViewById(R.id.ext_fab_add_hotel);
        btn.setOnClickListener(v -> {
            //startActivity(new Intent(mActivity, AddHostelActivity.class));
            UtilClass.loadFragment(new AddHostelFragment(), mActivity, R.id.admin_frame_layout);
        });

    }

    private List<Hostel> mHostelList;
    private FirebaseUser firebaseUser;

    private void getHostels() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.HOSTEL);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mHostelList = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Hostel hostel = child.getValue(Hostel.class);
                        assert hostel != null;
                        if (hostel.getAdminId().equalsIgnoreCase(firebaseUser.getUid())) {
                            mHostelList.add(hostel);
                            if (mHostelList.size() > 0) {
                                HostelAdapter hostelAdapter = new HostelAdapter(mActivity, mHostelList);
                                rvHostel.setAdapter(hostelAdapter);
                                hostelAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(mActivity, "no hostel has been added yet!", Toast.LENGTH_SHORT).show();
                            }
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