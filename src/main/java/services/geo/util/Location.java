package services.geo.util;

public class Location {

    private final String country;
    private final String province;
    private final String locality;
    private final String code;

    public Location(String country, String province, String locality, String code) {
        this.country = country;
        this.province = province;
        this.locality = locality;
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getLocality() {
        return locality;
    }

    public String getCode() {
        return code;
    }

}
