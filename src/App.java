import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame implements MouseListener, Action{
	
	JLabel labelId, labelTitle, labelTags, labelBody, labelCreate, labelUpdate;
	JButton buttonInsert, buttonUpdate, buttonDelete;
	JRadioButton radioImportant, radioBasic;
	ButtonGroup groupTags;
	JTextField textId, textTitle, textTags, textBody, textCreate, textUpdate;
	JScrollPane scrollPaneNotes;
	JTable tableNotes;
	DefaultTableModel tableModel;
	Vector<Object> tableRow, tableColumn;
	JPanel panelTable, panelForm, panelTags, panelButton;
	ArrayList<Note> listNotes;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	
	public App() {
		initComponent();
		initFrame();
	}
	
	private void initFrame() {
		setTitle("NOTES APP");
		setSize(800, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
		
		add(panelTable, BorderLayout.CENTER);
		add(panelButton, BorderLayout.NORTH);
		
	}
	
	private void initComponent() {
		labelId = new JLabel("ID");
		labelTitle = new JLabel("Title");
		labelTags = new JLabel("Tags");
		labelBody = new JLabel("Body");
		labelCreate = new JLabel("Created At");
		labelUpdate = new JLabel("Updated At");
		
		textId = new JTextField();
		textTitle = new JTextField();
		textTags = new JTextField();
		textBody = new JTextField();
		textCreate = new JTextField();
		textUpdate = new JTextField();
		
		textId.setEditable(false);
		textCreate.setEditable(false);
		textUpdate.setEditable(false);
		
		radioImportant = new JRadioButton("Important");
		radioBasic = new JRadioButton("Basic");
		
		radioImportant.setActionCommand("Imporant");
		radioBasic.setActionCommand("Basic");
		
		groupTags = new ButtonGroup();
		groupTags.add(radioImportant);
		groupTags.add(radioBasic);
		
		buttonInsert = new JButton("Insert");
		buttonUpdate = new JButton("Update");
		buttonDelete = new JButton("Delete");
		
		tableNotes = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		scrollPaneNotes = new JScrollPane(tableNotes);
		
		panelTags = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelTags.add(radioBasic);
		panelTags.add(radioImportant);
		
		panelForm = new JPanel(new GridLayout(6,2));
		panelForm.add(labelId);
		panelForm.add(textId);
		
		panelForm.add(labelTitle);
		panelForm.add(textTitle);
		
		panelForm.add(labelTags);
		panelForm.add(panelTags);
		
		panelForm.add(labelBody);
		panelForm.add(textBody);
		
		panelForm.add(labelCreate);
		panelForm.add(textCreate);
		
		panelForm.add(labelUpdate);
		panelForm.add(textUpdate);
		
		panelButton = new JPanel(new GridLayout(1, 3));
		panelButton.add(buttonInsert);
		panelButton.add(buttonUpdate);
		panelButton.add(buttonDelete);
		
		panelTable = new JPanel(new GridLayout(2,1));
		panelTable.add(panelForm);
		panelTable.add(scrollPaneNotes);
		
		refreshTable();
		
		tableNotes.addMouseListener(this);
		
		buttonInsert.addActionListener(this);
		buttonUpdate.addActionListener(this);
		buttonDelete.addActionListener(this);
	}
	
	private void refreshTable() {
		tableColumn = new Vector<Object>();
		tableColumn.add("ID");
		tableColumn.add("Name");
		tableColumn.add("Tags");
		tableColumn.add("Body");
		tableColumn.add("Created At");
		tableColumn.add("Updated At");
		
		tableModel = new DefaultTableModel(tableColumn, 0);
		
		ResultSet result = new Connect().findAllNotes();
		try {
			listNotes = new ArrayList<Note>();
			while(result.next()) {
				Note note = new Note();
				note.setId(result.getInt("id"));
				note.setTitle(result.getString("title"));
				note.setTag(result.getString("tag"));
				note.setBody(result.getString("body"));
				note.setCreatedAt(result.getTimestamp("createdAt"));
				note.setUpdatedAt(result.getTimestamp("updatedAt"));
				listNotes.add(note);
				
			}
			for(Note note : listNotes) {
				tableRow = new Vector<Object>();
				tableRow.add(note.getId());
				tableRow.add(note.getTitle());
				tableRow.add(note.getTag());
				tableRow.add(note.getBody());
				tableRow.add(formatter.format(note.getCreatedAt()));
				tableRow.add(formatter.format(note.getUpdatedAt()));
				
				tableModel.addRow(tableRow);
			}
			tableNotes.setModel(tableModel);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new App();
	}
	
	private void doInsertData() {
		if(textTitle.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "Title must be filled");		
		}else if (groupTags.getSelection() == null) {
			JOptionPane.showMessageDialog(this, "Tags must be chosen");
		}else if (textBody.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "Body must be filled");
		}else {
			if (!textId.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "This data already exist, please update this data");
			}else {
				Note note = setNoteData(0);
				new Connect().insertNewNote(note);
				JOptionPane.showMessageDialog(this, "SUCCESSFUL");
				refreshTable();
				resetField();
			}
		}
	}
	
	private Note setNoteData(int update) {
		Note note = new Note();
		
		if(!textId.getText().equals("")) {
			note.setId(Integer.parseInt(textId.getText()));
		}
		note.setTitle(textTitle.getText());
		note.setTag(groupTags.getSelection().getActionCommand());
		note.setBody(textBody.getText());
		long millis = System.currentTimeMillis();
		Date date = new Date(millis); 
		if(update == 0) {
			note.setCreatedAt(date);
		}
		note.setUpdatedAt(date);
		return note;
	}
	
	
	private void resetField() {
		textId.setText("");
		textTitle.setText("");
		groupTags.clearSelection();
		textBody.setText("");
		textCreate.setText("");
		textUpdate.setText("");
	}
	
	private void doUpdateData() {
		if(textId.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "Please insert this data first");
		}else {
			Note note = setNoteData(1);
			new Connect().updateNote(note);
			JOptionPane.showMessageDialog(this, "SUCCESSFULLY TO UPDATE DATA...");
			refreshTable();
			resetField();
		}
	}
	
	private void doDeleteData() {
		if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this data? ", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			new Connect().deleteNote(Integer.parseInt(textId.getText()));
			JOptionPane.showMessageDialog(this, "SUCCESSFULLY TO DELETE DATA...");
			refreshTable();
			resetField();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == buttonInsert) {
			doInsertData();
		} else if (arg0.getSource() == buttonUpdate) {
			doUpdateData();
		} else if (arg0.getSource() == buttonDelete) {
			doDeleteData();
		}
	}

	@Override
	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putValue(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		int row = tableNotes.getSelectedRow();
		if (row >= 0) {
			textId.setText(String.valueOf(listNotes.get(row).getId()));
			textTitle.setText(String.valueOf(listNotes.get(row).getTitle()));
			if (listNotes.get(row).getTag().equals("Basic")) {
				radioBasic.setSelected(true);
			}else {
				radioImportant.setSelected(true);
			}
			textBody.setText(String.valueOf(listNotes.get(row).getBody()));
			textCreate.setText(String.valueOf(listNotes.get(row).getCreatedAt()));
			textUpdate.setText(String.valueOf(listNotes.get(row).getUpdatedAt()));
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
