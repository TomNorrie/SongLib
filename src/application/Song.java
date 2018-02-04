package application;

public class Song implements Comparable<Song>{
	
	private String title;
	private String artist;
	private String album;
	private String year;

	public Song(String title, String artist, String album, String year) {
		this.setTitle(title);
		this.setArtist(artist);
		this.setAlbum(album);
		this.setYear(year);
	}
	
	@Override
	public String toString() {
		return String.format("%s by %s", this.title, this.artist);
	}

	@Override
	public int compareTo(Song s) {
		int titleComp = this.title.compareToIgnoreCase(s.getTitle());
		if (titleComp != 0) return titleComp;
		else {
			return this.artist.compareTo(s.getArtist());
		}
	}
	
	//Getters and Setters
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return this.artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return this.album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
}
