package com.example.demo;

import io.sentry.spring.tracing.SentrySpan;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

@SentrySpan
interface MovieRepository extends CrudRepository<Movie, Long> {
}
