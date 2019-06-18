package com.github.lbroudoux.chucknorris.filter;

import com.github.lbroudoux.chucknorris.filter.model.*;
import com.github.lbroudoux.chucknorris.filter.serdes.SerdeFactory;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class FilterTest {


   private TopologyTestDriver testDriver;
   private KeyValueStore<String, Long> store;

   @Before
   public void setup() {

      // Configure all the Serdes.
      final Serde<DefaultId> defaultIdSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(DefaultId.class, true);
      final Serde<Movie> movieSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Movie.class, false);
      final Serde<Customer> userSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Customer.class, false);
      final Serde<Rental> rentalSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(Rental.class, false);
      final Serde<CustomerRentalMovieAggregate> aggregateSerde = SerdeFactory.createDbzEventJsonPojoSerdeFor(CustomerRentalMovieAggregate.class, false);


      // Getting a streams builder.
      StreamsBuilder builder = new StreamsBuilder();

      // Create Tables for reference data (we only want latest status).
      KTable<DefaultId, Customer> usersTable =
            builder.table("customers", Consumed.with(defaultIdSerde, userSerde));
      KTable<DefaultId, Movie> moviesTable =
            builder.table("movies", Consumed.with(defaultIdSerde, movieSerde));

      // Deal with Integer as key so re-aggregate the tables.
      KTable<Integer, Customer> usersTableInt = usersTable.groupBy((key, customer) -> KeyValue.pair(customer.getId(), customer))
            .aggregate(
                  () -> null,  // Initiate the aggregate value
                  (userId, customer, aggValue) -> customer,   // adder (doing nothing, just passing the user through as the value)
                  (userId, customer, aggValue) -> customer    // subtractor (doing nothing, just passing the user through as the value)
            );
      KTable<Integer, Movie> moviesTableInt = moviesTable.groupBy((key, movie) -> KeyValue.pair(movie.getId(), movie))
            .aggregate(
                  () -> null,  // Initiate the aggregate value
                  (movieId, movie, aggValue) -> movie,   // adder (doing nothing, just passing the movie through as the value)
                  (movieId, movie, aggValue) -> movie    // subtractor (doing nothing, just passing the movie through as the value)
            );

      // Create Stream for rental: we are looking at each changes.
      KStream<DefaultId, Rental> rentalsStream = builder.stream("rentals", Consumed.with(defaultIdSerde, rentalSerde));



      KStream<Integer, CustomerRentalMovieAggregate> chuckNorrisRentalsStream =
            rentalsStream
                  .map((rentalId, rental) -> KeyValue.pair(rental.getMovieId(), rental))
                  .leftJoin(moviesTableInt, (rental, movie) ->
                        rental.getEventType() == EventType.DELETE ?
                              null : new CustomerRentalMovieAggregate(rental, (Movie) movie));

      // Build the topology.
      Topology topology = builder.build();


      // setup test driver
      Properties props = new Properties();
      props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "test");
      props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
      props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
      props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
      testDriver = new TopologyTestDriver(topology, props);

      // Pre-populate store.
      store = testDriver.getKeyValueStore("customers");


      store.put("a", 21L);
   }

   @After
   public void tearDown() {
      testDriver.close();
   }


}
