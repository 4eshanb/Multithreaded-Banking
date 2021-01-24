import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AccountWithConditionsUser {
	private static Account account = new Account();
	public static void main(String[] args) {
		System.out.println("Thread 1\t\tThread 2\t\tBalance");
		//we only need two threads, therefore we use a fixedthreadPool
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		//java will start the two threads almost simultaneously
		//because line 13 has no relation to line14
		executor.execute(new DepositTask());
		executor.execute(new WithdrawTask());
		//this allows us to end the program when the two threads are 
		//finished with execution.
		executor.shutdown();
		while(!executor.isShutdown()) {
		}
	}
	public static class DepositTask implements Runnable{
		public void run() {
			try {
				while(true) {
					account.deposit((int)(Math.random() * 10 ) + 1);
					//we need to put the thread to sleep, so all
					//threads have time to execute.
					Thread.sleep(1000);
				}
			}
			catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static class WithdrawTask implements Runnable{
		public void run() {
			while(true) {
				account.withdraw((int)(Math.random() * 10) + 1);
			}
		}
	}
	
	private static class Account{
		private static Lock lock = new ReentrantLock(true);
		private static Condition newDeposit = lock.newCondition();
		private int balance = 0;
		public int getBalance() {
			return balance;
		}
		
		public void withdraw(int amount) {
			lock.lock();
			try {
				//not enough money to withdraw
				while(balance < amount) {
					System.out.println("\t\t\tWait for deposit");
					//signaling that we need more money on this account
					//thus we wait until a deposit happens.
					newDeposit.await();
				}
				//once we get enough money to withdraw, the 
				//while loop is executed
				balance -=amount;
				System.out.println("\t\t\tWithdraw " + amount + "\t\t" + getBalance());
			}
			catch(InterruptedException ex) {
				ex.printStackTrace();
			}
			finally {
				lock.unlock();
			}
		}
		
		public void deposit(int amount) {
			lock.lock();
			try {
				balance = balance + amount;
				System.out.println("Deposit " + amount + "\t\t\t\t\t" + getBalance());
				//we signal that the deposit is done and then wake up all waiting threads.
				newDeposit.signalAll();
			
			}
			finally {
				lock.unlock();
			}
		}
		
	}
}
