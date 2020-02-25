package com.github.lbroudoux.chucknorris.filter.model;

import com.fasterxml.jackson.annotation.*;

import io.quarkus.runtime.annotations.RegisterForReflection;
/**
 * @author laurent
 */
@RegisterForReflection
public class Movie {

    @JsonProperty("_eventType")
    private final EventType eventType;

    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("title")
    private final String title;
    @JsonProperty("year")
    private final Integer year;
    @JsonProperty("main_actor")
    private final String mainActor;

    @JsonCreator
    public Movie(@JsonProperty("_eventType") EventType _eventType,
            @JsonProperty("id") Integer id,
            @JsonProperty("title") String title,
            @JsonProperty("year") Integer year,
            @JsonProperty("main_actor") String mainActor) {

        this.eventType = _eventType == null ? EventType.UPSERT : _eventType;
        this.id = id;
        this.title = title;
        this.year = year;
        this.mainActor = mainActor;
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @return the mainActor
     */
    public String getMainActor() {
        return mainActor;
    }

    @Override
    public String toString() {
        return "Movie{" +
              "_eventType='" + eventType + '\'' +
              ", id=" + id +
              ", title='" + title + '\'' +
              ", main_actor='" + mainActor + '\'' +
              ", year='" + year + '\'' +
              "}";
    }
}