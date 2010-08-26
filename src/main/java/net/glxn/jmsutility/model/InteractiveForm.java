package net.glxn.jmsutility.model;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * InteractiveForm enables dynamic/interactive table input
 * <p/>
 * Borrowed and modified from a good article at http://www.javalobby.org/articles/jtable/
 * written by Lenming Yeung
 */
public class InteractiveForm extends JPanel {
    static final String[] columnNames = {
            "Key", "Value", ""
    };

    private JTable table;
    private InteractiveTableModel tableModel;

    public InteractiveForm() {
        initComponent();
    }

    @SuppressWarnings({"UndesirableClassUsage"})
    void initComponent() {
        tableModel = new InteractiveTableModel();
        tableModel.addTableModelListener(new InteractiveForm.InteractiveTableModelListener());
        table = new JTable();
        table.setModel(tableModel);
        table.setSurrendersFocusOnKeystroke(true);
        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }

        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        table.setPreferredScrollableViewportSize(new java.awt.Dimension(300, 100));
        TableColumn hidden = table.getColumnModel().getColumn(InteractiveTableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer());

        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);
    }

    void highlightLastRow(int row) {
        int lastrow = tableModel.getRowCount();
        if (row == lastrow - 1) {
            table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
        } else {
            table.setRowSelectionInterval(row + 1, row + 1);
        }

        table.setColumnSelectionInterval(0, 0);
    }

    public void clear() {
        tableModel.dataVector = new Vector<JMSMessageProperty>();
        tableModel.addEmptyRow();
    }

    public Map<String, Object> getMessageProperties() {
        HashMap<String, Object> messagePropertiesHashMap = new HashMap<String, Object>();

        Enumeration<JMSMessageProperty> jmsMessagePropertyEnumeration = tableModel.dataVector.elements();
        while (jmsMessagePropertyEnumeration.hasMoreElements()) {
            JMSMessageProperty jmsMessageProperty = jmsMessagePropertyEnumeration.nextElement();
            if (jmsMessageProperty.getName() == null || "".equals(jmsMessageProperty.getName())) {
                continue;
            }
            messagePropertiesHashMap.put(jmsMessageProperty.getName(), jmsMessageProperty.getValue());
        }
        return messagePropertiesHashMap;
    }

    class InteractiveRenderer extends DefaultTableCellRenderer {
        final int interactiveColumn;

        public InteractiveRenderer() {
            this.interactiveColumn = InteractiveTableModel.HIDDEN_INDEX;
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == interactiveColumn && hasFocus) {
                if ((InteractiveForm.this.tableModel.getRowCount() - 1) == row &&
                        !InteractiveForm.this.tableModel.hasEmptyRow()) {
                    InteractiveForm.this.tableModel.addEmptyRow();
                }

                highlightLastRow(row);
            }

            return c;
        }
    }

    private class InteractiveTableModelListener implements TableModelListener {
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
