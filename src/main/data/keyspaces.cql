CREATE KEYSPACE webshop WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor': 3};
CREATE KEYSPACE webshoptest WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor': 3};