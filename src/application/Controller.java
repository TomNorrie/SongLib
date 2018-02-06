package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Controller implements Initializable {
	
//FXML Objects
	
	private BorderPane mainPane;
	
	//Main Data View
	@FXML private ListView<Song> listView;
	
	//Draggable Top Bar and Quit Button
	@FXML private GridPane topBar;
	@FXML private Button quitButton;
	
	//Song Groups
	@FXML private GridPane songLabels;
	@FXML private GridPane songFields;
	
	//Song Fields
	@FXML private TextField titleField;
	@FXML private TextField artistField;
	@FXML private TextField albumField;
	@FXML private TextField yearField;
	
	//Song Labels
	@FXML private Label titleLabel;
	@FXML private Label artistLabel;
	@FXML private Label albumLabel;
	@FXML private Label yearLabel;
	
	//Button Groups
	@FXML private HBox defaultButtons;
	@FXML private HBox editButtons;
	@FXML private HBox newButtons;
	@FXML private HBox deleteButtons;
	
	//Buttons
	@FXML private Button newButton;
	@FXML private Button editButton;
	@FXML private Button deleteButton;
	@FXML private Button confirmEditButton;
	@FXML private Button cancelEditButton;
	@FXML private Button addButton;
	@FXML private Button cancelAddButton;
	
//Instance Variables
	
	//name of the file in which data is saved/loaded from
	private String dataFilename = "data.txt";
	
	//The observable list in which all of our song data is stored
	private ObservableList<Song> data;
	
	//Vars for keeping track of window dragging
	private double dragDeltaX = 0;
	private double dragDeltaY = 0;
	
//Methods
	
	//Initialize called automatically when controller created
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sorter = new SongSorter();
		data = FXCollections.observableArrayList();
		
		load();
		
		//Set up listener for changing song selection to update sidebar
		listView.setItems(data);
		sort();
		listView.getSelectionModel().selectedItemProperty().addListener(
				(data, oldVal, newVal) ->
				refreshSidebar());
		defaultButtonsView();
		labelView();
		
		//Select first entry by default if it exists
		if (data.size() != 0) {
			listView.getSelectionModel().select(0);
		}	
		
		refreshSidebar();
	}
	
	public void refreshSidebar() {
		//get currently selected song
		Song song = getSelectedSong();
		
		if (data.size() == 0) {
			editButton.setDisable(true);
			deleteButton.setDisable(true);
		}
		else {
			editButton.setDisable(false);
			deleteButton.setDisable(false);
		}
		
		//if no song selected, then set labels to null and return
		//this will usually happen if the last song in the library is deleted
		if (song == null) {
			titleLabel.setText("");
			artistLabel.setText("");
			albumLabel.setText("");
			yearLabel.setText("");
			return;
		}
		
		//fill in sidebar
		titleLabel.setText(song.getTitle());
		artistLabel.setText(song.getArtist());
		albumLabel.setText(song.getAlbum());
		yearLabel.setText(song.getYear());
		
		if (!newButtons.isVisible()) {
			titleField.setText(song.getTitle());
			artistField.setText(song.getArtist());
			albumField.setText(song.getAlbum());
			yearField.setText(song.getYear());
		}
	}
	
	//Alphabetically sorts song data
	public void sort() {
		Collections.sort(data, (a,b) -> a.compareTo(b));
		}

	//Utility method that allows the window to be dragged by the top bar
	public void allowWindowDrag(Stage stage) {
		topBar.setOnMousePressed((MouseEvent event) -> {
			dragDeltaX = topBar.getLayoutX() - event.getSceneX();
			dragDeltaY = topBar.getLayoutY() - event.getSceneY();
		});
		topBar.setOnMouseDragged((MouseEvent event) -> {
			stage.setX(event.getScreenX() + dragDeltaX);
			stage.setY(event.getScreenY() + dragDeltaY);
		});
	}
	
	//Button group visibility methods
	
	public void defaultButtonsView() {
		defaultButtons.setVisible(true);
		editButtons.setVisible(false);
		newButtons.setVisible(false);
		deleteButtons.setVisible(false);
	}

		public void editButtonsView() {
		defaultButtons.setVisible(false);
		editButtons.setVisible(true);
		newButtons.setVisible(false);
		deleteButtons.setVisible(false);
	}

		public void newButtonsView() {
		defaultButtons.setVisible(false);
		editButtons.setVisible(false);
		newButtons.setVisible(true);
		deleteButtons.setVisible(false);
	}

		public void deleteButtonsView() {
		defaultButtons.setVisible(false);
		editButtons.setVisible(false);
		newButtons.setVisible(false);
		deleteButtons.setVisible(true);
	}
	
	//Button Methods
		
	public void newPress() {
		/*
		 * change button view to newButtonsView
		 * change to fieldsView
		 * make sure fields are empty
		 */
		
//		mainPane.setCenter(/* other fxml goes here? */)
		newButtonsView();
		fieldsView();
		resetFields();
	}
	
	public void editPress() {
		/*
		 * change button view to editButtonsView
		 * change to fieldsView
		 * fields should already be correct, but maybe refreshSidebar() for good measure.
		 */
		editButtonsView();
		fieldsView();
		refreshSidebar();
	}
	
	public void deletePress() {
		/*
		 * if nothing selected (then list is probably empty), do nothing
		 * otherwise, change to deleteButtonsView
		 * display confirmation prompt 
		 */
		if (data.size() == 0) {
			//TODO Prompt delete invalid in empty list
		}
		else {
			deleteButtonsView();
			//TODO Prompt delete confirmation.
		}
	}
	
	public void confirmEditPress() {
		/*
		 * if title empty, prompt title required, valid false
		 * if artist empty, prompt artist required, valid false
		 * if title/artist is a duplicate of NOT selected song, prompt duplicates forbidden, valid false
		 * if valid, save fields to song
		 * refresh listview
		 * go back to defaultButtonsView
		 * change to labelView
		 * sort list
		 * refreshSidebar to see changes
		 */
		boolean valid = true;
		
		if (titleEmpty()) {
			//TODO Prompt title required
			valid = false;
		}
		
		if (artistEmpty()) {
			//TODO Prompt artist required
			valid = false;
		}
		
		if (titleArtistChanged()) {
			if (isDuplicate()) {
				//TODO Prompt duplicates forbidden
				valid = false;
			}
		}
		
		if (valid) {
			Song song = getSelectedSong();
			song.setTitle(titleField.getText());
			song.setArtist(artistField.getText());
			song.setAlbum(albumField.getText());
			song.setYear(yearField.getText());
			
			defaultButtonsView();
			labelView();
			sort();
			refreshSidebar();
		}
	}
	
	public void cancelEditPress() {
		/*
		 * go back to defaultButtonsView
		 * change to labelView
		 * resetFields
		 */
		defaultButtonsView();
		labelView();
		resetFields();
	}
	
	public void addPress() {
		/*
		 * if title empty, prompt title required, valid false
		 * if artist empty, prompt artist required, valid false
		 * if title/artist is a duplicate, prompt duplicates forbidden, valid false
		 * if valid, construct new song and add to data
		 * go back to defaultButtonsView
		 * change to labelView
		 * sort
		 * make sure song is selected
		 */
		
		boolean valid = true;
		
		if (titleEmpty()) {
			//TODO Prompt title required	
			valid = false;
		}
		
		if (artistEmpty()) {
			//TODO Prompt artist required
			valid = false;
		}
		
		if (isDuplicate()) {
			//TODO Prompt duplicated forbidden
			valid = false;
		}
		
		if (valid) {
			Song newSong = new Song(
					titleField.getText(),
					artistField.getText(),
					albumField.getText(),
					yearField.getText());
			data.add(newSong);
			
			defaultButtonsView();
			labelView();
			sort();
			
			selectSong(newSong);
		}
	}
	
	public void cancelAddPress() {
		/*
		 * go back to defaultButtonsView
		 * change to labelView
		 * resetFields
		 */
		defaultButtonsView();
		labelView();
		resetFields();
	}
	
	public void confirmDeletePress() {
		/*
		 * find song's index
		 * remove song from data
		 * if index still in list, select index
		 * if out of range, select index-1
		 * if still out of range, select nothing
		 * go back to defaultButtonsView
		 */
		int i = getSelectedIndex();
		
		data.remove(i);
		
		if (i == data.size()) {
			if (data.size() != 0) {
				selectIndex(i-1);
			}
		}
		else {
			selectIndex(i);
		}
		defaultButtonsView();
	}
	
	public void cancelDeletePress() {
		/*
		 * go back to defaultButtonsView
		 * change to labelView
		 */
		defaultButtonsView();
		labelView();
	}
	
	public void quitPress() {
		save();
		((Stage) quitButton.getScene().getWindow()).hide();
	}
	
//Helper Methods
	
	private void selectSong(Song s) {
		listView.getSelectionModel().select(s);
	}
	
	private void selectIndex(int i) {
		listView.getSelectionModel().select(i);
	}
	
	//return true if either title or artist in field does not match label
	private boolean titleArtistChanged() {
		return !(titleField.getText().equalsIgnoreCase(titleLabel.getText())) || 
				!(artistField.getText().equalsIgnoreCase(artistLabel.getText()));
	}
	
	private boolean titleEmpty() {
		return titleField.getText().length()==0;
	}
	
	private boolean artistEmpty() {
		return artistField.getText().length()==0;
	}
	
	private boolean isDuplicate() {
		String title = titleField.getText();
		String artist = artistField.getText();
		for (Song song : data) {
			if (song.getTitle().equalsIgnoreCase(title)) {
				if (song.getArtist().equalsIgnoreCase(artist)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Song getSelectedSong() {
		return listView.getSelectionModel().getSelectedItem();
	}
	
	private int getSelectedIndex() {
		return listView.getSelectionModel().getSelectedIndex();
	}
	
	private void fieldsView() {
		songFields.setVisible(true);
		songLabels.setVisible(false);
	}
	
	private void labelView() {
		songFields.setVisible(false);
		songLabels.setVisible(true);
	}
	
	private void resetFields() {
		titleField.setText("");
		artistField.setText("");
		albumField.setText("");
		yearField.setText("");
	}
	
// Save/Load Data Methods
	
	//should do this with JSON.
	
	private void save() {
		JSONArray songs = new JSONArray();
		for (Song song : data) {
			songs.add(songToJson(song));
		}
		
		JSONObject obj = new JSONObject();
		obj.put("songs", songs);
		
		try {
			FileWriter writer =new FileWriter(dataFilename);
			writer.write(obj.toJSONString());
			writer.close();
		}
		catch (FileNotFoundException e) {
			createDataFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject songToJson(Song song) {
		JSONObject obj = new JSONObject();
		obj.put("title", song.getTitle());
		obj.put("artist", song.getArtist());
		obj.put("album", song.getAlbum());
		obj.put("year", song.getYear());
		return obj;
	}
	
	private void load() {
		JSONParser parser = new JSONParser();
		
		
		
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(dataFilename));
			JSONArray songs = (JSONArray) obj.get("songs");
			Iterator<JSONObject> iterator = songs.iterator();
			while (iterator.hasNext()) {
				obj = iterator.next();
				String title = (String) obj.get("title");
				String artist = (String) obj.get("artist");
				String album = (String) obj.get("album");
				String year = (String) obj.get("year");
				data.add(new Song(title, artist, album, year));
			}
		}
		catch (FileNotFoundException e) {
			//display welcome message?
			createDataFile();
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		catch (ParseException e) {
			System.out.println("ParseException");
			e.printStackTrace();
		}
	}
	
	private void createDataFile() {
		try {
			new File(dataFilename).createNewFile();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}


