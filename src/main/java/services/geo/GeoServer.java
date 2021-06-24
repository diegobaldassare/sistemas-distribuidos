package services.geo;

import io.etcd.jetcd.*;
import io.etcd.jetcd.election.LeaderResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import java.time.Duration;


public class GeoServer {
    private static final Logger logger = Logger.getLogger(GeoServer.class.getName());
    private static final int PORT = 51052; //TODO should server port be a static final attribute? podria ser 50000
    private Server server;

    long myLeaseTime = 10;
    String cacheURL = "memcached:11211";
    Duration cacheLeaseTime = Duration.ofMinutes(5);
    String etcdIp = "http://etcd:2379";

    Client client = Client.builder().endpoints(etcdIp).build();
    KV clientKV = client.getKVClient();
    Watch clientW = client.getWatchClient();
    Election ec = client.getElectionClient();
    Lease leaseClient = client.getLeaseClient();

    long leaseId = leaseClient.grant(myLeaseTime).get().getID(); //10

    InetAddress localhost = InetAddress.getLocalHost();
    String localIpAddress = localhost.getHostAddress();
    boolean leader = false;
    GeoService geoService = new GeoService(cacheURL, cacheLeaseTime);

    public GeoServer() throws ExecutionException, InterruptedException, UnknownHostException {
    }

    public static int getPORT() {
        return PORT;
    }

    public void start() throws IOException, InterruptedException {

        getDataFromETCD();

        server = ServerBuilder.forPort(PORT)
//                .addService(GeoServiceGrpc.bindService(geoService, ExecutionContext.global))
                .addService(geoService)
                .build()
                .start();

        logger.info("Server started on ip: " + InetAddress.getLocalHost().getHostAddress());
        logger.info("Server started on port: " + PORT);
        server.awaitTermination();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                GeoServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));

        registerInETCD();
        watch();
        while (true) {
            if (!leader) {
                ec.campaign(bytes("/election/geo/"), leaseId, bytes(localIpAddress));
            }
            Thread.sleep(6000);
        }
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
        final GeoServer server;
        try {
            server = new GeoServer();
            server.start();
            server.blockUntilShutdown();



        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public ByteSequence bytes(String str) {
        return ByteSequence.from(str.getBytes());
    }

    public void registerInETCD() {
        String id = "/services/geo/" + localIpAddress;
        leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
            @Override
            public void onNext(LeaseKeepAliveResponse value) {
//                System.out.println("LEASE: " + value);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });

        PutOption option = PutOption.newBuilder().withLeaseId(leaseId).build();
        clientKV.put(bytes(id), bytes(localIpAddress), option);
        ec.observe(bytes("/election/geo/"), new Election.Listener() {
            @Override
            public void onNext(LeaderResponse response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void getDataFromETCD() {
        GetResponse ttl = clientKV.get(bytes("config/services/geo/cache/ttl")).join();
        GetResponse url = clientKV.get(bytes("config/services/geo/cache/url")).join();

        cacheLeaseTime = Duration.ofMinutes(ttl.getKvs().get(0).getLease());
        cacheURL = url.getKvs().toString();
        System.out.println("CACHE URL: " + cacheURL);
        System.out.println("CACHE LEASE TIME: " + cacheLeaseTime);
    }

    public void watch() {
        ec.observe(bytes("/election/geo/"), new Election.Listener() {
            @Override
            public void onNext(LeaderResponse leaderResponse) {
                String leaderIp = leaderResponse.getKv().getValue().getBytes().toString();
                leader = leaderIp.equals(localIpAddress);
                geoService.setLeader(leader, leaderIp);
                System.out.println("I am: "+ localIpAddress + " and i am leader: "+ leader);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("ERROR ELECTION");
            }

            @Override
            public void onCompleted() {
                System.out.println("ON COMPLETED ELECTION");
            }
        });
    }
}
