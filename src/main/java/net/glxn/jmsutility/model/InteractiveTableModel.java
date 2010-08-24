package net.glxn.jmsutility.model;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * InteractiveTableModel enables dynamic/interactive table input
 *
 * Borrowed and modified from a good article at http://www.javalobby.org/articles/jtable/
 * written by Lenming Yeung
 */
 public class InteractiveTableModel extends AbstractTableModel {
     public static final int NAME_INDEX = 0;
     public static final int VALUE_INDEX = 1;
     public static final int HIDDEN_INDEX = 2;

     protected String[] columnNames;
     protected Vector<JMSMessageProperty> dataVector;

     public InteractiveTableModel(String[] columnNames) {
         this.columnNames = columnNames;
         dataVector = new Vector<JMSMessageProperty>();
     }

     public String getColumnName(int column) {
         return columnNames[column];
     }

     public boolean isCellEditable(int row, int column) {
         return column != HIDDEN_INDEX;
     }

     public Class getColumnClass(int column) {
         switch (column) {
             case NAME_INDEX:
             case VALUE_INDEX:
                return String.class;
             default:
                return Object.class;
         }
     }

     public Object getValueAt(int row, int column) {
         JMSMessageProperty jmsMessageProperty = dataVector.get(row);
         switch (column) {
             case NAME_INDEX:
                return jmsMessageProperty.getName();
             case VALUE_INDEX:
                return jmsMessageProperty.getValue();
             default:
                return new Object();
         }
     }

     public void setValueAt(Object value, int row, int column) {
         JMSMessageProperty jmsMessageProperty = dataVector.get(row);
         switch (column) {
             case NAME_INDEX:
                jmsMessageProperty.setName((String)value);
                break;
             case VALUE_INDEX:
                jmsMessageProperty.setValue((String)value);
                break;
             default:
                System.out.println("invalid index");
         }
         fireTableCellUpdated(row, column);
     }

     public int getRowCount() {
         return dataVector.size();
     }

     public int getColumnCount() {
         return columnNames.length;
     }

     public boolean hasEmptyRow() {
         if (dataVector.size() == 0) return false;
         JMSMessageProperty jmsMessageProperty = dataVector.get(dataVector.size() - 1);
         return jmsMessageProperty.getName().trim().equals("") &&
                 jmsMessageProperty.getValue().trim().equals("");
     }

     public void addEmptyRow() {
         dataVector.add(new JMSMessageProperty());
         fireTableRowsInserted(
            dataVector.size() - 1,
            dataVector.size() - 1);
     }
 }