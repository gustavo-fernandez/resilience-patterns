package com.example.resiliencepatterns;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

  private final CustomerService customerService;

  @CircuitBreaker(name = "customer-cb-01")
  @GetMapping("api/customer")
  public Mono<Customer> findCustomer() {
    return customerService.findCustomer()
      .onErrorResume(e -> Mono.just(new Customer(-1L, e.getClass().getCanonicalName())));
  }

  @CircuitBreaker(name = "employee-cb-01")
  @GetMapping("api/employee")
  public Mono<Customer> findEmployee() {
    return Mono.just(new Customer(9L, "Employee"));
  }

  @TimeLimiter(name = "time-limiter-01")
  @GetMapping("api/time-limiter-test")
  public Mono<Customer> timeLimiterTest() {
    return Mono.just(new Customer(9L, "Employee"))
      .delayElement(Duration.ofSeconds(4));
  }

  @Retry(name = "retry-01")
  @GetMapping("api/retry-test")
  public Mono<Customer> retryTest() {
    return Mono.<Customer>error(new RuntimeException("Error en \"retry\""))
      .doOnSubscribe(x -> log.info("Reintentando"));
  }

  @RateLimiter(name = "rate-limiter-01")
  @GetMapping("api/rate-limiter")
  public Mono<Customer> rateLimiterTest() {
    return Mono.just(new Customer(9L, "Employee"))
      .delayElement(Duration.ofSeconds(2));
  }

  @Bulkhead(name = "bulkhead-01")
  @GetMapping("api/bulkhead-01")
  public Mono<Customer> bulkHead01() {
    return Mono.just(new Customer(9L, "Employee"))
      .delayElement(Duration.ofSeconds(2));
  }

}
