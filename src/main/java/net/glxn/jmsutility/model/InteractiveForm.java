package net.glxn.jmsutility.model;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * InteractiveForm enables dynamic/interactive table input
 *
 * Borrowed and modified from a good article at http://www.javalobby.org/articles/jtable/
 * written by Lenming Yeung
 */
 public class InteractiveForm extends JPanel {
     public static final String[] columnNames = {
         "Key", "Value", ""
     };

     protected JTable table;
     protected JScrollPane scroller;
     protected InteractiveTableModel tableModel;

     public InteractiveForm() {
         initComponent();
     }

     @SuppressWarnings({"UndesirableClassUsage"})
     public void initComponent() {
         tableModel = new InteractiveTableModel(columnNames);
         tableModel.addTableModelListener(new InteractiveForm.InteractiveTableModelListener());
         table = new JTable();
         table.setModel(tableModel);
         table.setSurrendersFocusOnKeystroke(true);
         if (!tableModel.hasEmptyRow()) {
             tableModel.addEmptyRow();
         }

         scroller = new javax.swing.JScrollPane(table);
         scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
         table.setPreferredScrollableViewportSize(new java.awt.Dimension(300, 100));
         TableColumn hidden = table.getColumnModel().getColumn(InteractiveTableModel.HIDDEN_INDEX);
         hidden.setMinWidth(2);
         hidden.setPreferredWidth(2);
         hidden.setMaxWidth(2);
         hidden.setCellRenderer(new InteractiveRenderer(InteractiveTableModel.HIDDEN_INDEX));

         setLayout(new BorderLayout());
         add(scroller, BorderLayout.CENTER);
     }

     public void highlightLastRow(int row) {
         int lastrow = tableModel.getRowCount();
         if (row == lastrow - 1) {
             table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
         } else {
             table.setRowSelectionInterval(row + 1, row + 1);
         }

         table.setColumnSelectionInterval(0, 0);
     }

     class InteractiveRenderer extends DefaultTableCellRenderer {
         protected int interactiveColumn;

         public InteractiveRenderer(int interactiveColumn) {
             this.interactiveColumn = interactiveColumn;
         }

         public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column)
         {
             Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
             if (column == interactiveColumn && hasFocus) {
                 if ((InteractiveForm.this.tableModel.getRowCount() - 1) == row &&
                    !InteractiveForm.this.tableModel.hasEmptyRow())
                 {
                     InteractiveForm.this.tableModel.addEmptyRow();
                 }

                 highlightLastRow(row);
             }

             return c;
         }
     }

     public class InteractiveTableModelListener implements TableModelListener {
         public void tableChanged(TableModelEvent evt) {
             if (evt.getType() == TableModelEvent.UPDATE) {
                 int column = evt.getColumn();
                 int row = evt.getFirstRow();
                 System.out.println("row: " + row + " column: " + column);
                 table.setColumnSelectionInterval(column + 1, column + 1);
                 table.setRowSelectionInterval(row, row);
             }
         }
     }
 }
