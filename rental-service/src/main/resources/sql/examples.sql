INSERT INTO customer
(id, first_name, last_name, twitter_handle)
VALUES(nextval('customer_id_seq'::regclass), 'alain', 'pham', '@koint');

INSERT INTO movie
(id, main_actor, title, "year")
VALUES(nextval('movie_id_seq'::regclass), 'chuck', 'texas ranger', 0);

INSERT INTO rental
(id, rental_duration, start_date, customer_id, movie_id)
VALUES(nextval('rental_id_seq'::regclass), 7, '2019/05/10', 1, 1);

commit;