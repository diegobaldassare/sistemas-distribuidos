package services.geo;

import geo.Geo.*;
import geo.GeoServiceGrpc.*;
import io.grpc.stub.StreamObserver;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import services.geo.util.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GeoService extends GeoServiceImplBase {

    private final List<Location> locations = new ArrayList<>();
    private final Map<GetLocationRequest, GetLocationResponse> cache = new HashMap<>();

    GeoService() {
        loadLocations();
    }

    @Override
    public void listCountries(ListCountriesRequest request, StreamObserver<ListCountriesResponse> responseObserver) {
        Set<String> countries = new HashSet<>();
        for (Location l : locations) {
            String country = l.getCountry();
            countries.add(country);
        }
        ListCountriesResponse response = ListCountriesResponse.newBuilder().addAllCountries(countries).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listProvinces(ListProvincesRequest request, StreamObserver<ListProvincesResponse> responseObserver) {
        String requestCountry = request.getCountry();
        Set<String> provinces = new HashSet<>();
        for (Location l : locations) {
            String country = l.getCountry();
            String province = l.getProvince();
            if (requestCountry.equals(country)) provinces.add(province);
        }
        ListProvincesResponse response = ListProvincesResponse.newBuilder().addAllProvinces(provinces).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listLocalities(ListLocalitiesRequest request, StreamObserver<ListLocalitiesResponse> responseObserver) {
        String requestCountry = request.getCountry();
        String requestProvince = request.getProvince();
        Set<String> localities = new HashSet<>();
        for (Location l : locations) {
            String country = l.getCountry();
            String province = l.getProvince();
            String locality = l.getLocality();
            if (requestCountry.equals(country) && requestProvince.equals(province)) localities.add(locality);
        }
        ListLocalitiesResponse response = ListLocalitiesResponse.newBuilder().addAllLocalities(localities).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getLocation(GetLocationRequest request, StreamObserver<GetLocationResponse> responseObserver) {
        if (cache.containsKey(request)) {
            GetLocationResponse response = cache.get(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            try {
                GetLocationResponse response = ipServiceResponse(request);
                cache.put(request, response);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private GetLocationResponse ipServiceResponse(GetLocationRequest request) throws IOException {
        String requestIP = request.getIp();
        String province;
        String country;
        JSONObject json = new JSONObject(IOUtils.toString(
                new URL("https://ipapi.co/" + requestIP + "/json/"), StandardCharsets.UTF_8)
        );
        province = json.getString("region");
        country = json.getString("country_name");
        GetLocationResponse response = GetLocationResponse.newBuilder()
                .setCountry(country)
                .setProvince(province)
                .build();
        return response;
    }

    private void loadLocations() {
        BufferedReader csvReader;
        try {
            csvReader = new BufferedReader(new FileReader("src/main/resources/world-cities.csv"));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                Location l = new Location(
                        data[1],
                        data[2],
                        data[0],
                        data[3]
                );
                locations.add(l);
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
