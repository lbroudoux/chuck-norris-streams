package com.github.lbroudoux.chucknorris.filter.model;

import com.fasterxml.jackson.annotation.*;
/**
 * @author laurent
 */
public class Customer {

    private final EventType eventType;

    private final Integer id;
    private final String firstname;
    private final String lastname;
    private final String twitterHandle;

    @JsonCreator
    public Customer(@JsonProperty("_eventType") EventType _eventType,
                    @JsonProperty("id") Integer id,
                    @JsonProperty("first_name") String firstname,
                    @JsonProperty("last_name") String lastname,
                    @JsonProperty("twitter_handle") String twitterHandle) {

        this.eventType = _eventType == null ? EventType.UPSERT : _eventType;
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.twitterHandle = twitterHandle;
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
     * @return the firstName
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @return the lastName
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @return the twitterHandle
     */
    public String getTwitterHandle() {
        return twitterHandle;
    }


    @Override
    public String toString() {
        return "Customer{" +
              "_eventType='" + eventType + '\'' +
              ", id=" + id +
              ", first_name='" + firstname + '\'' +
              ", last_name='" + lastname + '\'' +
              ", twitter_handle='" + twitterHandle + '\'' +
              "}";
    }
}