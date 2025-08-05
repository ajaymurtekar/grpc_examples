package com.example.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    System.out.println("Shutting down server...");
                    server.shutdown();
                    System.out.println("Server shutdown successful");
                }
        ));

        server.awaitTermination();
    }
}
