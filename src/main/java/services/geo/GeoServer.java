package services.geo;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GeoServer {
    private static final Logger logger = Logger.getLogger(GeoServer.class.getName());
    private static final int PORT = 51052; //TODO should server port be a static final attribute?
    private Server server;

    public static int getPORT() {
        return PORT;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new GeoService())
                .build()
                .start();

        logger.info("Server started on port: " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                GeoServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        final GeoServer server = new GeoServer();
        server.start();
        server.blockUntilShutdown();
    }

}
