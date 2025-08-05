package com.example.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String results = "Hello "+ greeting.getFirstName();
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(results)
                .build();

        //send response back using observer
        responseObserver.onNext(response);

        //complete RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        try {
            String firstName = request.getGreeting().getFirstName();
            for (int i = 0; i < 10; i++) {
                String result = "First Name: "+ firstName + ", Response count: "+ i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(response);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }
}
