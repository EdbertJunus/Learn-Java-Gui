import java.sql.*;
import java.text.SimpleDateFormat;

public class Connect {
	Connection connection;
	Statement statement;
	ResultSet resultSet;
	
	
	public Connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/notesapp","root","");
			
			statement = connection.createStatement();
			
			System.out.println("SUCCESS TO CONNECT TO DATABASE");
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("FAIL TO CONNECT TO DATABASE");
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		new Connect();
	}
	
	public ResultSet executeQuery(String query) {
		try {
			resultSet = statement.executeQuery(query);
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public void executeManipulationData(String query) {
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public ResultSet findAllNotes() {
		return executeQuery("SELECT * FROM notes");
	}
	
	public void insertNewNote(Note note) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String query = String.format("INSERT INTO NOTES (title, tag, body, createdAt, updatedAt)"
				+ "VALUES ('%s', '%s', '%s', '%s', '%s')", 
				note.getTitle(), note.getTag(), note.getBody(), formatter.format(note.getCreatedAt()), formatter.format(note.getUpdatedAt()));
		executeManipulationData(query);
	}
	
	public void updateNote(Note note) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String query = String.format("UPDATE NOTES SET title = '%s', tag = '%s', body = '%s', "
				+ "updatedAt = '%s' WHERE id = %d", 
				note.getTitle(), note.getTag(), note.getBody(), 
				formatter.format(note.getUpdatedAt()), note.getId());
		executeManipulationData(query);
	}
	
	public void deleteNote(int id) {
		String query = String.format("DELETE FROM NOTES WHERE id = %d", id);
		executeManipulationData(query);
	}
	
}	
