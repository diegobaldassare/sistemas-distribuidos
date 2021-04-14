import io.grpc.stub.StreamObserver;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GeoServiceImpl extends GeoServiceGrpc.GeoServiceImplBase {

    private final List<String[]> locations = new ArrayList<>();

    GeoServiceImpl() {
        loadLocations();
    }

    @Override
    public void getCountriesList(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {
        List<String> countries = new ArrayList<>();
        for (String[] data : locations) {
            if (!countries.contains(data[1])) countries.add(data[1]);
        }
        Geo.GeoResponse response = Geo.GeoResponse.newBuilder().addAllList(countries).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProvincesList(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {
        String country = request.getCountry();
        List<String> provinces = new ArrayList<>();
        for (String[] data : locations) {
            if (country.equals(data[1]) && !provinces.contains(data[2])) provinces.add(data[2]);
        }
        Geo.GeoResponse response = Geo.GeoResponse.newBuilder().addAllList(provinces).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getLocality(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {
        String province = request.getProvince();
        List<String> localities = new ArrayList<>();
        for (String[] data : locations) {
            if (province.equals(data[2])) localities.add(data[0]);
        }
        Geo.GeoResponse response = Geo.GeoResponse.newBuilder().addAllList(localities).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getLocation(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {
        String ip = request.getIp();
        String city, region, country;
        city = region = country = "";
        try {
            JSONObject json = new JSONObject(IOUtils.toString(
                    new URL("https://ipapi.co/" + ip + "/json/"), StandardCharsets.UTF_8)
            );
            city = json.getString("city");
            region = json.getString("region");
            country = json.getString("country_name");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Geo.GeoResponse response = Geo.GeoResponse.newBuilder().addList(city + ", " + region + ", " + country).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void loadLocations() {
        BufferedReader csvReader = null;
        try {
            csvReader = new BufferedReader(new FileReader("src/main/java/world-cities.csv"));
            String row = csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                locations.add(data);
            }
            csvReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
