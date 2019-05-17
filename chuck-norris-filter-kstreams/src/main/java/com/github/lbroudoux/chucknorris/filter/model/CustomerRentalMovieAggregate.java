package com.github.lbroudoux.chucknorris.filter.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author laurent
 */
public class CustomerRentalMovieAggregate {

   @JsonProperty("movie")
   private final Movie movie;

   @JsonProperty("customer")
   private Customer customer;

   @JsonProperty("rental")
   private final Rental rental;


   @JsonCreator
   public CustomerRentalMovieAggregate(
         @JsonProperty("rental") Rental rental,
         @JsonProperty("movie") Movie movie) {
      this.rental = rental;
      this.movie = movie;
      if (movie != null && rental != null) {
         System.err.println("Building new CustomerRentalMovieAggregate with " + movie.toString() + " and " + rental.toString());
      }
      if (movie == null) {
         System.err.println("Got null movie...");
      }
      if (rental == null) {
         System.err.println("Got null rental...");
      }
   }

   public Movie getMovie() {
      return movie;
   }

   public Customer getCustomer() {
      return customer;
   }

   public void setCustomer(Customer customer) {
      this.customer = customer;
   }

   public Rental getRental() {
      return rental;
   }

   @Override
   public String toString() {
      return "CustomerRentalMovieAggregate{" +
            "rental=" + rental +
            ", movie=" + movie +
            ", customer=" + customer +
            "}";
   }
}
