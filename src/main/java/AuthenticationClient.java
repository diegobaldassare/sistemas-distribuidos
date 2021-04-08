import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;

public class AuthenticationClient {
    private final AuthenticationServiceGrpc.AuthenticationServiceBlockingStub blockingStub;

    public AuthenticationClient(Channel channel) {
        blockingStub = AuthenticationServiceGrpc.newBlockingStub(channel);
    }

    public void authenticate(String username, String password) {
        Authentication.AuthenticationRequest request = Authentication.AuthenticationRequest
                .newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        Authentication.AuthenticationResponse response;
        try {
            response = blockingStub.authenticate(request);
            System.out.println("Response status: " + response.getStatus());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        String username = "jose";
        String password = "Jose";
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [name [target]] \n");
                System.exit(1);
            }
            username = args[0];
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
            AuthenticationClient client = new AuthenticationClient(channel);
            client.authenticate(username, password);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
