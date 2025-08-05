package com.example.grpc.calculator.server;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        int firstNumber = request.getFirstNumber();
        int secondNumber = request.getSecondNumber();

        System.out.println("First Number: "+ firstNumber + ", Second Number: "+ secondNumber);

        int sum = firstNumber + secondNumber;

        SumResponse response = SumResponse.newBuilder().setResult(sum).build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
