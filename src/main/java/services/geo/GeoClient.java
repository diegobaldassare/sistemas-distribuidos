package services.geo;

import geo.Geo.*;
import geo.GeoServiceGrpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static geo.GeoServiceGrpc.newBlockingStub;

public class GeoClient {
    private final List<GeoServiceBlockingStub> stubs;

    public GeoClient(ManagedChannel[] channels) {
        stubs = new ArrayList<>();
        for (ManagedChannel mc : channels) {
            stubs.add(newBlockingStub(mc));
        }
    }

    public void getLocation(List<String> ips) {
        System.out.println("\n***");
        for (String ip : ips) {
            GetLocationRequest request = GetLocationRequest.newBuilder()
                    .setIp(ip)
                    .build();
            getLocation(request);
        }
    }

    private void getLocation(GetLocationRequest request) {
        try {
            GetLocationResponse response = randomStub().getLocation(request);
            System.out.println("IP Country: " + response.getCountry());
            System.out.println("IP Province: " + response.getProvince());
            System.out.println("\nOther Provinces in " + response.getCountry() + ":");
            getProvinces(response.getCountry());
            System.out.println("***");
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void getProvinces(String country) {
        ListProvincesRequest request = ListProvincesRequest.newBuilder()
                .setCountry(country)
                .build();
        listProvinces(request);
    }

    private void listProvinces(ListProvincesRequest request) {
        try {
            ListProvincesResponse response = randomStub().listProvinces(request);
            String[] provinces = response.getProvincesList().toArray(new String[0]);
            for (String province : provinces) {
                System.out.println("\t- " + province);
                getLocalities(request.getCountry(), province);
            }
            System.out.println("***");
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void getLocalities(String country, String province) {
        ListLocalitiesRequest request = ListLocalitiesRequest.newBuilder()
                .setCountry(country)
                .setProvince(province)
                .build();
        listLocalities(request);
    }

    private void listLocalities(ListLocalitiesRequest request) {
        try {
            ListLocalitiesResponse response = randomStub().listLocalities(request);
            String[] localities = response.getLocalitiesList().toArray(new String[0]);
            for (String locality : localities) {
                System.out.println("\t\t- " + locality);
            }
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private GeoServiceBlockingStub randomStub() {
        return stubs.get((int) Math.round(Math.random() * (stubs.size() - 1)));
    }

    public static void main(String[] args) throws Exception {
        String serviceIP = InetAddress.getLocalHost().getHostAddress();
        List<String> testingIPs = Arrays.asList(
                "8.8.8.8",
                "1.1.1.1"
        );
        ManagedChannel[] channels = new ManagedChannel[]{createChannel(serviceIP, GeoServer.getPORT())};
        GeoClient client = new GeoClient(channels);
        try {
            client.getLocation(testingIPs);
        } finally {
            for (ManagedChannel c : channels) {
                c.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    public static ManagedChannel createChannel(String ip, int port) {
        return ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
    }

}
