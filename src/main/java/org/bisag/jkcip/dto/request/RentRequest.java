package org.bisag.jkcip.dto.request;

public class RentRequest {
    private String name;
    private String email;     // NEW: for user confirmation
    private String mobile;
    private String state;
    private String district;
    private String village;
    private String rentTime;      // e.g., 1hour, halfday, fullday, etc.
    private String equipmentType; // JCB / Breaker
    private Long rate;            // total computed on frontend

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getRentTime() { return rentTime; }
    public void setRentTime(String rentTime) { this.rentTime = rentTime; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public Long getRate() { return rate; }
    public void setRate(Long rate) { this.rate = rate; }
}