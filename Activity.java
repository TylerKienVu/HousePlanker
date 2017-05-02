package mainFiles;


public interface Activity {
	public String getID();
	public boolean validate() throws InterruptedException;			
	public void run() throws InterruptedException;
}
