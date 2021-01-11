package com.example.hostelrecommendationsystem.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.admin.model.Hostel;
import com.example.hostelrecommendationsystem.utils.AppConstant;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HostelAdapter extends RecyclerView.Adapter<HostelAdapter.HostelViewHolder> {
    private Context mContext;
    private List<Hostel> mHostelList;

    public HostelAdapter(Context mContext, List<Hostel> mHostelList) {
        this.mContext = mContext;
        this.mHostelList = mHostelList;
    }

    @NonNull
    @Override
    public HostelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view
                = inflater.inflate(R.layout.single_hostel_items_list, parent, false);
        return new HostelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HostelViewHolder holder, int position) {
        Hostel hostel = mHostelList.get(position);
        holder.setData(hostel);
    }

    @Override
    public int getItemCount() {
        return mHostelList.size();
    }

    public class HostelViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgHostel;
        private TextView txtHostelName, txtAddress, txtPhone, txtRoomTypes, txtOtherFacilities, txtPerRoomPrice;

        public HostelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHostel = itemView.findViewById(R.id.image_hostel);
            txtHostelName = itemView.findViewById(R.id.hostel_name);
            txtPhone = itemView.findViewById(R.id.hostel_phone);
            txtAddress = itemView.findViewById(R.id.hostel_address);
            txtRoomTypes = itemView.findViewById(R.id.hostel_room_type);
            txtOtherFacilities = itemView.findViewById(R.id.hostel_facilities);
            txtPerRoomPrice = itemView.findViewById(R.id.hostel_room_price);

        }

        public void setData(Hostel hostel) {
            Glide.with(mContext).load(AppConstant.FIREBASE_IMAGE_URL+hostel.getmHostelImages().get(0).getImageUrl()).into(imgHostel);
            txtHostelName.setText(hostel.getHostelName());
            txtPhone.setText(hostel.getContactNo());
            txtAddress.setText(hostel.getAddress());
            txtRoomTypes.setText(hostel.getRoomType());
            txtOtherFacilities.setText(hostel.getFacilities());
            txtPerRoomPrice.setText(hostel.getPricePerSeat());
        }
    }
}
