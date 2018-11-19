--Start Our SYSDATE
CALL proc_startTime();

--Get start time
SELECT to_char(c_date, 'dd/mm/yy hh24:mi:ss') 
FROM ourSysDate;

--Populate Customers (admin account is created in schema.sql)
INSERT INTO Customer VALUES('dos42', 'pass1', 'Dylan', 'NULL', 'dos42@pitt.edu');
INSERT INTO Customer VALUES('jed121', 'pass2', 'Jen', 'NULL', 'jed121@pitt.edu');
INSERT INTO Customer VALUES('anw91', 'pass3', 'Alex', 'NULL', 'anw91@pitt.edu');

--Populate Category Tree
INSERT INTO Category VALUES('Collectibles/Art', NULL);
	INSERT INTO Category VALUES('Antiques', 'Collectibles/Art');
		INSERT INTO Category VALUES('Exotic', 'Antiques');
		INSERT INTO Category VALUES('Silver', 'Antiques');
		INSERT INTO Category VALUES('Maritime', 'Antiques');
		INSERT INTO Category VALUES('Period/Style', 'Antiques');
	INSERT INTO Category VALUES('Coins', 'Collectibles/Art');
		INSERT INTO Category VALUES('Commemorative Coins', 'Coins');
		INSERT INTO Category VALUES('Historical Coins', 'Coins');
	INSERT INTO Category VALUES('Art', 'Collectibles/Art');
		INSERT INTO Category VALUES('Painting', 'Art');
		INSERT INTO Category VALUES('Textile', 'Art');
		INSERT INTO Category VALUES('Photography', 'Art');
	INSERT INTO Category VALUES('Stamps', 'Collectibles/Art');
	INSERT INTO Category VALUES('Memorabilia', 'Collectibles/Art');
		INSERT INTO Category VALUES('Film/TV', 'Memorabilia');
		INSERT INTO Category VALUES('Sports', 'Memorabilia');
		INSERT INTO Category VALUES('Music', 'Memorabilia');
	INSERT INTO Category VALUES('Crafts', 'Collectibles/Art');
		INSERT INTO Category VALUES('Children', 'Crafts');
		INSERT INTO Category VALUES('Outdoor', 'Crafts');
 
--Add Products using proc_putProduct
CALL proc_putProduct('Barbarella Poster', NULL, 'Film/TV', 3, 'dos42', 1);
CALL proc_putProduct('Pitt Campus Collage', 'Made for school project', 'Photography', 5, 'jed121', 10);
	--Includes Multiple Categories
CALL proc_putProduct('1895 Silver Dollar', 'Mint-Condition', 'Historical Coins,Silver', 15, 'anw91', 75);	

--Bid on products with help of proc_placeBid and trig_updateHighBid
CALL proc_placeBid(80, '1895 Silver Dollar', 'jed121');
CALL proc_placeBid(2, 'Barbarella Poster', 'anw91');

--Test constraint violations and exceptions
	--Will fail due to PK constraint on user login (exception to catch must be in JAVA)
INSERT INTO Customer VALUES('dos42', 'pass1', 'Dylan', 'NULL', 'dos42@pitt.edu'); 
	--Will fail due to FK constraint on seller (exception to handle is in SQL)
CALL proc_putProduct('Cherub Fountain', 'Haunted', 'Outdoor', 10, 'prm73', 120);
	--Will fail due to FK constraint on auction_id (Handled by trig_updateHighBid)
CALL proc_placeBid(15, 'Bouncy Castle', 'jed121'); 
	--Bid won't complete since 12 < currHighBid = 80 (handled by proc_placeBid)
CALL proc_placeBid(12, '1895 Silver Dollar', 'dos42');

--Get time after placing bids (should be 20 seconds later using trig_bidTimeUpdate)
SELECT to_char(c_date, 'dd/mm/yy hh24:mi:ss') 
FROM ourSysDate;