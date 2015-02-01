/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.prefs.AppPreferences;

import javax.swing.*;
import java.awt.*;

import static com.cburch.logisim.util.LocaleString.getFromLocale;

@SuppressWarnings("serial")
class ExperimentalOptions extends OptionsPanel {
    private JLabel accelRestart = new JLabel();
    private PrefOptionList accel;

    public ExperimentalOptions(PreferencesFrame window) {
        super(window);

        accel = new PrefOptionList(AppPreferences.GRAPHICS_ACCELERATION,
                getFromLocale("accelLabel"), new PrefOption[]{
                new PrefOption(AppPreferences.ACCEL_DEFAULT, getFromLocale("accelDefault")),
                new PrefOption(AppPreferences.ACCEL_NONE, getFromLocale("accelNone")),
                new PrefOption(AppPreferences.ACCEL_OPENGL, getFromLocale("accelOpenGL")),
                new PrefOption(AppPreferences.ACCEL_D3D, getFromLocale("accelD3D")),
        });

        JPanel accelPanel = new JPanel(new BorderLayout());
        accelPanel.add(accel.getJLabel(), BorderLayout.LINE_START);
        accelPanel.add(accel.getJComboBox(), BorderLayout.CENTER);
        accelPanel.add(accelRestart, BorderLayout.PAGE_END);
        accelRestart.setFont(accelRestart.getFont().deriveFont(Font.ITALIC));
        JPanel accelPanel2 = new JPanel();
        accelPanel2.add(accelPanel);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(Box.createGlue());
        add(accelPanel2);
        add(Box.createGlue());
    }

    @Override
    public String getTitle() {
        return getFromLocale("experimentTitle");
    }

    @Override
    public String getHelpText() {
        return getFromLocale("experimentHelp");
    }

    @Override
    public void localeChanged() {
        accel.localeChanged();
        accelRestart.setText(getFromLocale("accelRestartLabel"));
    }
}
