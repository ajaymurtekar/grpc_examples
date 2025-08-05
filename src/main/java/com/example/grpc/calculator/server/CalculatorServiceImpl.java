package com.example.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        Integer firstNumber = request.getFirstNumber();
        Integer secondNumber = request.getSecondNumber();

        System.out.println("First Number: "+ firstNumber + ", Second Number: "+ secondNumber);

        Integer sum = firstNumber + secondNumber;

        SumResponse response = SumResponse.newBuilder().setResult(sum).build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        System.out.println("Received Number: "+ number);
        Integer divisor = 2;

        while(number > 1) {
            if (number % divisor == 0) {
                number = number / divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());
            } else {
                divisor ++;
            }
        }
        responseObserver.onCompleted();
    }
}
