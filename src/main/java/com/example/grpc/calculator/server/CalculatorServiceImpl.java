package com.example.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
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

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        return new StreamObserver<ComputeAverageRequest>() {
            int sum = 0;
            int count = 0;

            @Override
            public void onNext(ComputeAverageRequest computeAverageRequest) {
                sum = sum + computeAverageRequest.getNumber();
                count++;

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                double average = (double) sum /count;
                responseObserver.onNext(ComputeAverageResponse.newBuilder().setAverage(average).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<FindMaximumRequest> findMaximum(StreamObserver<FindMaximumResponse> responseObserver) {
        return new StreamObserver<FindMaximumRequest>() {
            int currentMax = 0;

            @Override
            public void onNext(FindMaximumRequest findMaximumRequest) {
                if (findMaximumRequest.getNumber() > currentMax) {
                    currentMax = findMaximumRequest.getNumber();
                    responseObserver.onNext(FindMaximumResponse.newBuilder().setMaximum(currentMax).build());
                }

            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(FindMaximumResponse.newBuilder().setMaximum(currentMax).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        int number = request.getNumber();
        if (number >= 0) {
            double root_number = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder().setNumberRoot(root_number).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Input number is not positive")
                            .augmentDescription("Number Sent: "+ number)
                    .asRuntimeException());
        }
    }
}
