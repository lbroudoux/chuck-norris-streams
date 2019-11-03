INSERT INTO movie (main_actor,title,`year`) VALUES (
'Jessica Lange','King Kong',1976);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Chuck Norris','Delta Force',1986);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Anthony Hopkins','The Elephant Man',1980);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Chuck Norris','Invasion U.S.A.',1985);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Chuck Norris','An Eye for an Eye',1981);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Roger Miller','Robin Hood',1973);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Charles Chaplin','Charly Chaplin in Wien',1931);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'Chuck Norris','The Octagon',1980);
INSERT INTO movie (main_actor,title,`year`) VALUES (
'James Stewart','Vertigo',1958);


INSERT INTO customer
( first_name, last_name, twitter_handle)
VALUES( 'alain', 'pham', '@koint');
INSERT INTO customer
( first_name, last_name, twitter_handle)
VALUES( 'laurent', 'broudoux', '@lbroudoux');


update movie set main_actor='Jessica Lange ' where id =1;
update movie set main_actor='Chuck Norris ' where id =2;
update movie set main_actor='Anthony Hopkins ' where id =3;
update movie set main_actor='Chuck Norris ' where id =4;
update movie set main_actor='Chuck Norris ' where id =5;
update movie set main_actor='Roger Miller ' where id =6;
update movie set main_actor='Charles Chaplin ' where id =7;
update movie set main_actor='Chuck Norris ' where id =8;
update movie set main_actor='James Stewart ' where id =9;

update customer set last_name='pham ' where id =1;
update customer set last_name='broudoux ' where id =2;

update movie set main_actor='Jessica Lange' where id =1;
update movie set main_actor='Chuck Norris' where id =2;
update movie set main_actor='Anthony Hopkins' where id =3;
update movie set main_actor='Chuck Norris' where id =4;
update movie set main_actor='Chuck Norris' where id =5;
update movie set main_actor='Roger Miller' where id =6;
update movie set main_actor='Charles Chaplin' where id =7;
update movie set main_actor='Chuck Norris' where id =8;
update movie set main_actor='James Stewart' where id =9;

update customer set last_name='pham' where id =1;
update customer set last_name='broudoux' where id =2;

commit;
