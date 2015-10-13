/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Preetam
 */
public class AuctionServer {

   /**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */


	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private  int soldItemsCount = 0;
	private  int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 

        private final Lock lock1 = new ReentrantLock(); // itemsUpForBidding

        
        
	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		
                     lock1.lock();

                     if (itemsUpForBidding.size() < serverCapacity) {
                        
                       //  lock2.lock();
                         if (itemsPerSeller.containsKey(sellerName)) {
                             
                             if (itemsPerSeller.get(sellerName) < maxSellerItems) {
                                 
                                 
                                 itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName)+1);
                                 
                         //        lock6.lock();
                                 lastListingID = lastListingID+1;
                                 itemsUpForBidding.add(new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs));
                                 
                                 itemsAndIDs.put(lastListingID, new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs));
                                 
                                 lock1.unlock();
                           //      lock2.unlock();
                             //    lock6.unlock();
                                 return lastListingID;
                                 
                                 
                             }else{
                                  lock1.unlock();
                               //   lock2.unlock();
                                 // lock6.unlock();
                                 return -1;
                             }
                             
                         }else{
                             itemsPerSeller.put(sellerName, 1);
                             //lock6.lock();
                             lastListingID = lastListingID +1;
                             itemsUpForBidding.add(new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs));
                                 itemsAndIDs.put(lastListingID, new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs));
                                 
                             
                             
                                  lock1.unlock();
                               //   lock2.unlock();
                                //  lock6.unlock();
                                  return lastListingID;
                         }
                         
                             
                         
                            
                     }else
                        {
                            lock1.unlock();
                            return -1;
                            
                        }
                           
       
	}
            
            
	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public List<Item> getItems()
	{
            
                lock1.lock();
                List<Item> items = new ArrayList<Item>(itemsUpForBidding);
                lock1.unlock();
            
		return items;
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
            
		
            lock1.lock();
            
            if (itemsUpForBidding.size() > 0) {
                
                boolean checkListingID = false;
                int itemNumber = 0;
                for (int i = 0; i < itemsUpForBidding.size(); i++) {
                    
                    if (itemsUpForBidding.get(i).listingID() == listingID) {
                        checkListingID = true;
                        itemNumber = i;
                        break;
                    }
                    
                }
                
                if (checkListingID) {
                    
                    if (itemsUpForBidding.get(itemNumber).biddingOpen()) {
                        
                       // lock3.lock();
                        if (itemsPerBuyer.containsKey(bidderName)) {
                           
                            if (itemsPerBuyer.get(bidderName) < maxBidCount) {
                             //   lock4.lock();
                                if (highestBids.containsKey(listingID)) {
                                    
                                    if (highestBids.get(listingID) > biddingAmount || highestBidders.get(listingID).equals(bidderName)) {
                                        
                                        lock1.unlock();
                         //               lock3.unlock();
                           //             lock4.unlock();
                                        
                                        return false;
                                    }else
                                    {
                               //         lock5.lock();
                                        highestBids.put(listingID, biddingAmount);
                                        highestBidders.put(listingID, bidderName);
                                        itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName)+1);
                                        lock1.unlock();
                                 //       lock3.unlock();
                                   //     lock4.unlock();                                        
                                    //    lock5.unlock();
                                        return true;
                                    }
                                    
                                }else
                                {
                                    if (biddingAmount >= itemsUpForBidding.get(itemNumber).lowestBiddingPrice()) {
                                      //  lock5.lock();
                                        highestBids.put(listingID, biddingAmount);
                                        highestBidders.put(listingID, bidderName);
                                        itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName)+1);
                                        lock1.unlock();
                         //               lock3.unlock();
                           //             lock4.unlock();
                             //           lock5.unlock();
                                        return true;
                                        
                                    }else{
                                        lock1.unlock();
                               //         lock3.unlock();
                                 //       lock4.unlock();

                                        return false;
                                    }
                                    }
                                
                            }else
                            {
                                lock1.unlock();
                               // lock3.unlock();

                                return false;
                            }
                            
                            
                            
                        }else
                        {
                            
                           itemsPerBuyer.put(bidderName,1);
                           //lock4.lock();
                           if (highestBids.containsKey(listingID)) {
                                    
                                    if (highestBids.get(listingID) > biddingAmount || highestBidders.get(listingID).equals(bidderName)) {
                                        lock1.unlock();
                             //           lock3.unlock();
                               //         lock4.unlock();


                                        return false;
                                    }else
                                    {
                                 //       lock5.lock();
                                        highestBids.put(listingID, biddingAmount);
                                        highestBidders.put(listingID, bidderName);
                                        lock1.unlock();
                                   //     lock3.unlock();
                                     //   lock4.unlock();
                                       // lock5.unlock();
                                        return true;
                                    }
                                    
                                }else
                                {
                                    
                                    if (biddingAmount >= itemsUpForBidding.get(itemNumber).lowestBiddingPrice()) {
                                       // lock5.lock();                                        
                                        highestBids.put(listingID, biddingAmount);
                                        highestBidders.put(listingID, bidderName);
                                        lock1.unlock();
                                       // lock3.unlock();
                                       // lock4.unlock();
                                       // lock5.unlock();
                                        return true;
                                        
                                    }else
                                        lock1.unlock();
                                       // lock3.unlock();
                                       // lock4.unlock();            

                                        return false;
                                }
                    
                        }
                        
                    }else{
                           
                                String sellerName = itemsUpForBidding.get(itemNumber).seller();
                                itemsUpForBidding.remove(itemNumber);
                                
                                itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName)-1);

                        lock1.unlock();

                        return false;
                    }
                    
                }else
                {
                    lock1.unlock();

                    return  false;
                }
                
            }
            else{
                lock1.unlock();

                return false;
            }
            
             
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public int checkBidStatus(String bidderName, int listingID)
	{
       
         
            
                boolean checkListingID = false;
                int itemNumber = 0;
                lock1.lock();
               // lock2.lock();
               // lock3.lock();
               // lock5.lock();
                
                for (int i = 0; i < itemsUpForBidding.size(); i++) {
                    
                    if (itemsUpForBidding.get(i).listingID() == listingID) {
                        checkListingID = true;
                        itemNumber = i;
                        break;
                    }
                    
                }
                
                if (checkListingID) {
                        
                    if (itemsUpForBidding.get(itemNumber).biddingOpen()) {
                        lock1.unlock();
                 //       lock2.unlock();
                   //     lock3.unlock();
                     //   lock5.unlock();
                        return 2;
                        
                    }else
                    {   
                        if (highestBidders.get(listingID).equals(bidderName)) {
                            
                            itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName)-1 );
                            soldItemsCount = soldItemsCount + 1;
                            revenue = revenue + highestBids.get(listingID);
                            
                            System.out.println("Sold Items: "+soldItemsCount);
                            System.out.println("Revenue: "+ revenue);
                            
                            String sellerName = itemsUpForBidding.get(itemNumber).seller();
                            itemsUpForBidding.remove(itemNumber);
                            itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName)-1);
                            
                            lock1.unlock();
                       //     lock2.unlock();
                         //   lock3.unlock();
                           // lock5.unlock();

                            return 1;
                            
                        }else
                        {
                            
                               
                                lock1.unlock();
                             //   lock2.unlock();
                               // lock3.unlock();
                               // lock5.unlock();
                                
                            return 3;
                            
                        }
                        
                    }
             
                 }
                else
                {
                    lock1.unlock();
                  //  lock2.unlock();
                   // lock3.unlock();
                   // lock5.unlock();
                    return 3;
                }

	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID)
	{
	
                lock1.lock();
                boolean checkListingID = false;
                int itemNumber = 0;
                for (int i = 0; i < itemsUpForBidding.size(); i++) {
                    
                    if (itemsUpForBidding.get(i).listingID() == listingID) {
                        checkListingID = true;
                        itemNumber = i;
                        break;
                    }
                    
                }
        
     
//            lock4.lock();
            
            if (highestBids.containsKey(listingID)) {  
                int minValue = highestBids.get(listingID);
  //              lock4.unlock();
                lock1.unlock();
                return minValue;
                
            }
            else if (checkListingID){
            int minValue = itemsUpForBidding.get(itemNumber).lowestBiddingPrice();
            lock1.unlock();
    //        lock4.unlock();
            return minValue;
            }
        
            else
            {
      //          lock4.unlock();
                lock1.unlock();
                return -1;
            }
            		
	}


	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public Boolean itemUnbid(int listingID)
	{
          
            lock1.lock();

            if (highestBids.containsKey(listingID)) {
                lock1.unlock();
                return false;
                
            }
            else{
                lock1.unlock();
                return true;
            }
            
	}

}
