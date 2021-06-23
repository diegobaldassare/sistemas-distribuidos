package services.products;

import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import products.ProductServiceGrpc;
import products.Products.*;
import services.products.util.Product;

import java.util.*;

public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final List<Product> products = new ArrayList<>();
    private final Map<GetProductRequest, GetProductResponse> cache = new HashMap<>();

    public ProductService() {
        this.products.add(new Product("0", "Computadora", "234000"));
        this.products.add(new Product("1", "Mesa", "10000"));
        this.products.add(new Product("2", "Celular", "60111"));
        this.products.add(new Product("3", "Heladera", "30500"));
        this.products.add(new Product("4", "Reloj", "5800"));
        this.products.add(new Product("5", "Lapicera", "80"));
        this.products.add(new Product("6", "Remera", "2700"));
        this.products.add(new Product("7", "Zapatillas", "4200"));
        this.products.add(new Product("8", "Goma", "51"));
        this.products.add(new Product("9", "Mochila", "999"));
        this.products.add(new Product("10", "Silla", "1200"));
    }

    @Override
    public void listProducts(EmptyRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        Set<String> result = new HashSet<>();
        for (Product p : products) {
            String product = p.toString();
            result.add(product);
        }
        ListProductsResponse response = ListProductsResponse.newBuilder().addAllProducts(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(GetProductRequest request, StreamObserver<GetProductResponse> responseObserver) {
        if (cache.containsKey(request)) {
            GetProductResponse response = cache.get(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            Product product = products.get(Integer.getInteger(request.getProductId()));
            GetProductResponse response = GetProductResponse.newBuilder()
                    .setProductId(1, product.getId())
                    .setProductName(2, product.getName())
                    .setProductPrice(3, product.getPrice())
                    .build();
            cache.put(request, response);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
