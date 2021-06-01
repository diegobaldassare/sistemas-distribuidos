package services.auth;

import auth.Auth.*;
import auth.AuthServiceGrpc.*;
import io.grpc.stub.StreamObserver;
import services.auth.util.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AuthService extends AuthServiceImplBase {
    private final List<User> users;

    AuthService() {
        users = new ArrayList<>();
        Stream.of("Diego", "Franco", "Jose", "Matias").forEach(username -> users.add(new User(username, username)));
        // TODO Should hardcoded users be defined here?
    }

    @Override
    public void authenticateUser(AuthenticateUserRequest request, StreamObserver<AuthenticateUserResponse> responseObserver) {
        String username = request.getUsername();
        String password = request.getPassword();
        User userToAuthenticate = new User(username, password);

        AuthenticateUserResponse response = AuthenticateUserResponse.newBuilder().setStatus("FAILED").build();

        if (users.contains(userToAuthenticate)) {
            response = AuthenticateUserResponse.newBuilder().setStatus("OK").build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
