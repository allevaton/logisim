/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.JFileChoosers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.cburch.logisim.util.LocaleString.getFromLocale;

@SuppressWarnings("serial")
class FilePanel extends LogPanel {
    private class Listener implements ActionListener, ModelListener {
        @Override
        public void selectionChanged(ModelEvent event) {
        }

        @Override
        public void entryAdded(ModelEvent event, Value[] values) {
        }

        @Override
        public void filePropertyChanged(ModelEvent event) {
            Model model = getModel();
            computeEnableItems(model);

            File file = model.getFile();
            fileField.setText(file == null ? "" : file.getPath());
            enableButton.setEnabled(file != null);

            headerCheckBox.setSelected(model.getFileHeader());
        }

        private void computeEnableItems(Model model) {
            if (model.isFileEnabled()) {
                enableLabel.setText(getFromLocale("fileEnabled"));
                enableButton.setText(getFromLocale("fileDisableButton"));
            } else {
                enableLabel.setText(getFromLocale("fileDisabled"));
                enableButton.setText(getFromLocale("fileEnableButton"));
            }
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == enableButton) {
                getModel().setFileEnabled(!getModel().isFileEnabled());
            } else if (src == selectButton) {
                int result = chooser.showSaveDialog(getLogFrame());
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File file = chooser.getSelectedFile();
                if (file.exists() && (!file.canWrite() || file.isDirectory())) {
                    JOptionPane.showMessageDialog(getLogFrame(),
                            getFromLocale("fileCannotWriteMessage", file.getName()),
                            getFromLocale("fileCannotWriteTitle"),
                            JOptionPane.OK_OPTION);
                    return;
                }
                if (file.exists() && file.length() > 0) {
                    String[] options = {
                            getFromLocale("fileOverwriteOption"),
                            getFromLocale("fileAppendOption"),
                            getFromLocale("fileCancelOption"),
                    };
                    int option = JOptionPane.showOptionDialog(getLogFrame(),
                            getFromLocale("fileExistsMessage", file.getName()),
                            getFromLocale("fileExistsTitle"),
                            0, JOptionPane.QUESTION_MESSAGE, null,
                            options, options[0]);
                    if (option == 0) {
                        try {
                            FileWriter delete = new FileWriter(file);
                            delete.close();
                        } catch (IOException e) {
                        }
                    } else if (option == 1) {
                        // do nothing
                    } else {
                        return;
                    }
                }
                getModel().setFile(file);
            } else if (src == headerCheckBox) {
                getModel().setFileHeader(headerCheckBox.isSelected());
            }
        }
    }

    private Listener listener = new Listener();
    private JLabel enableLabel = new JLabel();
    private JButton enableButton = new JButton();
    private JLabel fileLabel = new JLabel();
    private JTextField fileField = new JTextField();
    private JButton selectButton = new JButton();
    private JCheckBox headerCheckBox = new JCheckBox();
    private JFileChooser chooser = JFileChoosers.create();

    public FilePanel(LogFrame frame) {
        super(frame);

        JPanel filePanel = new JPanel(new GridBagLayout());
        GridBagLayout gb = (GridBagLayout) filePanel.getLayout();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gb.setConstraints(fileLabel, gc);
        filePanel.add(fileLabel);
        gc.weightx = 1.0;
        gb.setConstraints(fileField, gc);
        filePanel.add(fileField);
        gc.weightx = 0.0;
        gb.setConstraints(selectButton, gc);
        filePanel.add(selectButton);
        fileField.setEditable(false);
        fileField.setEnabled(false);

        setLayout(new GridBagLayout());
        gb = (GridBagLayout) getLayout();
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1.0;
        gc.gridy = GridBagConstraints.RELATIVE;
        JComponent glue;
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        add(glue);
        gc.weighty = 0.0;
        gb.setConstraints(enableLabel, gc);
        add(enableLabel);
        gb.setConstraints(enableButton, gc);
        add(enableButton);
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        add(glue);
        gc.weighty = 0.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gb.setConstraints(filePanel, gc);
        add(filePanel);
        gc.fill = GridBagConstraints.NONE;
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        add(glue);
        gc.weighty = 0.0;
        gb.setConstraints(headerCheckBox, gc);
        add(headerCheckBox);
        glue = new JPanel();
        gc.weighty = 1.0;
        gb.setConstraints(glue, gc);
        add(glue);
        gc.weighty = 0.0;

        enableButton.addActionListener(listener);
        selectButton.addActionListener(listener);
        headerCheckBox.addActionListener(listener);
        modelChanged(null, getModel());
        localeChanged();
    }

    @Override
    public String getTitle() {
        return getFromLocale("fileTab");
    }

    @Override
    public String getHelpText() {
        return getFromLocale("fileHelp");
    }

    @Override
    public void localeChanged() {
        listener.computeEnableItems(getModel());
        fileLabel.setText(getFromLocale("fileLabel") + " ");
        selectButton.setText(getFromLocale("fileSelectButton"));
        headerCheckBox.setText(getFromLocale("fileHeaderCheck"));
    }

    @Override
    public void modelChanged(Model oldModel, Model newModel) {
        if (oldModel != null) {
            oldModel.removeModelListener(listener);
        }

        if (newModel != null) {
            newModel.addModelListener(listener);
            listener.filePropertyChanged(null);
        }
    }

}
