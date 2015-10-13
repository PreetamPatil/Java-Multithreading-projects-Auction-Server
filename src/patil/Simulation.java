/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patil;
 
/**
 *
 * @author Preetam
 */
public class Simulation {
    
     public static void main(String[] args)
    {                
        int nrSellers = 50;
        int nrBidders = 70;
        int spent = 0;
    
        
        Thread[] sellerThreads = new Thread[nrSellers];
        Thread[] bidderThreads = new Thread[nrBidders];
        Seller[] sellers = new Seller[nrSellers];
        Bidder[] bidders = new Bidder[nrBidders];
        
        // Start the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            sellers[i] = new Seller(
            		AuctionServer.getInstance(), 
            		"Seller"+i, 
            		100, 50, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
        }
        
        // Start the buyers
        for (int i=0; i<nrBidders; ++i)
        {
            bidders[i] = new Bidder(
            		AuctionServer.getInstance(), 
            		"Buyer"+i, 
            		1000, 20, 150, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }
        
        // Join on the sellers
        for (int i=0; i<nrSellers; ++i)
        {
            try
            {
                sellerThreads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        // Join on the bidders
        for (int i=0; i<nrBidders; ++i)
        {
            try
            {
                                spent += bidders[i].cashSpent();

                bidderThreads[i].join();

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        System.out.println("Total amount spent by all bidders: "+spent);
  

        // TODO: Add code as needed to debug

        
    }
    
}
