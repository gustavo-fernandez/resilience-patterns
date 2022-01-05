package com.example.resiliencepatterns;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  // @CircuitBreaker(name = "customer-cb-01")
  public Mono<Customer> findCustomer() {
    var customerCb01 = circuitBreakerRegistry.circuitBreaker("customer-cb-01");
    return Mono.just(new Customer(1L, "Hector"))
      .doOnSubscribe(x -> log.info("Intentando obtener customer"))
      .flatMap(i -> Mono.<Customer>error(new RuntimeException("Fallo en el service")))
      .transformDeferred(CircuitBreakerOperator.of(customerCb01));
  }

}
