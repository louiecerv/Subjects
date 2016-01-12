
/**
 * Write a description of class Subjects here.
 * 
 * @author Louie F. Cervantes
 * @version mm/dd/2015
 */
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import static java.awt.GridBagConstraints.*;
import java.sql.*;

public class Subjects
{
    private JPanel mainPanel, topPanel, midPanel, bottomPanel;
    private JList<String> lstSubjects;
    private DefaultListModel<String> listModel;
    private JTextField txtCode, txtSubject, txtUnits;
    private JButton btnAdd, btnEdit, btnDelete;

    private String dbName = "subjectsDB";
    private String connectionURL = "jdbc:derby:" + dbName + ";create=TRUE";
    private Connection connection;
    private Statement statement;
    private ResultSet rs;
    
    public Subjects()
    {
        connectDB();
        listModel = new DefaultListModel<String>();
        lstSubjects = new JList<String>(listModel);
        loadSubjects();
        buildGUI();
    }
    
    private void addComponent(Container container, Component component,
        int gridX, int gridY, int gridWidth, int gridHeight, int anchor, int fill)
    {
        Insets insets = new Insets(2, 2, 2, 2);
        GridBagConstraints gbc = new GridBagConstraints(gridX, gridY, gridWidth,
            gridHeight, 1.0, 1.0, anchor, fill, insets, 0, 0);
        container.add(component, gbc);
    }
        
    private void buildGUI()
    {
        JFrame frame = new JFrame("Subjects");
        JLabel lblSubjects = new JLabel("Subjects");
        mainPanel = new JPanel(new GridBagLayout());
        topPanel = new JPanel(new GridBagLayout());
        midPanel = new JPanel(new GridBagLayout());
        bottomPanel = new JPanel(new GridBagLayout());
        
        
        lstSubjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSubjects.addListSelectionListener(
            new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent evt)
                {
                    if (evt.getValueIsAdjusting()==false)
                    {
                        if (lstSubjects.getSelectedIndex() != -1)
                        {
                            showSubjectInfo();
                        }
                    }
                }
            }       
        );
        JScrollPane scrollPane = new JScrollPane(lstSubjects, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            
        addComponent(topPanel, lblSubjects, 0, 0, 1, 1, BASELINE_LEADING, NONE);
        addComponent(topPanel, scrollPane, 0, 1, 1, 1, BASELINE_LEADING, NONE);
        
        JLabel lblCode = new JLabel("Code");
        JLabel lblSubject = new JLabel("Subject");
        JLabel lblUnits = new JLabel("Units");
        
        txtCode = new JTextField(15);
        txtSubject = new JTextField(30);
        txtUnits = new JTextField(5);
        
        addComponent(midPanel, lblCode, 0, 0, 1, 1, BASELINE_LEADING, NONE);
        addComponent(midPanel, txtCode, 1, 0, 1, 1, BASELINE_LEADING, NONE);
        addComponent(midPanel, lblSubject, 0, 1, 1, 1, BASELINE_LEADING, NONE);
        addComponent(midPanel, txtSubject, 1, 1, 1, 1, BASELINE_LEADING, NONE);
        addComponent(midPanel, lblUnits, 0, 2, 1, 1, BASELINE_LEADING, NONE);
        addComponent(midPanel, txtUnits, 1, 2, 1, 1, BASELINE_LEADING, NONE);

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        
        addComponent(bottomPanel, btnAdd, 0, 0, 1, 1, BASELINE_LEADING, NONE);
        addComponent(bottomPanel, btnEdit, 1, 0, 1, 1, BASELINE_LEADING, NONE);
        addComponent(bottomPanel, btnDelete, 2, 0, 1, 1, BASELINE_LEADING, NONE);
                
        addComponent(mainPanel, topPanel, 0, 0, 1, 1, BASELINE_LEADING, NONE);
        addComponent(mainPanel, midPanel, 0, 1, 1, 1, CENTER, BOTH);
        addComponent(mainPanel, bottomPanel, 0, 2, 1, 1, BASELINE_TRAILING, NONE);
        
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        btnAdd.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    if (btnAdd.getText().equals("Add"))
                    {
                        btnAdd.setText("Save");
                        btnEdit.setText("Cancel");
                        btnDelete.setVisible(false);
                        lstSubjects.setEnabled(false);
                        
                        txtCode.setText("");
                        txtSubject.setText("");
                        txtUnits.setText("");
                        
                    } else if (btnAdd.getText().equals("Save"))
                    {
                        btnAdd.setText("Add");
                        btnEdit.setText("Edit");
                        btnDelete.setVisible(true);
                        lstSubjects.setEnabled(true);
                        
                        //save new record
                        doInsertNewRecord();
                        listModel.clear();
                        loadSubjects();
                        
                    }
                }
            }
        );
        
        btnEdit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (lstSubjects.getSelectedIndex()<0)
                    {
                        JOptionPane.showMessageDialog(null, "Select an item in the list to edit.");
                        return;
                    }
                    
                    if (btnEdit.getText().equals("Cancel"))
                    {
                        btnAdd.setText("Add");
                        btnEdit.setText("Edit");
                        btnDelete.setVisible(true);
                        lstSubjects.setEnabled(true);                    
                    } else if (btnEdit.getText().equals("Edit"))
                    {
                        btnAdd.setVisible(false);
                        btnEdit.setText("Save");
                        btnDelete.setText("Cancel");
                        lstSubjects.setEnabled(false);                        
                    } else if(btnEdit.getText().equals("Save")) 
                    {
                        btnAdd.setVisible(true);
                        btnEdit.setText("Edit");
                        btnDelete.setText("Delete");
                        lstSubjects.setEnabled(true);                                                

                        //save changes
                        doSaveEdits();
                        listModel.clear();
                        loadSubjects();
                        
                    }
                }
            }
        );
        
        btnDelete.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    if (btnDelete.getText().equals("Cancel"))
                    {
                        btnAdd.setVisible(true);
                        btnEdit.setText("Edit");
                        btnDelete.setText("Delete");
                        lstSubjects.setEnabled(true);                        
                    } else if (btnDelete.getText().equals("Delete")) {
                        
                        if (lstSubjects.getSelectedIndex() <0)
                        {
                            JOptionPane.showMessageDialog(null, 
                            "No item is selected. Select the subject from \n" +
                            "the list.");
                            return;
                        }
                        
                        String message = "Delete the selected record?";
                        String title = "Confirm delete";
                        
                        int reply = JOptionPane.showConfirmDialog(
                            null, message, title, JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION)
                        {
                            //delete record
                            doDeleteRecord();
                            listModel.clear();
                            loadSubjects();
                        }                        
                    }
                }
            }
        
        );
    }
    
    private void connectDB()
    {
        
        try
        {
            connection = DriverManager.getConnection(connectionURL);
        } catch (SQLException ex) {
            displaySQLErrors(ex);
        }
    }
    
    private void loadSubjects()
    {                
        
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery("SELECT * FROM Subjects");
            
            while(rs.next())
            {
                listModel.addElement(rs.getString("code"));
            }
        } catch (SQLException ex) {
            displaySQLErrors(ex);
        }
        
     }
    
    private void doInsertNewRecord()
    {
        try
        {
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate("INSERT INTO Subjects (code, subject, units) " + 
                "VALUES ('" + txtCode.getText() + "', '" + 
                txtSubject.getText() + "', " + 
                txtUnits.getText() + ")");
        } catch (SQLException ex){
            displaySQLErrors(ex);
        }
    }
    
    private void doSaveEdits()
    {
        try {
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate("UPDATE subjects " +
            "SET code = '" + txtCode.getText() + "', " +
            "subject = '" + txtSubject.getText() + "', " +
            "units = " + txtUnits.getText() + " WHERE code ='" + 
            lstSubjects.getSelectedValue() + "'");
            
        } catch (SQLException ex)
        {
            displaySQLErrors(ex);
        }
    }
    
    private void doDeleteRecord()
    {
        try {
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate(
                "DELETE FROM Subjects where code = '" + 
                lstSubjects.getSelectedValue() + "'");                
            
        } catch (SQLException ex) {
            displaySQLErrors(ex);
        }
    }
    
    private void showSubjectInfo()
    {
        try
        {
            rs.beforeFirst();
            while(rs.next())
            {
                if (rs.getString("code").equals(
                    lstSubjects.getSelectedValue()))
                    break;
            }
            
            if (!rs.isAfterLast())
            {
                txtCode.setText(rs.getString("code"));
                txtSubject.setText(rs.getString("subject"));
                txtUnits.setText(rs.getDouble("units") + "");
            }
            
        } catch (SQLException ex) {
            displaySQLErrors(ex);
        }        
    }
    
    
    private void displaySQLErrors(SQLException ex)
    {
        String errorText = "SQL Exception: " + ex.getMessage() + "\n" +
                "SQL state: " + ex.getSQLState() + "\n" + 
                "Vendor Error: " + ex.getErrorCode();
        JOptionPane.showMessageDialog(null, errorText);
    }
    
    
    
    public static void main(String[] args)
    {
        Subjects app = new Subjects();

    }
    
}
