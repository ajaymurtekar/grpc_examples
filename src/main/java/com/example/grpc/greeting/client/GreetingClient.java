package com.example.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello, I am gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");
        //dummy code
        //DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        //create client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //Unary
//        Greeting greeting = Greeting.newBuilder()
//                .setFirstName("Ajay")
//                .setLastName("Murtekar")
//                .build();
//
//        GreetRequest request = GreetRequest.newBuilder()
//                .setGreeting(greeting)
//                .build();
//
//        //RPC call
//        GreetResponse response = greetClient.greet(request);

//        System.out.println(response.getResult());

        //Server Streaming
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Ajay"))
                .build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                        .forEachRemaining(greetManyTimesResponse ->
                                System.out.println(greetManyTimesResponse.getResult()));

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
