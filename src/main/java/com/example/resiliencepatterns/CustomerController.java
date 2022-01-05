package com.example.resiliencepatterns;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

}
