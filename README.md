# CS1555-Project-Team-7
CS1555 Term Project Repository for Dylan Sporrer (dos42), Jennifer Dudek (jed121) and Alexander Washy (anw91)

Team 7 - Phase 2 & 3 ReadMe File
treeGen.sql contains a category tree which Driver.java and Benchmark.java have been constructed around
	- Please run this file after schema.sql and trigger.sql but before any java files

The MyAuction.java file contains the menu system and all SQL operations for phase 2. Before running the file, change the dbUser and dbPass string values found in the main method to your personal database lgin info. 

Navigating the menu is done primarily through numbered lists, be sure to enter the number of the option not its name.

Two login options are available: Customer and Administrator
	- Using Team 7's Schema.sql file, the default admin credentials are:
		admin 
		root
	- Customer login can be used once a new customer is added by an admin
	
Customer Action Notes:

(a) To navigate categories as a customer, enter the case sensitive name of one of the categories listed upon entry to the browse option and each time a category
is changed. The price sort or name sort returns when browsing will return all items under any child of the current category or under the current category itself.

(b) When searching products by description, if only one keyword is desired, simply leave the other blank.

(c) Placing an item for auction simply requires entering relevant info. Be sure to spell category names correctly and add the product to leaf categories only 
as any non-leaf category inputs or category inputs which do not match existing categories will not be included with the product.

(d) Bidding on a product requires knowlege of the products ID beforehand

(e) When selling products, all products of the current user which are "under auction" will be shown first. One a product ID is selected, the product must
be sold or withdrawn regardless of current sell price

Admin Action Notes:

(b) The new time value must be entered in the format specified. Otherwise the update will not occur.

(d) Month window and result limt must be entered before a statistic type is selected.

To exit the system, you must navigate back up to the starting menu.
