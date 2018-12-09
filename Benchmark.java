import java.util.*;
import java.sql.*;

public class Benchmark{
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
		//Set Time
		for(int i = 1; i < 11; i++){
			String nextTime = "12/" + Integer.toString(i) + "/2018 11:59:59";
			newMenu.newTime(nextTime, dbcon);
		}
		
		//Make New Admins and Log In as Them
		for(int i = 1; i < 11; i++){
			String nextLogin = "Admin" + Integer.toString(i);
			String nextPass = "APass" + Integer.toString(i);
			newMenu.newAdmin(nextLogin, nextPass, "Stress", "Stress", "admin@aol.com", dbcon);
			newMenu.adminValidate(nextLogin, nextPass, dbcon);
		}
		
		//Make New Customers and Log In as Them
		for(int i = 1; i < 11; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			String nextPass = "CPass" + Integer.toString(i);
			newMenu.newCustomer(nextLogin, nextPass, "Stress", "Stress", "admin@aol.com", dbcon);
			newMenu.validateCustomer(nextLogin, nextPass, dbcon);
		}
		
		//Add a product for each customer
		for(int i = 1; i < 6; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			String nextItem = "item" + Integer.toString(i);
			newMenu.productPut(nextItem, "Description1", "Film/TV", 5, nextLogin, 10, dbcon);
		}
		
		for(int i = 6; i < 11; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			String nextItem = "item" + Integer.toString(i);
			newMenu.productPut(nextItem, "Description2", "Maritime", 5, nextLogin, 10, dbcon);
		}
		
		//Search Keywords
		for(int i = 1; i < 6; i++){
			newMenu.keySearch("%1%", "%3%", dbcon);
		}
		for(int i = 6; i < 11; i++){
			newMenu.keySearch("%2%", "%4%", dbcon);
		}
		
		//Top Browse
		for(int i = 1; i < 11; i++){
			newMenu.topLevelTree(dbcon);
		}
		
		//Category Browse and Sorts
		for(int i = 1; i < 6; i++){
			newMenu.currTreeDisplay("Memorabilia", dbcon);
			newMenu.priceSort("Memorabilia", dbcon);
			newMenu.nameSort("Memorabilia", dbcon);
		}
		
		for(int i = 6; i < 11; i++){
			newMenu.currTreeDisplay("Collectibles/Art", dbcon);
			newMenu.priceSort("Collectibles/Art", dbcon);
			newMenu.nameSort("Collectibles/Art", dbcon);
		}
		
		//Place Bids
		for(int i = 1; i < 6; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			int nextPrice = 15+i;
			newMenu.bidPlace(nextPrice, 5, nextLogin, dbcon);
		}
		
		for(int i = 6; i < 11; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			int nextPrice = 15+i;
			newMenu.bidPlace(nextPrice, 6, nextLogin, dbcon);
		}
		
		//Show My Auctions
		for(int i = 1; i < 11; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			newMenu.myAuctions(nextLogin, dbcon);
		}
		
		//Show Sell Price
		for(int i = 1; i < 6; i++){
			int sellPrice = newMenu.sellPrice(5, dbcon);
			System.out.println("Sell Price = " + sellPrice);
		}
		
		for(int i = 6; i < 11; i++){
			int sellPrice = newMenu.sellPrice(6, dbcon);
			System.out.println("Sell Price = " + sellPrice);
		}
		
		//Withdraw
		for(int i = 1; i < 6; i++){
			newMenu.withdrawProduct(i, dbcon);
		}
		
		//Sell
		for(int i = 6; i < 11; i++){
			newMenu.sellProduct(i, dbcon);
		}
		
		//Show High Price
		for(int i = 1; i < 11; i++){
			newMenu.highBid(5, dbcon);
		}
		
		//Suggestions
		for(int i = 1; i < 11; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			newMenu.getSuggest(nextLogin, dbcon);
		}
		
		//All Products
		for(int i = 1; i < 11; i++){
			newMenu.allProducts(dbcon);
		}
		
		//Show Speicifc User Auctions
		for(int i = 1; i < 11; i++){
			String nextLogin = "Customer" + Integer.toString(i);
			newMenu.showUser(nextLogin, dbcon);
		}
		
		//Leaf Volume
		for(int i = 1; i < 11; i++){
			newMenu.leafVolume(2, i, dbcon);
		}
		
		//Root Volume
		for(int i = 1; i < 11; i++){
			newMenu.rootVolume(2, i, dbcon);
		}
		
		//Top Bidders
		for(int i = 1; i < 11; i++){
			newMenu.topBidders(2, i, dbcon);
		}
		
		//Top Buyers
		for(int i = 1; i < 11; i++){
			newMenu.topBuyers(2, i, dbcon);
		}
	}
}