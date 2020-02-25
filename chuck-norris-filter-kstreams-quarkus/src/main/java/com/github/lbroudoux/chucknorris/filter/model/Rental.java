package com.github.lbroudoux.chucknorris.filter.model;

import com.fasterxml.jackson.annotation.*;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Date;
/**
 * @author laurent
 */
@RegisterForReflection
public class Rental {

    @JsonProperty("_eventType")
    private final EventType eventType;

    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("customer_id")
    private final Integer customerId;
    @JsonProperty("movie_id")
    private final Integer movieId;
    @JsonProperty("start_date")
    private final Date startDate;
    @JsonProperty("rental_duration")
    private final Integer rentalDuration;

    @JsonCreator
    public Rental(@JsonProperty("_eventType") EventType _eventType,
            @JsonProperty("id") Integer id,
            @JsonProperty("customer_id") Integer customerId,
            @JsonProperty("movie_id") Integer movieId,
            @JsonProperty("start_date") Date startDate,
            @JsonProperty("rental_duration") Integer rentalDuration) {

        this.eventType = _eventType == null ? EventType.UPSERT : _eventType;
        this.id = id;
        this.customerId = customerId;
        this.movieId = movieId;
        this.startDate = startDate;
        this.rentalDuration = rentalDuration;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the customerId
     */
    public Integer getCustomerId() {
        return customerId;
    }

    /**
     * @return the movieId
     */
    public Integer getMovieId() {
        return movieId;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @return the rentalDuration
     */
    public Integer getRentalDuration() {
        return rentalDuration;
    }


    @Override
    public String toString() {
        return "Rental{" +
              "_eventType='" + eventType + '\'' +
              ", id=" + id +
              ", customer_id='" + customerId + '\'' +
              ", movie_id='" + movieId + '\'' +
              ", startDate='" + startDate + '\'' +
              "}";
    }
}