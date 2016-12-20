DROP TABLE IF EXISTS customer;

CREATE TABLE customers (
  customerid text PRIMARY KEY,
  customername text,
  emailaddress text,
  deliveryaddress text,
  orderid text,
  createddatetime timestamp
) WITH comment='customer of the web shop'
  AND COMPACT STORAGE
  AND read_repair_chance = 1.0;
