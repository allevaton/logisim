/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.opts;

import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.file.ToolbarData.ToolbarListener;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
class ToolbarList extends JList {
    private static class ToolIcon implements Icon {
        private Tool tool;

        ToolIcon(Tool tool) {
            this.tool = tool;
        }

        @Override
        public void paintIcon(Component comp, Graphics g, int x, int y) {
            Graphics gNew = g.create();
            tool.paintIcon(new ComponentDrawContext(comp, null, null, g, gNew), x + 2, y + 2);
            gNew.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    }

    private static class ListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            Component ret;
            Icon icon;
            if (value instanceof Tool) {
                Tool t = (Tool) value;
                ret = super.getListCellRendererComponent(list, t.getDisplayName(),
                        index, isSelected, cellHasFocus);
                icon = new ToolIcon(t);
            } else if (value == null) {
                ret = super.getListCellRendererComponent(list, "---",
                        index, isSelected, cellHasFocus);
                icon = null;
            } else {
                ret = super.getListCellRendererComponent(list, value.toString(),
                        index, isSelected, cellHasFocus);
                icon = null;
            }
            if (ret instanceof JLabel) {
                ((JLabel) ret).setIcon(icon);
            }
            return ret;
        }
    }

    private class Model extends AbstractListModel
            implements ToolbarListener, AttributeListener, PropertyChangeListener {
        @Override
        public int getSize() {
            return base.size();
        }

        @Override
        public Object getElementAt(int index) {
            return base.get(index);
        }

        @Override
        public void toolbarChanged() {
            fireContentsChanged(this, 0, getSize());
        }

        @Override
        public void attributeListChanged(AttributeEvent e) {
        }

        @Override
        public void attributeValueChanged(AttributeEvent e) {
            repaint();
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (AppPreferences.GATE_SHAPE.isSource(event)) {
                repaint();
            }
        }
    }

    private ToolbarData base;
    private Model model;

    public ToolbarList(ToolbarData base) {
        this.base = base;
        this.model = new Model();

        setModel(model);
        setCellRenderer(new ListRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        AppPreferences.GATE_SHAPE.addPropertyChangeListener(model);
        base.addToolbarListener(model);
        base.addToolAttributeListener(model);
    }

    public void localeChanged() {
        model.toolbarChanged();
    }
}
