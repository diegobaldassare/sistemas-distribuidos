import services.auth.AuthClient;
import services.auth.AuthServer;
import services.geo.GeoClient;
import services.geo.GeoServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String serviceName = System.getenv("SERVICE_NAME");
        System.out.println("SERVICE_NAME: " + serviceName);
        switch (serviceName) {
            case "auth-client":
                try {
                    AuthClient.main(null);
                    System.out.println("Succesfully deployed: " + serviceName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "auth-server":
                try {
                    AuthServer.main(null);
                    System.out.println("Succesfully deployed: " + serviceName);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                break;
            case "geo-client":
                try {
                    GeoClient.main(null);
                    System.out.println("Succesfully deployed: " + serviceName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "geo-server":
                try {
                    GeoServer.main(null);
                    System.out.println("Succesfully deployed: " + serviceName);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Requested application not found");
                break;
        }
    }

}
