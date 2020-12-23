package com.example.hostelrecommendationsystem.admin.model;

import java.util.List;

public class Hostel {
    private String uID, hostelName, address, location, contactNo, city;
    private String facilities, totalRooms, roomType, pricePerSeat;
    private List<HostelImages> mHostelImages;

    public Hostel() {
    }

    public Hostel(String uID, String hostelName, String address, String city,
                  String location, String contactNo, String facilities,
                  String totalRooms, String roomType, String pricePerSeat,
                  List<HostelImages> mHostelImages) {
        this.uID = uID;
        this.hostelName = hostelName;
        this.address = address;
        this.location = location;
        this.contactNo = contactNo;
        this.city = city;
        this.facilities = facilities;
        this.totalRooms = totalRooms;
        this.roomType = roomType;
        this.pricePerSeat = pricePerSeat;
        this.mHostelImages = mHostelImages;
    }

    public String getuID() {
        return uID;
    }

    public String getHostelName() {
        return hostelName;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getFacilities() {
        return facilities;
    }

    public String getCity() {
        return city;
    }

    public String getTotalRooms() {
        return totalRooms;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getPricePerSeat() {
        return pricePerSeat;
    }

    public List<HostelImages> getmHostelImages() {
        return mHostelImages;
    }

    public static class HostelImages {
        private String imageUrl;

        public HostelImages() {
        }

        public HostelImages(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
