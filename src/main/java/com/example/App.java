package com.example;

import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.http.server.HttpServer;

import java.time.Duration;
import java.util.Optional;

public class App {

    static RouterFunction<ServerResponse> routes() {
        return new MessageHandler().routes();
    }

    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();
        int port = Optional.ofNullable(System.getenv("PORT")) //
                .map(Integer::parseInt) //
                .orElse(8080);
        HttpServer httpServer = HttpServer.create().host("0.0.0.0").port(port);
        httpServer.route(routes -> {
            HttpHandler httpHandler = RouterFunctions.toHttpHandler(
                    App.routes(), HandlerStrategies.builder().build());
            routes.route(x -> true, new ReactorHttpHandlerAdapter(httpHandler));
        }).bindUntilJavaShutdown(Duration.ofSeconds(3), disposableServer -> {
            long elapsed = System.currentTimeMillis() - begin;
            LoggerFactory.getLogger(App.class).info("Started in {} seconds",
                    elapsed / 1000.0);
        });
    }
}
