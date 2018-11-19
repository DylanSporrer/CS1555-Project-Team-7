--
--Setup Triggers
--

--Updates High Bid Only If New Bid is Highest
CREATE OR REPLACE TRIGGER trig_updateHighBid
	BEFORE INSERT ON Bidlog
FOR EACH ROW
DECLARE
	currHighBid int;
	dummy DATE;
BEGIN
	SELECT amount INTO currHighBid
	FROM Product
	WHERE auction_id = :new.auction_id;
	
	--If bid is not higher than currHighBid, it is not updated
	IF(currHighBid >= :new.amount) THEN
		SELECT c_date INTO dummy FROM ourSysDATE;
	ELSE
		UPDATE Product
		SET amount = :new.amount
		WHERE auction_id = :new.auction_id;
	END IF;
END;
/

--Closes any auction which has passed its end time
CREATE OR REPLACE TRIGGER trig_closeAuctions
	AFTER UPDATE ON ourSysDATE
FOR EACH ROW
BEGIN
	UPDATE Product
	SET status = 'sold'
	WHERE auction_id IN(
		SELECT auction_id
		FROM Product
		WHERE status = 'under auction'
		AND sell_date <= :new.c_date
	);
END;
/

--Updates internal sysdate after each bid
CREATE OR REPLACE TRIGGER trig_bidTimeUpdate
	AFTER INSERT ON Bidlog
FOR EACH ROW
BEGIN
	UPDATE ourSysDATE
	SET c_date = c_date+(5/24/60/60);
END;
/
commit;
--
--Setup stored procedures and functions
--

--Function to find amount user spent in past X months
CREATE OR REPLACE FUNCTION func_buyingAmount(u IN varchar2, x IN int)
RETURN int IS 
	sumBids int;
	startWindow DATE;
	currDate DATE;
BEGIN
	SELECT c_date INTO currDate
	FROM ourSysDATE;
	
	startWindow := ADD_MONTHS(currDate, -1*x);
	
	SELECT SUM(amount) INTO sumBids
	FROM Product
	WHERE status = 'sold' 
	AND buyer = u
	AND sell_date >= startWindow;
	
	IF(sumBids IS NULL) THEN RETURN 0;
	ELSE RETURN sumBids;
	END IF;
END;
/

--Function to find number of bids placed by user in past X months
CREATE OR REPLACE FUNCTION func_bidCount(u IN varchar2, x IN int)
RETURN int IS 
	countBids int;
	startWindow DATE;
	currDate DATE;
BEGIN
	SELECT c_date INTO currDate
	FROM ourSysDATE;
	
	startWindow := ADD_MONTHS(currDate, -1*x);
	
	SELECT count(bidsn) INTO countBids
	FROM BidLog
	WHERE bidder = u
	AND bid_time >= startWindow;
	
	RETURN countBids;
END;
/

--Function to count number of products sold in past X months in given category
CREATE OR REPLACE FUNCTION func_productCount(x IN int, c IN varchar2)
RETURN int IS 
	countProds int;
	startWindow DATE;
	currDate DATE;
BEGIN
	SELECT c_date INTO currDate
	FROM ourSysDATE;
	
	startWindow := ADD_MONTHS(currDate, -1*x);
	
	SELECT count(*) INTO countProds
	FROM Product
	WHERE status = 'sold' 
	AND sell_date >= startWindow
	AND auction_id IN(
		SELECT auction_id
		FROM BelongsTo
		WHERE category = c
	);
	
	RETURN countProds;
END;
/

--Procedure to place product on auction 
CREATE OR REPLACE PROCEDURE proc_putProduct (prodName IN varchar2, prodDesc IN varchar2, allCategories IN varchar2,
											daysToSell IN int, seller IN varchar2, minPrice IN int)  
IS
	next_auction_id int;
	currDate DATE;
	sellDate DATE;
	currCategory varchar2(20);
	
	--creates table with one attribute which lists categories included in input categories
	CURSOR category_cursor IS
		SELECT regexp_substr(allCategories,'[^,]+', 1, LEVEL) 
		FROM dual
		CONNECT BY regexp_substr(allCategories, '[^,]+', 1, LEVEL) IS NOT NULL;
BEGIN
	--Makes max 0 instead of NULL if table empty
	SELECT COALESCE(MAX(auction_id), 0)+1 
	INTO next_auction_id
	FROM Product;
	
	SELECT c_date 
	INTO currDate
	FROM ourSysDATE;
	
	sellDate := currDate+(daysToSell);
	
	INSERT INTO Product VALUES(next_auction_id, prodName, prodDesc, seller, currDate, minPrice, daysToSell, 'under auction', NULL, sellDate, minPrice);
	EXCEPTION WHEN OTHERS THEN
		raise_application_error (-20002,'Exception Caught: Seller login must exist in customer table');
	
	--adds each (product, category) pair to BelongsTo table
	OPEN category_cursor;
	LOOP
		FETCH category_cursor INTO currCategory;
	EXIT WHEN category_cursor%NOTFOUND;
		INSERT INTO BelongsTo VALUES(next_auction_id, currCategory); 
	END LOOP;
	CLOSE category_cursor;
END;
/

--Procedure to get new bid ID and update tables
CREATE OR REPLACE PROCEDURE proc_placeBid(amount IN int, prodName IN varchar2, bidder IN varchar2)
IS
	nextBidSN int;
	matching_auction_id int;
	currDate DATE;
BEGIN
	SELECT COALESCE(MAX(bidsn), 0)+1
	INTO nextBidSN
	FROM BidLog; 
	
	SELECT auction_id
	INTO matching_auction_id
	FROM Product
	WHERE name = prodName;
	
	SELECT c_date 
	INTO currDate
	FROM ourSysDATE;
	
	INSERT INTO BidLog VALUES(nextBidSN, matching_auction_id, bidder, currDate, amount); 
END;
/

--Procedure to initialize ourSysDATE from current time
CREATE OR REPLACE PROCEDURE proc_startTime IS
	currDate DATE;
BEGIN
	SELECT CURRENT_DATE 
	INTO currDate
	FROM Dual;
	
	INSERT INTO ourSysDATE VALUES(currDate);
END;
/
commit;