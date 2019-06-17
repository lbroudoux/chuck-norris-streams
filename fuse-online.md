
# Connectors setup

## Salesforce

As described on [Connecting to Salesforce](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/connecting_fuse_online_to_applications_and_services/index#connecting-to-sf_connectors), create a new Connector to SalesForce into your Fuse Online/Syndesis  instance.

## Telegram

As described on [Connecting to Telegram](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/connecting_fuse_online_to_applications_and_services/index#connecting-to-telegram_connectors), create a new Connector to Telegram into your Fuse Online/Syndesis instance.

# Schema

```
{
  "id" : 27,
  "rental_duration" : 7,
  "start_date" : 1558343540000,
  "customer_id" : 1,
  "movie_id" : 2,
  "movie" : {
    "id" : 2,
    "main_actor" : "Chuck Norris",
    "title" : "Delta Force",
    "year" : 1986
  },
  "customer" : {
    "id" : 1,
    "first_name" : "alain",
    "last_name" : "pham",
    "twitter_handle" : "@koint"
  }
}
```

# Chat ID

```
157105479
```
