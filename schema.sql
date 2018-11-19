--
--Setup Table Schema and Constraints
--

DROP TABLE BelongsTo;
DROP TABLE Category;
DROP TABLE Bidlog;
DROP TABLE Product;
DROP TABLE Administrator;
DROP TABLE Customer;
DROP TABLE ourSysDATE;
commit;

CREATE TABLE ourSysDATE(
	c_date DATE NOT NULL,
	CONSTRAINT PK_ourSysDATE PRIMARY KEY(c_date) INITIALLY IMMEDIATE DEFERRABLE
);

CREATE TABLE Customer(
	login varchar2(10),
	password varchar2(10) NOT NULL,
	name varchar2(20) NOT NULL,
	address varchar2(30) NOT NULL,
	email varchar2(20) NOT NULL,
	CONSTRAINT PK_Customer PRIMARY KEY(login) INITIALLY IMMEDIATE DEFERRABLE
);  

CREATE TABLE Administrator(
	login varchar2(10),
	password varchar2(10) NOT NULL,
	name varchar2(20) NOT NULL,
	address varchar2(30) NOT NULL,
	email varchar2(20) NOT NULL,
	CONSTRAINT PK_Administrator PRIMARY KEY(login) INITIALLY IMMEDIATE DEFERRABLE
);
INSERT INTO Administrator VALUES('admin', 'root', 'Admin', 'default', 'admin@aol.com');

CREATE TABLE Product(
	auction_id int,
	name varchar2(20) NOT NULL,
	description varchar2(30),
	seller varchar2(10),
	start_date DATE,
	min_price int DEFAULT 0,
	number_of_days int,
	status varchar2(15) NOT NULL,
	buyer varchar2(10),
	sell_date DATE,
	amount int,
	CONSTRAINT PK_Product PRIMARY KEY(auction_id) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_Product_Seller FOREIGN KEY(seller) REFERENCES Customer(login) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_Product_Buyer FOREIGN KEY(buyer) REFERENCES Customer(login) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT Product_Status_Range CHECK(status in ('under auction', 'sold', 'withdrawn', 'closed')) INITIALLY IMMEDIATE DEFERRABLE
);

CREATE TABLE Bidlog(
	bidsn int,
	auction_id int,
	bidder varchar2(10),
	bid_time DATE NOT NULL,
	amount int NOT NULL,
	CONSTRAINT PK_Bidlog PRIMARY KEY(bidsn) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_Bidlog_Auction FOREIGN KEY(auction_id) REFERENCES Product(auction_id) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_Bidlog_Bidder FOREIGN KEY(bidder) REFERENCES Customer(login) INITIALLY IMMEDIATE DEFERRABLE
);

CREATE TABLE Category(
	name varchar2(20),
	parent_category varchar2(20),
	CONSTRAINT PK_Category PRIMARY KEY(name) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_Category_Sub FOREIGN KEY(parent_category) REFERENCES Category(name) INITIALLY IMMEDIATE DEFERRABLE
);

CREATE TABLE BelongsTo(
	auction_id int,
	category varchar2(20),
	CONSTRAINT PK_BelongsTo PRIMARY KEY(auction_id, category) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_BelongsTo_Auction FOREIGN KEY(auction_id) REFERENCES Product(auction_id) INITIALLY IMMEDIATE DEFERRABLE,
	CONSTRAINT FK_BelongsTo_Category FOREIGN KEY(category) REFERENCES Category(name) INITIALLY IMMEDIATE DEFERRABLE
);
commit;