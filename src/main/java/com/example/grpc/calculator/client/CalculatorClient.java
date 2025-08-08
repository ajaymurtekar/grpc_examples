package com.example.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient();
        client.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        //doClientStreamingCall(channel);
        //doBiDiStreamingCall(channel);
        doErrorCall(channel);

        channel.shutdown();

    }

    private void doErrorCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(channel);
        try {
            SquareRootResponse response = syncClient.squareRoot(SquareRootRequest.newBuilder().setNumber(-1).build());
            System.out.println("Square root value is: "+ response.getNumberRoot());
        } catch (StatusRuntimeException e) {
            System.out.println("Exception while calculating square root!!!");
            e.printStackTrace();
        }

    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse findMaximumResponse) {
                System.out.println("Got new max from server: "+ findMaximumResponse.getMaximum());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending messages.");
                latch.countDown();
            }
        });

        Arrays.asList(3,5,17,8,9,12,56).forEach(
                number -> {
                    System.out.println("Sending number from client: "+ number);
                    requestObserver.onNext(FindMaximumRequest.newBuilder().setNumber(number).build());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub calculatorService = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ComputeAverageRequest> requestObserver = calculatorService.computeAverage(new StreamObserver<ComputeAverageResponse>() {

            @Override
            public void onNext(ComputeAverageResponse computeAverageResponse) {
                System.out.println("Received response from server"+computeAverageResponse.getAverage());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server completed sending data");
                latch.countDown();
            }
        });
        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(1).build());
        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(2).build());
        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(3).build());
        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(4).build());

        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService = CalculatorServiceGrpc.newBlockingStub(channel);
        calculatorService.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder().setNumber(567890)
                        .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });

    }

    private void doUnaryCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService = CalculatorServiceGrpc.newBlockingStub(channel);
        SumRequest request = SumRequest.newBuilder().setFirstNumber(5).setSecondNumber(7).build();
        SumResponse response = calculatorService.sum(request);
        System.out.println("Addition result: "+ response.getResult());
    }
}
