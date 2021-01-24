import java.util.concurrent.*;
import java.util.concurrent.locks.*;

//this result is much closer to 100 because synchronized blocks of code 
//aren't the perfect way to communicate information between blocks, because
//we want to have the threads build off each other. Right now, they are accessing 
//similar information and its locked, but the information is not properly updated.
//To do so, we need to use conditions.
public class AccountWithSyncUser {
	private static Account userAccount = new Account();
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(int i = 0; i < 100; i++) {
			executor.execute(new AddAPenny());
		}
		executor.shutdown();
		while(!executor.isShutdown()) {
		}
		System.out.println("What is balance? " + userAccount.getBalance());
	}
	private static class AddAPenny implements Runnable{
		public void run() {
			//synchronization for user account.
			synchronized(userAccount) {
				userAccount.deposit(1);
			}
		}
	}
	
	private static class Account{
		//we use a reentrant lock to specify the fairness policy
		//its set to true, meaning that a thread that been waiting the
		//longest with the highest priority will be passed this lock after the 
		//thread finishes.
		
		//if its set to false, the lock will randomly be passed between threads
		//its faster, but it will have longer variances between execution times.
		//This is because if one thread is very important, there will be no guarantee
		//that it will actually get the lock it needs.
		private static Lock lock = new ReentrantLock(true);
		private int balance = 0;
		public int getBalance() {
			return balance;
		}
		
		public void deposit(int amount) {
			//lock aquired
			lock.lock();
			try {
				int newBalance = balance + amount;
				balance = newBalance;
			}
			finally {
				//once a thread is done, it releases the lock.
				lock.unlock();
			}
		}
		
	}
}
