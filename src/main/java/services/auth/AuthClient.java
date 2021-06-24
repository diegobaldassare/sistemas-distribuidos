package services.auth;

import auth.AuthServiceGrpc.*;
import auth.Auth.*;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static auth.AuthServiceGrpc.newBlockingStub;

public class AuthClient {
    private static final Logger logger = Logger.getLogger(AuthClient.class.getName());
    private final List<AuthServiceBlockingStub> stubs; // TODO Should we use blocking stubs?

    public AuthClient(Channel[] channels) {
        stubs = new ArrayList<>();
        for (Channel c : channels) {
            stubs.add(newBlockingStub(c));
        }
    }

    public void authenticateUser(String username, String password) {
        AuthenticateUserRequest request = AuthenticateUserRequest
                .newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        AuthenticateUserResponse response;
        try {
            response = randomStub().authenticateUser(request);
            logger.info("Response status: " + response.getStatus());
        } catch (StatusRuntimeException e) {
            e.printStackTrace(); // TODO Fail-over strategy is implemented here?
        }
    }

    private AuthServiceBlockingStub randomStub() {
        return stubs.get((int) Math.round(Math.random() * (stubs.size() - 1)));
    }

    public static void main(String[] args) throws Exception {
        String ip = InetAddress.getLocalHost().getHostAddress();
        ManagedChannel[] channels = new ManagedChannel[]{createChannel(ip, AuthServer.getPORT())};
        AuthClient client = new AuthClient(channels);
        try {
            client.authenticateUser(
                    "mati",
                    "mati"
            );
            client.authenticateUser(
                    "franco",
                    "Franco"
            );
            client.authenticateUser(
                    "dieguito",
                    "diego"
            );
            client.authenticateUser(
                    "Jose",
                    "Jose"
            );
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
