import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GeoServiceImpl extends GeoServiceGrpc.GeoServiceImplBase {

    private final List<String[]> locations = new ArrayList<>();

    GeoServiceImpl() {
        loadLocations();
    }

    @Override
    public void getCountriesList(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {
        List<String> countries = new ArrayList<>();
        for (String[] data: locations) {
            if (!countries.contains(data[1])) countries.add(data[1]);
        }
        Geo.GeoResponse response = Geo.GeoResponse.newBuilder().addAllList(countries).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProvincesList(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {

    }

    @Override
    public void getLocality(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {

    }

    @Override
    public void getLocation(Geo.GeoRequest request, StreamObserver<Geo.GeoResponse> responseObserver) {

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
