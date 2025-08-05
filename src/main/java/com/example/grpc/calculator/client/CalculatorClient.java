package com.example.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService = CalculatorServiceGrpc.newBlockingStub(channel);

        //Unary operation
//        SumRequest request = SumRequest.newBuilder().setFirstNumber(5).setSecondNumber(7).build();
//
//        SumResponse response = calculatorService.sum(request);
//
//        System.out.println("Addition result: "+ response.getResult());

        //Server Streaming
        calculatorService.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder().setNumber(567890)
                        .build())
                        .forEachRemaining(primeNumberDecompositionResponse -> {
                            System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                        });



        channel.shutdown();
    }
}
