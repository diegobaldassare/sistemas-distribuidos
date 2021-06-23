package services.products;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ProductServer {
    private static final Logger logger = Logger.getLogger(ProductServer.class.getName());
    private static final int PORT = 51052; //TODO should server port be a static final attribute?
    private Server server;

    public static int getPORT() {
        return PORT;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new ProductService())
                .build()
                .start();

        logger.info("Server started on ip: " + InetAddress.getLocalHost().getHostAddress());
        logger.info("Server started on port: " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                ProductServer.this.stop();
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
        final ProductServer server = new ProductServer();
        server.start();
        server.blockUntilShutdown();
    }

}
