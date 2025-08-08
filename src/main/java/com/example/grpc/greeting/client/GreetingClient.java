package com.example.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    ManagedChannel channel;
    private void run() throws InterruptedException {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        //unary call
//        doUnaryCall(channel);

        //server streaming
//        doServerStreamingCall(channel);

        //client streaming
//        doClientStreamingCall(channel);

        //Bi-di streaming
//        doBiDiStreamingCall(channel);

        //Deadline example
        doUnaryCallWithDeadline(channel);

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void doUnaryCallWithDeadline(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);

        //call withh 3000ms deadline
        try {
            System.out.println("Sending request with 3000 ms deadline");
            GreetWithDeadlineResponse response = blockingStub.withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder().setFirstName("Ajay").getDefaultInstanceForType()).build());
            System.out.println(response.getResult());
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("System deadline exceeded");
            } else {
                ex.printStackTrace();
            }
        }


        //call withh 100ms deadline
        try {
            System.out.println("Sending request with 100 ms deadline");
            GreetWithDeadlineResponse response = blockingStub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder().setFirstName("Avanti").getDefaultInstanceForType()).build());
            System.out.println(response.getResult());
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("System deadline exceeded");
            } else {
                ex.printStackTrace();
            }
        }
    }

    private void doClientStreamingCall(ManagedChannel channel) throws InterruptedException {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                //we get a response from server
                System.out.println("Received response from server");
                System.out.println(longGreetResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                //we get error from server
            }

            @Override
            public void onCompleted() {
                //server is done sending data
                System.out.println("Server has completed sending data");
                //onCompleted will be called right after onNext()
                latch.countDown();
            }
        });

        List<String> firstNames = Arrays.asList("Ajay", "Avanti", "Abhidnya");

        for (String name: firstNames) {
            //send data to server
            requestObserver.onNext(LongGreetRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder().setFirstName(name)).build());
        }
        //tell server that client is done sending data
        requestObserver.onCompleted();

        latch.await(3, TimeUnit.SECONDS);

    }

    private void doUnaryCall(ManagedChannel channel) {
        //create client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Ajay")
                .setLastName("Murtekar")
                .build();

        GreetRequest request = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //RPC call
        GreetResponse response = greetClient.greet(request);

        System.out.println(response.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        //create client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Ajay"))
                .build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse ->
                        System.out.println(greetManyTimesResponse.getResult()));
    }

    private  void doBiDiStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse greetEveryoneResponse) {
                System.out.println("received response from server: " + greetEveryoneResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList("Ajay", "Avanti", "Abhidnya")
                .forEach(name -> {
                    System.out.println("Sending Name: "+ name);
                    requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder().setFirstName(name)).build());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, I am gRPC Client");

        GreetingClient client = new GreetingClient();
        client.run();
    }
}
