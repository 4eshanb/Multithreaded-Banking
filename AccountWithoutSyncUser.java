import java.util.concurrent.*;
public class AccountWithoutSyncUser {
	private static Account userAccount = new Account();
	public static void main(String[] args) {
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		//all 100 threads have a shared memory, since we are dealing with 1 account.
		for(int i = 0; i < 100; i++) {
			executor.execute(new AddAPenny());
		}
		
		//if any operations are still going, let them execute
		//but, do not add any new operations.
		executor.shutdown();
		while(!executor.isShutdown()) {
			
		}
		System.out.println("What is balance? " + userAccount.getBalance());
	}
	private static class AddAPenny implements Runnable{
		public void run() {
				userAccount.deposit(1);
		}
	}
	
	private static class Account{
		private int balance = 0;
		
		public void deposit(int amount) {
			int newBalance = balance + amount;
			//when you have multiple threads running at the same time
			//each thread will have access to the same memory.
			//This can get tricky because multiple threads can Add a penny
			//at the same time, but they will do so with the same balance. 
			//Therefore, we need synchronization, otherwise we will get a really low
			//number for our total balance.
			
			//at run-time, each thread gets a specific amount of time
			//so the answer after running this will not be the same each time.
			//we try and use thread.sleep to allow a pause among each thread's execution,
			//so that the threads don't move too fast.
			//however, it does not work all the time.
			//these are Race Conditions
			try {
				Thread.sleep(1);
			}
			catch(InterruptedException ex) {
				
			}
			balance = newBalance;
		}
		
		public int getBalance() {
			return balance;
		}
		
		
	}
}
