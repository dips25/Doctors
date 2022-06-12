package com.example.doctorsjp.Models;

import java.util.ArrayList;

public class Patients {

        String id;
        String name;
        String email;
        String fulladdress;
        Double latitude;
        Double longitude;
        ArrayList<String> searchparams;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFulladdress() {
            return fulladdress;
        }

        public void setFulladdress(String fulladdress) {
            this.fulladdress = fulladdress;
        }

        public ArrayList<String> getSearchparams() {
            return searchparams;
        }

        public void setSearchparams(ArrayList<String> searchparams) {
            this.searchparams = searchparams;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }


