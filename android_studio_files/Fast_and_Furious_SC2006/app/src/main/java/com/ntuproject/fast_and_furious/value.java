package com.ntuproject.fast_and_furious;

public class value {
    String CarParkID;
    String Area;
    String Development;
    String Location;
    Integer AvailableLots;
    String LotType;
    String Agency;

    public String getAgency() {
        return Agency;
    }

    public String getArea() {
        return Area;
    }

    public Integer getAvailableLots() {
        return AvailableLots;
    }

    public String getCarParkID() {
        return CarParkID;
    }

    public String getDevelopment() {
        return Development;
    }

    public String getLocation() {
        return Location;
    }

    public String getLotType() {
        return LotType;
    }

    public void setAgency(String agency) {
        Agency = agency;
    }

    public void setArea(String area) {
        Area = area;
    }

    public void setAvailableLots(Integer availableLots) {
        AvailableLots = availableLots;
    }

    public void setCarParkID(String carParkID) {
        CarParkID = carParkID;
    }

    public void setDevelopment(String development) {
        Development = development;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setLotType(String lotType) {
        LotType = lotType;
    }

    @Override
    public String toString() {
        return "User{" +
                "CarParkID='" + CarParkID + '\'' +
                ", Area=" + Area +
                ", Development=" + Development +
                ", Location=" + Location +
                ", AvailableLots=" + AvailableLots +
                ", LotType=" + LotType +
                ", Agency=" + Agency +
                '}';

    }

}
