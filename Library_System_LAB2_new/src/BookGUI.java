//CHEN Yitong 20098897d, HAO Jiadong 20084595d
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.io.*;

public class BookGUI extends JFrame{
	//definitions of the components
	JTextArea bookTextArea = new JTextArea();
	JTable bookTable;
	DefaultTableModel model;
	
	JLabel ISBNLable = new JLabel("ISBN: ");
	JTextField ISBNTextField = new JTextField(10);
	JLabel titleLabel = new JLabel("Title: ");
	JTextField titleTextField = new JTextField(10);
 
	JButton addButton = new JButton("Add");
	JButton editButton = new JButton("Edit");
	JButton editSaveButton = new JButton("Save");
	JButton deleteButton = new JButton("Delete");
	JButton searchButton = new JButton("Search");
	JButton moreButton = new JButton("More>>");
	JButton testDataButton = new JButton("Load Test Data");
	JButton displayAllButton = new JButton("Display All");
	JButton sortISBNButton = new JButton("Display All by ISBN");
	JButton sortTitleButton = new JButton("Display All by Title");
	JButton exitButton = new JButton("Exit");
	
	//store the books in a linkedlist
	private MyLinkedList<Book> bookLinkedList = new MyLinkedList<Book>();
	//store the book index when clicking "More>>"
	private int editIndex;
	//Check whether to display the books in ascending order or descending order
	private boolean reverseISBN = false;
	private boolean reverseTitle = false;
	
	//constructor to add the components to the JFrame
	public BookGUI(){
		
		//set the upper part of the JFrame
		String text = "Student Name and ID: HAO Jiadong(20084595d)\n" + 
				"Student Name and ID: CHEN Yitong(20098897d)\n" + new Date().toString() + "\n\n";
		bookTextArea.setText(text);
		add(bookTextArea,BorderLayout.NORTH);

		//set the middle part of the JFrame
		String[] titles = {"ISBN","Title","Available"};
		String [][] data = {};
		//when the model change, the data on the table will automatically change
		model = new DefaultTableModel(data,titles);
		bookTable = new JTable(model);
		JScrollPane s = new JScrollPane(bookTable);
		add(s,BorderLayout.CENTER);
		
		//set the bottom part of the JFrame
		JPanel bottomRegion = new JPanel();
		bottomRegion.setLayout(new BorderLayout());
		JPanel inputLine = new JPanel();
		JPanel buttonLine1 = new JPanel();
		JPanel buttonLine2 = new JPanel();
		inputLine.add(ISBNLable);
		inputLine.add(ISBNTextField);
		inputLine.add(titleLabel);
		inputLine.add(titleTextField);
		
		buttonLine1.add(addButton);
		buttonLine1.add(editButton);
		buttonLine1.add(editSaveButton);
		editSaveButton.setEnabled(false);
		buttonLine1.add(deleteButton);
		buttonLine1.add(searchButton);
		buttonLine1.add(moreButton);
		
		buttonLine2.add(testDataButton);
		buttonLine2.add(displayAllButton);
		buttonLine2.add(sortISBNButton);
		buttonLine2.add(sortTitleButton);
		buttonLine2.add(exitButton);
		
		bottomRegion.add(inputLine,BorderLayout.NORTH);
		bottomRegion.add(buttonLine1,BorderLayout.CENTER);
		bottomRegion.add(buttonLine2,BorderLayout.SOUTH);
		add(bottomRegion,BorderLayout.SOUTH);
		
		//all the event handlers
		addButton.addActionListener(event->addBook(titleTextField.getText(),ISBNTextField.getText()));
		testDataButton.addActionListener(event->loadTestData());
		bookTable.getSelectionModel().addListSelectionListener(event->selectRow());
		editButton.addActionListener(event->editBook(ISBNTextField.getText()));
		editSaveButton.addActionListener(event->editSaveBook(titleTextField.getText(),ISBNTextField.getText()));
		deleteButton.addActionListener(event->deleteBook(ISBNTextField.getText()));	
		displayAllButton.addActionListener(event->displayAll());
		searchButton.addActionListener(event->searchBook(titleTextField.getText(),ISBNTextField.getText()));
		exitButton.addActionListener(event->exit());
		sortISBNButton.addActionListener(event->sortByISBN());
		sortTitleButton.addActionListener(event->sortByTitle());
		moreButton.addActionListener(event->more(ISBNTextField.getText()));
		//when opening a new window, loading the data file. when closing a new window, storing the linkedlist to the data file.
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				readData();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				writeData();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
		});
	}
	
	private void addBook(String thisTitle, String thisISBN) {
		//when adding a new book, neither field could be empty
		if (isFieldEmpty(thisISBN)) {
			JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank!");
			return;
		}
			
		else if(isFieldEmpty(thisTitle)) {
			JOptionPane.showMessageDialog(null, "Error: title cannot be blank!");
			return;
		}
			
		else {
			//check whether the book exists in the linkedlist 
			int index = findBookByISBN(thisISBN);
			if(index != -1) {
				JOptionPane.showMessageDialog(null, "Error: book ISBN" + thisISBN + " is already in the database!");
				return;
			}
				
			else {
				//add a book to the linked list
				Book thisBook = new Book();
				thisBook.setISBN(thisISBN);
				thisBook.setTitle(thisTitle);
				thisBook.setAvailable(true);
				bookLinkedList.addLast(thisBook);
				ISBNTextField.setText("");
				titleTextField.setText("");
				displayAll();
			}
		}
	}
	
	//load test data using the addBook function
	private void loadTestData() {
		addBook("HTML How to Program","0131450913");
		addBook("C++ How to Program","0131857576");
		addBook("Java How to Program","0132222205");
	}
	
	
	//when the user choose one record, set the two fields
	private void selectRow() {
		if(bookTable.getSelectedRow()>=0) {
			int row = bookTable.getSelectedRow();
			ISBNTextField.setText((String)bookTable.getValueAt(row, 0));
			titleTextField.setText((String)bookTable.getValueAt(row, 1));
		}
		
	}
	
	private void editBook(String thisISBN) {
		//check whether the ISBN field is empty
		if(isFieldEmpty(thisISBN)){	
			JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank!");
			return;
		}
		//check whether that ISBN exists in the database
		int row = findBookByISBNOnTable(thisISBN);
		if(row == -1) {
			JOptionPane.showMessageDialog(null, "Error: book ISBN" + thisISBN + " is not in the database!");
			return;
		}
			
		else {
			//if the text field is empty, fill it
			titleTextField.setText((String)bookTable.getValueAt(row, 1));
			// compulsorily select the row with the specified ISBN for user
			bookTable.getSelectionModel().setSelectionInterval(row,row);
			//disable all button
			changeAllButtonState();
		}
	}
	
	private void editSaveBook(String thisTitle, String thisISBN) {
		//get the ISBN of the book the user trying to edit on
		int row = bookTable.getSelectedRow();
		String selectedISBN =(String)bookTable.getValueAt(row, 0);
		//no input fields of a edited book can be empty
		if(isFieldEmpty(thisISBN)) {
			JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank!");
			return;
		}
			
		if(isFieldEmpty(thisTitle)) {
			JOptionPane.showMessageDialog(null, "Error: title cannot be blank!");
			return;
		}
			
		//if the user change the ISBN of a selected book and the modified ISBN has already been in the database for another book 
		if(!thisISBN.equals(selectedISBN) && findBookByISBN(thisISBN) !=-1) {
			JOptionPane.showMessageDialog(null, "Error: book ISBN" + thisISBN + " is already in the database!");
			return;
		}
		else {
			//the user remain the ISBN or change it to a valid one, with either changing the title or not
			int index = findBookByISBN(selectedISBN);
			Book thisBook = bookLinkedList.get(index);
			thisBook.setTitle(thisTitle);
			thisBook.setISBN(thisISBN);
			ISBNTextField.setText("");
			titleTextField.setText("");
			displayAll();
			changeAllButtonState();
		}
	}
	
	//delete a book
	private void deleteBook(String thisISBN) {
		if(isFieldEmpty(thisISBN)) {
			JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank!");
			return;
		}
			
		int index = findBookByISBN(thisISBN);
		if(index == -1) {
			JOptionPane.showMessageDialog(null, "Error: book ISBN" + thisISBN + " is not in the database!");
			return;
		}
			
		else {
			bookLinkedList.remove(index);
			displayAll();
			ISBNTextField.setText(null);
			titleTextField.setText(null);
			
		}		
	}
	
	//search books
	private void searchBook(String thisTitle, String thisISBN) {
		Iterator<Book> iterator = bookLinkedList.iterator();
		//clear the current JTable
		model.setRowCount(0);
		//check whether there is such a book or books
		boolean recordsFound = false;
		while(iterator.hasNext()) {
			Book currentBook = iterator.next();
			//if there is a match, display it immediately on the JTable
			if(!thisTitle.equals("")&&!thisISBN.equals("")) {
				if(currentBook.getISBN().contains(thisISBN)||currentBook.getTitle().contains(thisTitle)) {
					displayARecord(currentBook);
					recordsFound =true;
				}
			}else if(thisTitle.equals("")&&!thisISBN.equals("")) {
				if(currentBook.getISBN().contains(thisISBN)) {
					displayARecord(currentBook);
					recordsFound =true;
				}
			}else if(!thisTitle.equals("")&&thisISBN.equals("")) {
				if(currentBook.getTitle().contains(thisTitle)) {
					displayARecord(currentBook);
					recordsFound =true;
				}
			}
			
		}
		if(recordsFound == false)
			JOptionPane.showMessageDialog(null, "Error: No such book are found!");
		ISBNTextField.setText(null);
		titleTextField.setText(null);
	}
	
	//display all the records in the linkedlist
	private void displayAll() {
		model.setRowCount(0);
		Iterator<Book> iterator = bookLinkedList.iterator();
		while(iterator.hasNext()) {
			Book currentBook = iterator.next();
			displayARecord(currentBook);
		}
	}
	
	//sort by ISBN
		private void sortByISBN() {
			Book [] bookArray = new Book[bookLinkedList.size()];
			bookLinkedList.toArray(bookArray);
			
			if(!reverseISBN) {
				Arrays.sort(bookArray, Comparator.comparing(Book::getISBN));
				displayArray(bookArray);
			}
				
			else {
				Arrays.sort(bookArray, Comparator.comparing(Book:: getISBN).reversed());
				displayArray(bookArray);
			}
			reverseISBN = !reverseISBN;
		}
		
		//sort by Title
		private void sortByTitle() {
			Book [] bookArray = new Book[bookLinkedList.size()];
			bookLinkedList.toArray(bookArray);
			
			if(!reverseTitle) {
				Arrays.sort(bookArray, Comparator.comparing(Book::getTitle));
				displayArray(bookArray);
			}
				
			else {
				Arrays.sort(bookArray, Comparator.comparing(Book:: getTitle).reversed());
				displayArray(bookArray);
			}
			reverseTitle = !reverseTitle;
		}
		
	
	//find a book in the database with a specific ISBN
	private int findBookByISBN(String ISBN) {
		Iterator<Book> iterator = bookLinkedList.iterator();
		int now = 0;
		while(iterator.hasNext()) {
			Book currentBook = iterator.next();
			String currentISBN = currentBook.getISBN();
			if(currentISBN.equals(ISBN))
				return now;
			now++;
		}
		return -1;//if not found, return -1;
	}
	//find a book on the JTable with a specific ISBN
	private int findBookByISBNOnTable(String ISBN) {
		int numOfRows = bookTable.getRowCount();
		for(int i=0 ; i<=numOfRows-1; i++)
		{
			String currentISBN = (String) bookTable.getValueAt(i, 0);
			if(currentISBN.equals(ISBN))
				return i;
		}
		return -1;
	}
	
	//switch all the buttons' states
	private void changeAllButtonState() {
		changeButtonState(editSaveButton);
		changeButtonState(addButton);
		changeButtonState(editButton);
		changeButtonState(deleteButton);
		changeButtonState(searchButton);
		changeButtonState(moreButton);
		changeButtonState(testDataButton);
		changeButtonState(displayAllButton);
		changeButtonState(sortISBNButton);
		changeButtonState(sortTitleButton);
		changeButtonState(exitButton);
	}
	
	private void changeButtonState(JButton button) {
		button.setEnabled(!button.isEnabled());
	}
	
	//check whether an input field is empty
	private boolean isFieldEmpty(String thisField) {
		return thisField.trim().equals("");
	}
	
	//display a specific book on the JTable
	private void displayARecord(Book currentBook) {
		String currentISBN = currentBook.getISBN();
		String currentTitle = currentBook.getTitle();
		String isAvailable = currentBook.isAvailable()? "true":"false";
		String [] insert = {currentISBN,currentTitle,isAvailable};
		model.addRow(insert);
	}
	
	//exit the program
	private void exit() {
		writeData();
		System.exit(0);
	}
	
	//display the sort results on the JTable
	private void displayArray(Book [] thisArray){
		model.setRowCount(0);
		for(int i=0;i<=thisArray.length-1;i++) {
			Book currentBook = thisArray[i];
			String currentISBN = currentBook.getISBN();
			String currentTitle = currentBook.getTitle();
			String isAvailable = currentBook.isAvailable()? "true":"false";
			String [] insert = {currentISBN,currentTitle,isAvailable};
			model.addRow(insert);
		}
		
	}
	
	private void more(String thisISBN) {
		if(isFieldEmpty(thisISBN)) {
			JOptionPane.showMessageDialog(null, "Error: ISBN can not be blank!");
			return;
		}
			
		else {
			editIndex = bookTable.getSelectedRow();
			String thisTitle = (String)model.getValueAt(editIndex, 1);
			String isAvailable = (String)model.getValueAt(editIndex, 2);
			int index = findBookByISBN(thisISBN);
			Book thisBook = bookLinkedList.get(index);
			Dialog dialog = new Dialog(this,thisBook);
			dialog.setSize(500,400);
			dialog.setTitle(thisTitle);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
	
	}
	
	private void createFile() {
		try {
			File file = new File("BookInformation.txt");
			if(file.createNewFile()) {
				System.out.println("The file has been created.");
			}else {
				System.out.println("File already exists.");
			}
		}
		catch(IOException e) {
			System.out.println("Error when creating the file.");
			e.printStackTrace();
		}
	}
	
	private void writeData() {
		createFile();
		try {
			FileWriter myWriter = new FileWriter("BookInformation.txt",false);
			myWriter.write("");
			myWriter.close();
		}catch(IOException e){
			System.out.println("An error when clearing the file.");
			e.printStackTrace();
		}
		Iterator<Book> iterator = bookLinkedList.iterator();
		while(iterator.hasNext()) {
			Book thisBook = iterator.next();
			try {
				FileWriter myWriter = new FileWriter("BookInformation.txt",true);
				String isAvailable = thisBook.isAvailable()? "true" :"false";
				myWriter.write(thisBook.getISBN() + "--" + thisBook.getTitle() + "--" + isAvailable+"\n");
				myWriter.close();
				System.out.println("Successfully insert one record into the file");
			}catch(IOException e)
			{
				System.out.println("An error when writing to the file.");
				e.printStackTrace();
			}
		}	
	}
	
	private void readData() {
		try {
			File myFile = new File("BookInformation.txt");
			Scanner myReader = new Scanner(myFile);
			while(myReader.hasNextLine()) {
				String record = myReader.nextLine();
				String[] data = record.split("--");
				if(data.length == 3) {
					Book thisBook =new Book();
					boolean isAvailable = data[2].equals("true")? true:false;
					thisBook.setISBN(data[0]);
					thisBook.setTitle(data[1]);
					thisBook.setAvailable(isAvailable);
					bookLinkedList.addLast(thisBook);
					displayAll();
				}
					
			}
			myReader.close();
		}catch(FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BookGUI frame = new BookGUI();
		frame.setTitle("Library Admin System");
		frame.setSize(700,400);
		frame.setVisible(true);
		//display the window in the center of the screen
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

}
