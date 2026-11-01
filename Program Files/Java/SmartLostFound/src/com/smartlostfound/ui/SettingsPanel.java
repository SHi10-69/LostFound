package com.smartlostfound.ui;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private JToggleButton notificationToggle;
    private JToggleButton darkModeToggle;

    private JLabel statusLabel;

    public SettingsPanel() {

        createGUI();
    }


    private void createGUI() {

        setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );


        JLabel title =
                new JLabel(
                        "APPLICATION SETTINGS",
                        SwingConstants.CENTER
                );


        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );


        add(
                title,
                BorderLayout.NORTH
        );


        JPanel settingsPanel =
                new JPanel(
                        new GridLayout(
                                4,
                                1,
                                10,
                                10
                        )
                );


        notificationToggle =
                new JToggleButton(
                        "Notifications OFF"
                );


        darkModeToggle =
                new JToggleButton(
                        "Dark Mode OFF"
                );


        JButton testButton =
                new JButton(
                        "TEST SETTINGS"
                );


        statusLabel =
                new JLabel(
                        "Settings not changed.",
                        SwingConstants.CENTER
                );


        settingsPanel.add(
                notificationToggle
        );


        settingsPanel.add(
                darkModeToggle
        );


        settingsPanel.add(
                testButton
        );


        settingsPanel.add(
                statusLabel
        );


        add(
                settingsPanel,
                BorderLayout.CENTER
        );


        notificationToggle
                .addActionListener(
                        e -> {

                            if (
                                    notificationToggle
                                            .isSelected()
                            ) {

                                notificationToggle
                                        .setText(
                                                "Notifications ON"
                                        );

                            } else {

                                notificationToggle
                                        .setText(
                                                "Notifications OFF"
                                        );
                            }
                        }
                );


        darkModeToggle
                .addActionListener(
                        e -> {

                            if (
                                    darkModeToggle
                                            .isSelected()
                            ) {

                                darkModeToggle
                                        .setText(
                                                "Dark Mode ON"
                                        );

                            } else {

                                darkModeToggle
                                        .setText(
                                                "Dark Mode OFF"
                                        );
                            }
                        }
                );


        testButton.addActionListener(
                e -> {

                    String notification =
                            notificationToggle
                                    .isSelected()
                                    ? "ON"
                                    : "OFF";


                    String darkMode =
                            darkModeToggle
                                    .isSelected()
                                    ? "ON"
                                    : "OFF";


                    statusLabel.setText(
                            "Notifications: "
                                    + notification
                                    + " | Dark Mode: "
                                    + darkMode
                    );
                }
        );
    }
}