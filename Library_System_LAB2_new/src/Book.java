//CHEN Yitong 20098897d, HAO Jiadong 20084595d
public class Book {
	private String title;
	private String ISBN;
	private boolean available = true;
	private MyQueue<String> reservedQueue = new MyQueue<String>();
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}
	
	public String getISBN() {
		return this.ISBN;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public boolean isAvailable() {
		return this.available;
	}
	
	
	public void setReservedQueue(MyQueue<String> reservedQueue) {
		this.reservedQueue = reservedQueue;
	}
	
	public MyQueue<String> getReservedQueue() {
		return this.reservedQueue;
	}
	
	
	
}
