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

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        return new StreamObserver<LongGreetRequest>() {
            String result = "";
            @Override
            public void onNext(LongGreetRequest longGreetRequest) {
                //when client sends a message
                result += "Hello " + longGreetRequest.getGreeting().getFirstName() + "! ";
            }

            @Override
            public void onError(Throwable throwable) {
                //client sends an error
            }

            @Override
            public void onCompleted() {
                //client is done streaming
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                //this is when we want to return rerquestObserver back to client
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest greetEveryoneRequest) {
                String result  = "Hello " + greetEveryoneRequest.getGreeting().getFirstName();
                GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder().setResult(result).build();
                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                //do nothing
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
