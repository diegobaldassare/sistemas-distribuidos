package services.products;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import products.ProductServiceGrpc.*;
import products.Products.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static products.ProductServiceGrpc.newBlockingStub;

public class ProductClient {
    private final List<ProductServiceBlockingStub> stubs;

    public ProductClient(ManagedChannel[] channels) {
        stubs = new ArrayList<>();
        for (ManagedChannel mc : channels) {
            stubs.add(newBlockingStub(mc));
        }
    }

    public void getProduct(List<String> ids) {
        System.out.println("\n***");
        for (String id : ids) {
            GetProductRequest request = GetProductRequest.newBuilder()
                    .setProductId(id)
                    .build();
            getProduct(request);
        }
    }

    private void getProduct(GetProductRequest request) {
        try {
            GetProductResponse response = randomStub().getProductById(request);
            System.out.println("Product id: " + response.getProductId(1));
            System.out.println("Product name: " + response.getProductName(2));
            System.out.println("Product price: " + response.getProductPrice(3));
            System.out.println("***");
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private void listProducts(EmptyRequest request) {
        try {
            ListProductsResponse response = randomStub().listProducts(request);
            String[] products = response.getProductsList().toArray(new String[0]);
            for (String product : products) {
                System.out.println("\t- " + product);
            }
            System.out.println("***");
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private ProductServiceBlockingStub randomStub() {
        return stubs.get((int) Math.round(Math.random() * (stubs.size() - 1)));
    }

    public static void main(String[] args) throws Exception {
        String serviceIP = InetAddress.getLocalHost().getHostAddress();
        List<String> testingIds = Arrays.asList(
                "1",
                "10"
        );
        ManagedChannel[] channels = new ManagedChannel[]{createChannel(serviceIP, ProductServer.getPORT())};
        ProductClient client = new ProductClient(channels);
        try {
            client.getProduct(testingIds);
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
