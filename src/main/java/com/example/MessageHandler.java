package com.example;

import am.ik.yavi.core.Validator;
import com.example.protobuf.Message;
import com.example.protobuf.Messages;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class MessageHandler {
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private static Validator<Message> validator = Validator.builder(Message.class)
            .constraint(Message::getText, "text", c -> c.notBlank().lessThanOrEqual(8))
            .build();

    public RouterFunction<ServerResponse> routes() {
        return route() //
                .GET("/messages", this::getMessages) //
                .POST("/messages", this::postMessage) //
                .build();
    }

    Mono<ServerResponse> getMessages(ServerRequest req) {
        return ok().syncBody(Messages.newBuilder().addAllValue(this.messages).build());
    }

    Mono<ServerResponse> postMessage(ServerRequest req) {
        return req.bodyToMono(Message.class)
                .flatMap(b -> validator.validateToEither(b)
                        .bimap(ErrorResponses::toError, this.messages::add)
                        .fold(v -> badRequest().syncBody(v), t -> ok().syncBody(b)));
    }

}
