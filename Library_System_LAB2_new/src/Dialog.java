//CHEN Yitong 20098897d, HAO Jiadong 20084595d
import java.awt.BorderLayout;
import java.util.Iterator;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Dialog extends JDialog{
	
	JTextArea bookTextArea = new JTextArea();
	JButton borrowButton = new JButton("Borrow");
	JButton returnButton = new JButton("Return");
	JButton reserveButton = new JButton("Reserve");
	JButton queueButton = new JButton("Waiting Queue");
	JLabel imageLabel = new JLabel();
	JTextArea promptArea = new JTextArea();
	
	private Book thisBook;
	
	public Dialog(JFrame frame,Book thisBook) {
		super(frame,thisBook.getTitle(),true);
		this.thisBook = thisBook;
		String isAvailable = thisBook.isAvailable()? "true" : "false";
		String text = "ISBN: " + thisBook.getISBN() + "\nTitle: " + thisBook.getTitle() + "\nAvailable: " + isAvailable + "\n";
		bookTextArea.setText(text);
		//image
		String isdnAddress = "image/"+thisBook.getISBN()+".jpg";
		ImageIcon image = new ImageIcon(isdnAddress);
		imageLabel.setIcon(image);
		//imageLabel.setIcon(new ImageIcon("image/0132222205.jpg"));
		
		JPanel buttonPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		buttonPanel.add(borrowButton);
		buttonPanel.add(returnButton);
		buttonPanel.add(reserveButton);
		buttonPanel.add(queueButton);
		centerPanel.add(buttonPanel,BorderLayout.NORTH);
		centerPanel.add(imageLabel,BorderLayout.SOUTH);
		
		add(bookTextArea,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		add(promptArea,BorderLayout.SOUTH);
		
		if(thisBook.isAvailable() == true) {
			returnButton.setEnabled(false);
			reserveButton.setEnabled(false);
			queueButton.setEnabled(false);	
		}
		
		else
			borrowButton.setEnabled(false);
		
		borrowButton.addActionListener(event->borrow());
		reserveButton.addActionListener(event->reserve());
		queueButton.addActionListener(event->waitingQueue());
		returnButton.addActionListener(event->returnBook());
	}
	
	private void imageLabel(ImageIcon image) {
		// TODO Auto-generated method stub
		
	}

	public void borrow() {
		changeAllButtonState();
		thisBook.setAvailable(false);
		String text1 = "ISBN: " + thisBook.getISBN() + "\nTitle: " + thisBook.getTitle() + "\nAvailable: false\n";
		bookTextArea.setText(text1);
		String text2 = "The book is borrowed.";
		promptArea.setText(text2);
	}
	
	private void reserve() {
		String user = JOptionPane.showInputDialog(null,"What's your name?");
		MyQueue reservedQueue = thisBook.getReservedQueue();
		reservedQueue.enqueue(user);
		String text = "The book is reserved by " + user + ".";
		promptArea.setText(text);
	}
	
	private void waitingQueue() {
		MyQueue reservedQueue = thisBook.getReservedQueue();
		MyLinkedList reservedLinkedList = reservedQueue.getList();
		Iterator<String> iterator = reservedLinkedList.iterator();
		String text = "The waiting queue:\n";
		while(iterator.hasNext()) {
			text += iterator.next() + "\n";
		}
		promptArea.setText(text);
	}
	
	private void returnBook() {
		if(thisBook.getReservedQueue().getSize()==0) {
			thisBook.setAvailable(true);
			String text1 = "ISBN: " + thisBook.getISBN() + "\nTitle: " + thisBook.getTitle() + "\nAvailable: true\n";
			bookTextArea.setText(text1);
			String text2 = "The book is returned.";
			promptArea.setText(text2);
			changeAllButtonState();
		}else {
			MyQueue reservedQueue = thisBook.getReservedQueue();
			String nextUser = (String)reservedQueue.dequeue();
			String text2 = "The book is returned.\nThe book is now borrowed by " + nextUser + ".";
			promptArea.setText(text2);
		}
	}
	public void changeAllButtonState() {
		borrowButton.setEnabled(!borrowButton.isEnabled());
		returnButton.setEnabled(!returnButton.isEnabled());
		reserveButton.setEnabled(!reserveButton.isEnabled());
		queueButton.setEnabled(!queueButton.isEnabled());	
	}
	
}
