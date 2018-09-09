package com.example;

import com.example.protobuf.ErrorResponse;
import com.example.protobuf.Message;
import com.example.protobuf.Messages;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.is;

public class MessageHandlerTest {
    private WebTestClient testClient;

    @Before
    public void setUp() throws Exception {
        this.testClient = WebTestClient.bindToRouterFunction(new MessageHandler().routes()).build();
    }

    @Test
    public void testMessage() throws Exception {
        this.testClient.post()
                .uri("/messages") //
                .syncBody(Message.newBuilder().setText("Hello").build())
                .exchange() //
                .expectStatus().isOk() //
                .expectBody(Message.class)
                .isEqualTo(Message.newBuilder().setText("Hello").build());

        this.testClient.get()
                .uri("/messages") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody(Messages.class)
                .value(Messages::getValueCount, is(1))
                .value(x -> x.getValue(0), is(Message.newBuilder().setText("Hello").build()));
    }


    @Test
    public void testInvalidMessage() throws Exception {
        this.testClient.post()
                .uri("/messages") //
                .syncBody(Message.newBuilder().setText("HelloHello").build())
                .exchange() //
                .expectStatus().isBadRequest() //
                .expectBody(ErrorResponse.class)
                .value(ErrorResponse::getDetailsCount, is(1))
                .value(r -> r.getDetails(0).getDefaultMessage(),
                        is("The size of \"text\" must be less than or equal to 8. The given size is 10"));
    }
}
