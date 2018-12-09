import java.util.*;
import java.sql.*;

public class driver{
	public static Connection dbcon;
	
	public static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws SQLException{
		try{
			String dbUser = "dos42", dbPass = "3965052";
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			String dbURL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
			dbcon = DriverManager.getConnection(dbURL, dbUser, dbPass);
			start();
		}catch(SQLException se){
			System.out.println("Error connecting to database.  Machine Error: " + se.toString());
		}
		finally{
			dbcon.close();
		}
	}
	
	public static void start() throws SQLException{
		System.out.println("Assume we start with all tables empty except Administrator and Category Tree");
		
		//Verify Admin
		boolean validAdmin = newMenu.adminValidate("admin", "root", dbcon);
		System.out.println("Testing Admin Validation...");
		System.out.println("Admin Login: " + !validAdmin);
		
		//Add new admin
		newMenu.newAdmin("admin2", "root2", "Driver", "NULL", "admin@aol.com", dbcon);
		
		//Add new customers
		newMenu.newCustomer("dos42", "pass1", "Dylan", "435 Forbes", "dos42@pitt.edu", dbcon);
		newMenu.newCustomer("jed121", "pass2", "Jennifer", "436 Forbes", "jed121@pitt.edu", dbcon);
		newMenu.newCustomer("anw91", "pass3", "Alexander", "437 Forbes", "anw91@pitt.edu", dbcon);
		newMenu.newCustomer("tes42", "pass4", "Tanya", "438 Forbes", "tes42@pitt.edu", dbcon);
		
		//And verify
		System.out.println("Testing Customer Validation:");
		boolean validCustomer = newMenu.validateCustomer("dos42", "pass1", dbcon);
		System.out.println("Customer Login: " + !validCustomer);
		
		//Add Products
		newMenu.productPut("Barbarella Poster", "No Creases", "Film/TV", 5, "dos42", 10, dbcon);
		newMenu.productPut("Pitt Campus Collage", "Made for school project", "Photography", 10, "jed121", 2, dbcon);
		newMenu.productPut("1895 Silver Dollar", "Mint-Condition", "Historical Coins,Silver", 15, "anw91", 75, dbcon);
		newMenu.productPut("Urn: Used", "RIP Grandma", "Period/Style", 2, "dos42", 8, dbcon);
		newMenu.productPut("Plymouth Neon", "Metallic green. Still runs.", "Cars: Used", 35, "jed121", 200, dbcon);
		newMenu.productPut("Suzuki Rebel", "Orange; Needs new fuel line", "Motorcycles", 18, "anw91", 1150, dbcon);
		newMenu.productPut("Model Ship", "SS Endeavor", "Maritime,Vehicles", 20, "tes42", 45, dbcon);
		newMenu.productPut("Ornamental Rug", "14x6", "Textile", 12, "tes42", 115, dbcon);
		
		System.out.println("\n\n");
		//Keyword Search
		System.out.println("Testing Keyword Search:\n");
		newMenu.keySearch("%school%", "%Mint%", dbcon);
		newMenu.keySearch("%Grandma%", "", dbcon);
		
		System.out.println("\n\n");
		//Top-Level Browse
		System.out.println("Testing Root Browse");
		newMenu.topLevelTree(dbcon);
		
		System.out.println("\n\n");
		//Category Browse
		System.out.println("Testing Category Browse");
		newMenu.currTreeDisplay("Vehicles", dbcon);
		
		System.out.println("\n\n");
		//Sort By Price
		System.out.println("Sort Category by Price");
		newMenu.priceSort("Vehicles", dbcon);
		
		System.out.println("\n\n");
		//Sort By Name
		System.out.println("Sort Category by Name");
		newMenu.nameSort("Vehicles", dbcon);
		
		System.out.println("\n\n");
		//Place Bids
		newMenu.bidPlace(225, 5, "dos42", dbcon);
		newMenu.bidPlace(50, 7, "dos42", dbcon);
		newMenu.bidPlace(90, 3, "dos42", dbcon);
		newMenu.bidPlace(12, 1, "jed121", dbcon);
		newMenu.bidPlace(100, 4, "jed121", dbcon);
		newMenu.bidPlace(100, 3, "jed121", dbcon);
		newMenu.bidPlace(14, 1, "anw91", dbcon);
		newMenu.bidPlace(60, 7, "anw91", dbcon);
		newMenu.bidPlace(120, 8, "anw91", dbcon);
		
		
		System.out.println("\n\n");
		//My Auctions
		System.out.println("Display dos42 Auctions");
		newMenu.myAuctions("dos42", dbcon);
		
		System.out.println("\n\n");
		//Show and Withdraw
		System.out.println("Show Sell Price, Withdraw, and Show High Bid");
		int sellPrice1 = newMenu.sellPrice(1, dbcon);
		System.out.println("Sell Price: " + sellPrice1);
		newMenu.withdrawProduct(1, dbcon);
		newMenu.highBid(1, dbcon);
		
		System.out.println("\n\n");
		//Show and Sell
		System.out.println("Show Sell Price, Sell, and Show High Bid");
		int sellPrice2 = newMenu.sellPrice(4, dbcon);
		System.out.println("Sell Price: " + sellPrice2);
		newMenu.sellProduct(4, dbcon);
		newMenu.highBid(4, dbcon);
		
		System.out.println("\n\n");
		//Get Suggestions
		System.out.println("Get Suggestions for jed121");
		newMenu.getSuggest("jed121", dbcon);
		
		System.out.println("\n\n");
		//Show All Products
		System.out.println("Show All Products");
		newMenu.allProducts(dbcon);
		
		System.out.println("\n\n");
		//Change Time
		System.out.println("Change the Time");
		newMenu.newTime("12/28/2018 11:59:59", dbcon);
		
		System.out.println("\n\n");
		//Show All Products Again
		System.out.println("Show All Products Again");
		newMenu.allProducts(dbcon);
		
		System.out.println("\n\n");
		//Show Speicifc User Auctions
		System.out.println("Admin Shows tes42 Auctions");
		newMenu.showUser("tes42", dbcon);
		
		System.out.println("\n\n");
		//Leaf Volume
		System.out.println("Get 2 Top Volume Leaf Nodes");
		newMenu.leafVolume(2, 2, dbcon);
		
		System.out.println("\n\n");
		//Root Volume
		System.out.println("Get 2 Top Volume Root Nodes");
		newMenu.rootVolume(2, 2, dbcon);
		
		System.out.println("\n\n");
		//Top Bidders
		System.out.println("Get 2 Top Bidders");
		newMenu.topBidders(2, 2, dbcon);
		
		System.out.println("\n\n");
		//Top Buyers
		System.out.println("Get 2 Top Buyers");
		newMenu.topBuyers(2, 2, dbcon);
	}
}