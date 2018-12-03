import java.util.*;
import java.sql.*;

public class menus{
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
	
	public static void start(){
		boolean invalid;
		int select = 0;
		
		do{
			invalid = false;
			
			System.out.println(
				"---Login------------------------------\n" +
				"Select An Option:\n" +
				"1.) Customer Login\n" +
				"2.) Administrator Login\n" +
				"3.) Exit System\n"+
				"--------------------------------------\n"
			);
			
			try{
				select = scan.nextInt();
				scan.nextLine();
			}catch(java.util.InputMismatchException IME){
				scan.nextLine();
			}
			
			switch (select) {
			case 1:
				try{
					custLogin("NONE", "NONE");
				}catch(SQLException se1){se1.toString();}
				break;
			case 2:
				try{
					adminLogin();
				}catch(SQLException se2){se2.toString();}
				break;
			case 3:
				System.out.println("Goodbye");
			default:
				System.out.println("Invalid selection\n");
				invalid = true;
				break;
			}
		}while(invalid);
	}
	
	public static void custLogin(String validLogin, String validPass) throws SQLException{
		boolean invalid;
		String usr;
		String pass;
		
		if(validLogin.equals("NONE")){
			do{
				invalid = false;
				
				System.out.print("Enter Username: ");
				usr = scan.nextLine();
				System.out.print("Enter Password: ");
				pass = scan.nextLine();
			
				try{
					//Run Query on Customer Table to check info
					PreparedStatement custFind = dbcon.prepareStatement("SELECT * FROM Customer WHERE login= ? AND password = ?");
					custFind.setString(1, usr);
					custFind.setString(2, pass);
					ResultSet validUser = custFind.executeQuery();
					
					if(validUser.next() == false){
						System.out.println("Invalid Username or Password");
						invalid = true;
					}
				}catch(SQLException se3){se3.toString();}
			}while(invalid);
		}
		else{
			usr = validLogin;
			pass = validPass;
		}
		
		do{
			int select = 0;
			invalid = false;
			
			System.out.println(
				"\n\n\n---Customer Actions------------------\n" +
				"---Select An Option:-----------------\n" +
				"1.) Browse Products\n" +
				"2.) Search Products By Keyword\n" +
				"3.) Auction Product\n" +
				"4.) Place Bid\n" +
				"5.) Manage Auctions\n" +
				"6.) Get Product Suggestions\n" +
				"7.) Back\n" +
				"--------------------------------------\n"
			);
			
			try{
				select = scan.nextInt();
				scan.nextLine();
			}catch(java.util.InputMismatchException IME){
				scan.nextLine();
				invalid = true;
			}
			
			switch (select) {
			case 1:
				browse(usr, pass);
				break;
			case 2:
				System.out.print("\nEnter First Search Term: ");
				String term1 = scan.nextLine();
				if(term1.equals("")){term1 = "%This wiIUOll not XX appear in YYJJ descriptions%"}
				else{term1 = "%"+term1+"%";}
				
				System.out.print("\nEnter Second Search Term: ");
				String term2 = scan.nextLine();
				if(term2.equals("")){term2 = "%This wiIUOll not XX appear in YYJJ descriptions%"}
				else{term2 = "%"+term2+"%";}
			  
				//Query to return all with keyword(s)
				try{
					PreparedStatement keyWords = dbcon.prepareStatement("SELECT * FROM Product WHERE description LIKE ? OR description LIKE ?");
					keyWords.setString(1, term1);
					keyWords.setString(2, term2);
					ResultSet resSet = keyWords.executeQuery();
					ResultSetMetaData rsmd = resSet.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					for (int i = 1; i <= columnsNumber; i++) {
						System.out.print(rsmd.getColumnName(i) + "\t");
					}
					while (resSet.next()) {
						System.out.println("");
						for (int i = 1; i <= columnsNumber; i++) {
							String columnValue = resSet.getString(i);
							System.out.print(columnValue + "\t");
						}
						System.out.println("");
					}
				}catch(SQLException se4){se4.toString(); System.out.print("Keyword error");}
				break;
			case 3:
				String allCategories = "";
				
				System.out.print("\nEnter Product Name: ");
				String name = scan.nextLine();
				
				System.out.print("\nEnter Description: ");
				String desc = scan.nextLine();
				
				String nextCategory = "";
				do{
					
					System.out.print("Enter A Category (Type EXIT to End Entry): ");
					nextCategory = scan.nextLine();
					if(!nextCategory.equals("EXIT") && !nextCategory.equals("exit")){
						allCategories = allCategories + "," + nextCategory;
					}
				  
				}while(!nextCategory.equals("EXIT") && !nextCategory.equals("exit"));
				
				boolean badIn;
				int daysToSell = 0, startPrice = 0;
				do{
					badIn = false;
					System.out.print("\nEnter Days Until Close: ");
					try{
						daysToSell = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException bI){scan.nextLine(); badIn = true;}
				}while(badIn);
				
				do{
					badIn = false;
					System.out.print("\nEnter Starting Price: ");
					try{
						startPrice = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException BI2){scan.nextLine(); badIn = true;}
				}while(badIn);
				//Run proc_putProduct() with info
				try{	
					PreparedStatement putProduct = dbcon.prepareStatement("CALL proc_putProduct(?, ?, ?, ?, ?, ?)");
					putProduct.setString(1, name);
					putProduct.setString(2, desc);
					putProduct.setString(3, allCategories);
					putProduct.setInt(4, daysToSell);
					putProduct.setString(5, usr);
					putProduct.setInt(6, startPrice);
					putProduct.executeQuery();
				}catch(SQLException se5){se5.toString();}
				break;
			case 4:
				System.out.print("\nEnter ID of Product To Bid On: ");
				int bidID = scan.nextInt();
				scan.nextLine();
				
				System.out.print("\nEnter Bid Amount: ");
				int bidAmount = scan.nextInt();
				scan.nextLine();
				
				//Run proc_placeBid() with info
				try{
					PreparedStatement placeBid = dbcon.prepareStatement("CALL proc_placeBid(?, ?, ?)");
					placeBid.setInt(1, bidAmount);
					placeBid.setInt(2, bidID);
					placeBid.setString(3, usr);
					placeBid.executeQuery();
				}catch(SQLException se6){se6.toString(); System.out.println("Bid Error");}
				break;
			case 5:
				boolean subsubInvalid = false;
				int manageID = 0;
				//Run Query to show auctions by user
				try{
					PreparedStatement showMyAuctions = dbcon.prepareStatement("SELECT * FROM Product WHERE seller = ? AND status = 'under auction'");
					showMyAuctions.setString(1, usr);
					ResultSet resSet = showMyAuctions.executeQuery();
					ResultSetMetaData rsmd = resSet.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					for (int i = 1; i <= columnsNumber; i++) {
						System.out.print(rsmd.getColumnName(i) + "\t");
					}
					while (resSet.next()) {
						System.out.println("");
						for (int i = 1; i <= columnsNumber; i++) {
							String columnValue = resSet.getString(i);
							System.out.print(columnValue + "\t");
						}
						System.out.println("");
					}
				}catch(SQLException se7){se7.toString();}
				
				do{
					System.out.print("\nEnter the ID of the Auction to Sell or Withdraw: ");
					try{
						manageID = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException IMESell){
						subsubInvalid = true;
					}
				}while(subsubInvalid);
				
				int sellPrice = 0;
				//Run Query to show sell price
				try{
					PreparedStatement showSellPrice = dbcon.prepareStatement("SELECT max(amount) as sellPrice FROM BidLog WHERE auction_id = ? AND amount <> (SELECT max(amount) FROM Bidlog WHERE auction_id = ?)");
					showSellPrice.setInt(1, manageID);
					showSellPrice.setInt(2, manageID);
					ResultSet rs = showSellPrice.executeQuery();
					sellPrice = rs.getInt("sellPrice");
					if(sellPrice == 0){
						PreparedStatement showOnlyPrice = dbcon.prepareStatement("SELECT max(amount) as sellPrice FROM BidLog WHERE auction_id = ?");
						showSellPrice.setInt(1, manageID);
						showSellPrice.setInt(2, manageID);
						ResultSet rso = showOnlyPrice.executeQuery();
						sellPrice = rso.getInt("sellPrice");
					}
				}catch(SQLException se8){se8.toString();}
				
				System.out.println("This product would sell for: " + sellPrice); 
				System.out.println("Would you still like to sell it (Y/N)? ");
				String ans = scan.nextLine();
							
				if(ans.charAt(0) == 'y' || ans.charAt(0) == 'Y'){
					//Run Query to sell product with ID
					try{
						PreparedStatement sellAuction = dbcon.prepareStatement("Call proc_sellProduct(?)");
						sellAuction.setInt(1, manageID);
						sellAuction.executeQuery();
					}catch(SQLException se9){se9.toString(); System.out.println("Product Withdraw Error");}
					
					System.out.println("Product Sold");
				}
				else{
					//Run Query to withdraw auction with ID
					try{
						PreparedStatement withdrawAuction = dbcon.prepareStatement("UPDATE Product SET status = 'withdrawn' WHERE auction_id = ? AND status = 'under auction'");
						withdrawAuction.setInt(1, manageID);
						withdrawAuction.executeQuery();
					}catch(SQLException se10){se10.toString(); System.out.println("Product Withdraw Error");}
					
					System.out.println("Product Withdrawn");
				}
				break;
			case 6:
				//Run Suggestions Query
				try{
					PreparedStatement suggest = dbcon.prepareStatement("SELECT auction_id, name, description, amount, count(DISTINCT bidder) as friendNum FROM BidLog NATURAL JOIN Product WHERE bidder IN (SELECT bidder FROM BidLog WHERE auction_id IN(SELECT auction_id FROM BidLog WHERE bidder = ?) AND bidder <> ?)GROUP BY auction_id, name, description, amount ORDER BY friendNum DESC");
					
					suggest.setString(1, usr);
					suggest.setString(2, usr);
					ResultSet resSet = suggest.executeQuery();
					ResultSetMetaData rsmd = resSet.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					for (int i = 1; i <= columnsNumber; i++) {
						System.out.print(rsmd.getColumnName(i) + "\t");
					}
					while (resSet.next()) {
						System.out.println("");
						for (int i = 1; i <= columnsNumber; i++) {
							String columnValue = resSet.getString(i);
							System.out.print(columnValue + "\t");
						}
						System.out.println("");
					}
				}catch(SQLException se11){se11.toString(); System.out.println("Suggestion Query Error");}
				break;
			case 7:
				start();
				break;
			default:
				System.out.println("Invalid selection\n");
				invalid = true;
				break;
			}
		}while(true);
	}
	
	public static void browse(String usr, String pass) throws SQLException{
		int select = 0;
		boolean invalid;
		String currCategory = "NULL";
		
		//Query Shows Top Level of Category Tree
		try{
			PreparedStatement treeRoot = dbcon.prepareStatement("SELECT name FROM Category WHERE parent_category IS NULL");
			ResultSet resSet = treeRoot.executeQuery();
			ResultSetMetaData rsmd = resSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			for (int i = 1; i <= columnsNumber; i++) {
				if(i == 1){System.out.println("---Category Names---");}
				else{System.out.print(rsmd.getColumnName(i) + "\t");}
			}
			while (resSet.next()) {
				System.out.println("");
				for (int i = 1; i <= columnsNumber; i++) {
					String columnValue = resSet.getString(i);
					System.out.print(columnValue + "\t");
				}
				System.out.println("");
			}
		}catch(SQLException se12){se12.toString();}
		do{
			System.out.println(
				"\n\n\n---Select An Option:------------------\n" +
				"1.) Change Category\n" +
				"2.) Browse Current Category By Price\n" +
				"3.) Browse Current Category By Name\n" +
				"4.) Back\n" +
				"--------------------------------------\n"
			);
			
			try{
				select = scan.nextInt();
				scan.nextLine();
			}catch(java.util.InputMismatchException IME){scan.nextLine();}
			
			switch (select) {
			case 1:
				boolean invalidCategory;
				do{
					invalidCategory = false;
					System.out.print("Enter the name of the category you wish to navigate to: ");
					currCategory = scan.nextLine();
					//Query to display contents of category tree with currCategory as root
					try{
						//Run Query on Customer Table to check info
						PreparedStatement categoryNav = dbcon.prepareStatement("SELECT name FROM Category WHERE parent_category = ? OR name = ?");
						categoryNav.setString(1, currCategory);
						categoryNav.setString(2, currCategory);
						ResultSet resSet = categoryNav.executeQuery();
					
						if(resSet.next() == false){
							System.out.println("Invalid Category Name");
							invalidCategory = true;
						}
						else{
							System.out.println();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								if (i == 1) {
									System.out.println("---Category Names---");
								} else {
									System.out.print(rsmd.getColumnName(i) + "\t");
								}
							}
							while (resSet.next()) {
							System.out.println("");
							for (int i = 1; i <= columnsNumber; i++) {
								String columnValue = resSet.getString(i);
								System.out.print(columnValue + "\t");
							}
							System.out.println("");
						}
						}
					}catch(SQLException se13){se13.toString();}
				}while(invalidCategory);
				break;
			case 2:
				//Query to Browse by Price with Category = currCategory
				try{
					PreparedStatement categoryReturnP = dbcon.prepareStatement("SELECT * FROM Product WHERE auction_id IN(SELECT auction_id FROM BelongsTo WHERE category IN(SELECT name FROM Category WHERE parent_category IN(SELECT name FROM Category WHERE parent_category = ?) OR parent_category = ? OR name = ?) AND status = 'under auction') ORDER BY amount DESC");
					categoryReturnP.setString(1, currCategory);
					categoryReturnP.setString(2, currCategory);
					categoryReturnP.setString(3, currCategory);
					ResultSet resSet = categoryReturnP.executeQuery();
					ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
				}catch(SQLException se14){se14.toString(); System.out.println("Price sort Error");}
				break;
			case 3:
				//Query to Browse by Name with Category = currCategory
				try{
					PreparedStatement categoryReturnN = dbcon.prepareStatement("SELECT * FROM Product WHERE auction_id IN(SELECT auction_id FROM BelongsTo WHERE category IN(SELECT name FROM Category WHERE parent_category IN(SELECT name FROM Category WHERE parent_category = ?) OR parent_category = ? OR name = ?) AND status = 'under auction') ORDER BY name");
					categoryReturnN.setString(1, currCategory);
					categoryReturnN.setString(2, currCategory);
					categoryReturnN.setString(3, currCategory);
					ResultSet resSet = categoryReturnN.executeQuery();
					ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
				}catch(SQLException se15){se15.toString(); System.out.println("Name sort Error");}
				break;
			case 4:
				try{
					custLogin(usr, pass);
				}catch(SQLException se16){se16.toString();}
				break;
			default:
				System.out.println("Invalid selection\n");
				break;
			}
		}while(true);
	}
	public static void adminLogin() throws SQLException{
		boolean invalid;
		do{
			invalid = false;
			
			System.out.print("Enter Username: ");
			String usr = scan.nextLine();
			System.out.print("Enter Password: ");
			String pass = scan.nextLine();
			
			//Run Query on Admin Table to check info
			try{
				PreparedStatement adminFind = dbcon.prepareStatement("SELECT * FROM Administrator WHERE login= ? AND password = ?");
				adminFind.setString(1, usr);
				adminFind.setString(2, pass);
				ResultSet validAdmin = adminFind.executeQuery();
				
				if(validAdmin.next() == false){
					System.out.println("Invalid Username or Password");
					invalid = true;
				}
			}catch(SQLException se17){se17.toString();}
		}while(invalid);
		
		do{
			invalid = false;
			int select = 0;
			
			System.out.println(
				"\n\n\n---Admin Actions----------------------\n" +
				"---Select An Option:------------------\n" +
				"1.) Add Customer\n" +
				"2.) Update SysTime\n" +
				"3.) View Products\n" +
				"4.) View Statistics\n" +
				"5.) Back\n" +
				"--------------------------------------\n"
			);
			
			try{
				select = scan.nextInt();
				scan.nextLine();
			}catch(java.util.InputMismatchException IME){scan.nextLine();}
			
			switch (select) {
			case 1:
				System.out.print("Enter User Username: ");
				String custUsr = scan.nextLine();
				System.out.print("Enter User Password: ");
				String custPass = scan.nextLine();
				System.out.print("Enter User Name: ");
				String custName = scan.nextLine();
				System.out.print("Enter User Address: ");
				String custAddr = scan.nextLine();
				System.out.print("Enter User Email: ");
				String custEmail = scan.nextLine();
				System.out.println("Is the User an Admin (Y/N)? ");
				String adminResp = scan.nextLine();
				
				//Run Insert on customer or admin table
				if(adminResp.charAt(0) == 'y' || adminResp.charAt(0) == 'Y'){
					try{
						PreparedStatement newCustomer = dbcon.prepareStatement("INSERT INTO Administrator VALUES(?,?,?,?,?)");
						newCustomer.setString(1, custUsr);
						newCustomer.setString(2, custPass);
						newCustomer.setString(3, custName);
						newCustomer.setString(4, custAddr);
						newCustomer.setString(5, custEmail);
						newCustomer.executeQuery();
					}catch(SQLException se18){se18.toString(); System.out.println("New Customer Error");}
				}
				else{
					try{
						PreparedStatement newCustomer = dbcon.prepareStatement("INSERT INTO Customer VALUES(?,?,?,?,?)");
						newCustomer.setString(1, custUsr);
						newCustomer.setString(2, custPass);
						newCustomer.setString(3, custName);
						newCustomer.setString(4, custAddr);
						newCustomer.setString(5, custEmail);
						newCustomer.executeQuery();
					}catch(SQLException se18){se18.toString(); System.out.println("New Admin Error");}
				}
				break;
			case 2:
				System.out.print("Enter New Date in MM/DD/YYYY hh:mi:ss Format: ");
				String newDate = scan.nextLine();
				
				//Run procedure to set new time
				try{
					PreparedStatement newTime = dbcon.prepareStatement("UPDATE ourSysDATE SET c_date = TO_DATE(?, 'MM/DD/YYYY hh:mi:ss')");
					newTime.setString(1, newDate);
					newTime.executeUpdate();
				}catch(SQLException se19){se19.toString(); System.out.println("Date Update Error");}
				break;
			case 3:
				boolean subInvalid;
				int selectTwo = 0;
				
				do{
					subInvalid = false;
					
					System.out.println(
					"\n\n\n---Select An Option:------------------\n" +
					"1.) Display All\n" +
					"2.) Display From Customer\n" +
					"3.) Back\n" +
					"--------------------------------------\n"
					);
					
					try{
						selectTwo = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException subIME){scan.nextLine();}
				
					switch (selectTwo) {
					case 1:
						//Run Query to show all products
						try{
							PreparedStatement allProducts = dbcon.prepareStatement("SELECT * FROM Product");
							ResultSet resSet = allProducts.executeQuery();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
						}catch(SQLException se20){se20.toString();}
						break;
					case 2:
						System.out.print("Enter Customer Username: ");
						String userName = scan.nextLine();
						//Run Query to show given a user
						try{
							PreparedStatement someProducts = dbcon.prepareStatement("SELECT * FROM Product WHERE seller = ?");
							someProducts.setString(1, userName);
							ResultSet resSet = someProducts.executeQuery();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
						}catch(SQLException se21){se21.toString();}
						break;
					case 3:
						invalid = true;
						break;
					default:
						System.out.println("Invalid selection\n");
						subInvalid = true;
						break;
					}
				}while(subInvalid);
				break;
			case 4:
				boolean subsubInvalid;
				boolean invalidIns;
				int selectThree = 0;
				int monthVal = 0, kVal = 0;
				
				do{
					invalidIns = false;
					System.out.print("Enter value for time-window in months: ");
					try{
						monthVal = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException IME){
						scan.nextLine();
						System.out.println("Invalid Value for Months");
						invalidIns = true;
					}
					
					System.out.print("Enter value for number of results: ");
					try{
						kVal = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException IME){
						scan.nextLine();
						System.out.println("Invalid Value for Result Limit");
						invalidIns = true;
					}
				}while(invalidIns);
				
				do{
					subsubInvalid = false;
					
					System.out.println(
					"\n\n\n---Select An Option:------------------\n" +
					"1.) Top-K Volume Leaf Categories\n" +
					"2.) Top-K Volume Root Categories\n" +
					"3.) Top-K Bidders\n" +
					"4.) Top-K Buyers\n" +
					"5.) Back\n" +
					"--------------------------------------\n"
					);
					
					try{
						selectThree = scan.nextInt();
						scan.nextLine();
					}catch(java.util.InputMismatchException subsubIME){scan.nextLine();}
				
					switch (selectThree) {
					case 1:
						//Run Leaf Volume Query
						try{
							PreparedStatement leafVolume = dbcon.prepareStatement("SELECT name, func_productCount(?, name) as prodNum FROM Category WHERE name NOT IN (SELECT parent_category FROM Category) AND ROWNUM < ? GROUP BY name ORDER BY prodNum DESC");
							leafVolume.setInt(1, monthVal);
							leafVolume.setInt(2, kVal);
							ResultSet resSet = leafVolume.executeQuery();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
						}catch(SQLException se22){se22.toString(); System.out.println("Leaf Volume Error");}
						break;
					case 2:
						//Run Root Volume Query
						try{
							PreparedStatement rootVolume = dbcon.prepareStatement("SELECT name, func_productCount(?, name) as prodNum FROM Category WHERE parent_category IS NULL AND ROWNUM < ? GROUP BY name ORDER BY prodNum DESC");
							rootVolume.setInt(1, monthVal);
							rootVolume.setInt(2, kVal);
							ResultSet resSet = rootVolume.executeQuery();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
						}catch(SQLException se23){se23.toString(); System.out.println("Root Volume Error");}
						break;
					case 3:
						//Run Query to show bidders
						try{
							PreparedStatement bidVolume = dbcon.prepareStatement("SELECT login, func_bidCount(login, ?) as bidNum FROM Customer WHERE ROWNUM < ? GROUP BY login ORDER BY bidNum DESC");
							bidVolume.setInt(1, monthVal);
							bidVolume.setInt(2, kVal);
							ResultSet resSet = bidVolume.executeQuery();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
						}catch(SQLException se24){se24.toString(); System.out.println("Bidder Volume Error");}
						break;
					case 4:
						//Run Query to show buyers
						try{
							PreparedStatement buyVolume = dbcon.prepareStatement("SELECT login, func_buyingAmount(login, ?) as amountSpent FROM Customer WHERE ROWNUM < ? GROUP BY login ORDER BY amountSpent DESC");
							buyVolume.setInt(1, monthVal);
							buyVolume.setInt(2, kVal);
							ResultSet resSet = buyVolume.executeQuery();
							ResultSetMetaData rsmd = resSet.getMetaData();
							int columnsNumber = rsmd.getColumnCount();
							for (int i = 1; i <= columnsNumber; i++) {
								System.out.print(rsmd.getColumnName(i) + "\t");
							}
							while (resSet.next()) {
								System.out.println("");
								for (int i = 1; i <= columnsNumber; i++) {
									String columnValue = resSet.getString(i);
									System.out.print(columnValue + "\t");
								}
								System.out.println("");
							}
						}catch(SQLException se25){se25.toString(); System.out.println("Buy Volume Error");}
						break;
					case 5:
						subsubInvalid = true;
						break;
					default:
						System.out.println("Invalid selection\n");
						break;
					}
				}while(!subsubInvalid);
				break;
			case 5:
				start();
				break;
			default:
				System.out.println("Invalid selection\n");
				invalid = true;
				break;
			}
		}while(true);
	}
}