
# Connectors setup

## Salesforce

As described on [Connecting to Salesforce](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/connecting_fuse_online_to_applications_and_services/index#connecting-to-sf_connectors), create a new Connector to SalesForce into your Fuse Online/Syndesis  instance.

## Telegram

As described on [Connecting to Telegram](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/connecting_fuse_online_to_applications_and_services/index#connecting-to-telegram_connectors), create a new Connector to Telegram into your Fuse Online/Syndesis instance.

# Schema

```
{
  "rental": {
    "id": 1,
    "user_id": 1,
    "movie_id": 1,
    "start_date": "2019-05-16",
    "rental_duration": 3
  },
  "movie": {
    "id": 1,
    "title": "The Delta Force",
    "year": 1986,
    "main_actor": "Chuck Norris"
  },
  "customer": {
    "id": 1,
    "first_name": "Laurent",
    "last_name": "Broudoux",
    "twitter_handle": "@lbroudoux"
  }
}
```

# Chat ID

```
157105479
```
