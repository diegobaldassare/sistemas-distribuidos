import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class GeoClient {
    private final GeoServiceGrpc.GeoServiceBlockingStub blockingStub;

    public GeoClient(Channel channel) {
        blockingStub = GeoServiceGrpc.newBlockingStub(channel);
    }

    public void getCountriesList() {
        Geo.GeoRequest request = Geo.GeoRequest
                .newBuilder()
                .build();
        Geo.GeoResponse response;
        try {
            response = blockingStub.getCountriesList(request);
            System.out.println("Countries: " + response.getListList().toString());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [name [target]] \n");
                System.exit(1);
            }
        }
        if (args.length > 1) {
            target = args[1];
        }

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        try {
            GeoClient client = new GeoClient(channel);
            client.getCountriesList();
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
