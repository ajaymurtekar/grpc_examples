package com.example.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

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
        doClientStreamingCall(channel);

        channel.shutdown();

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
