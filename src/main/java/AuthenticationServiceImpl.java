import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AuthenticationServiceImpl extends AuthenticationServiceGrpc.AuthenticationServiceImplBase {
    private final Map<String, String> users;

    AuthenticationServiceImpl() {
        Map<String, String> aux = new HashMap<>();
        Stream.of("Franco", "Mati", "Diego", "Jose").forEach(name -> {
            aux.put(name.toLowerCase(), name);
        });
        users = aux;
    }

    @Override
    public void authenticate(
            Authentication.AuthenticationRequest request,
            StreamObserver<Authentication.AuthenticationResponse> responseObserver
    ) {
        String username = request.getUsername();
        String password = request.getPassword();

        Authentication.AuthenticationResponse response = Authentication.AuthenticationResponse.newBuilder().setStatus("FAILED").build();

        if (users.containsKey(username)) {
            if (users.get(username).equals(password)) {
                response = Authentication.AuthenticationResponse.newBuilder().setStatus("OK").build();
            }
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
