package com.example.demo;

import java.util.Optional;

import io.sentry.IHub;
import io.sentry.ITransaction;
import io.sentry.SentryTracer;
import io.sentry.SpanStatus;
import io.sentry.TransactionContext;
import io.sentry.protocol.SentrySpan;
import io.sentry.protocol.SentryTransaction;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private MovieRepository movieRepository;

    @SpyBean
    private IHub hub;

    @Test
    void contextLoads() {
        ITransaction tx = new SentryTracer(new TransactionContext("tx", "op", true), hub);
        hub.configureScope(scope -> scope.setTransaction(tx));
        try {
            Optional<Movie> movie = movieRepository.findById(10L);
            assertThat(movie).isEmpty();
        }finally {
            tx.finish(SpanStatus.OK);
        }
        ArgumentCaptor<SentryTransaction> captor = ArgumentCaptor.forClass(SentryTransaction.class);
        verify(hub).captureTransaction(captor.capture(), any(), any(), any());
        SentryTransaction result = captor.getValue();

        assertThat(result.getSpans()).isNotEmpty();
        SentrySpan span = result.getSpans().get(0);
        assertThat(span.getOp()).isEqualTo("CrudRepository.findById");
    }

}
