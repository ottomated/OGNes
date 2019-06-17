package net.ottomated.OGNes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

class SettingsWindow extends JFrame {

    private static final String[] buttonNames = new String[]{"↑", "←", "↓", "→", "SELECT", "START", "A", "B"};

    SettingsWindow() {
        super("Settings");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JTabbedPane pane = new JTabbedPane();
        add(pane);
        initTabs(pane);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initTabs(JTabbedPane pane) {
        JComponent emulation = new JPanel();
        JComboBox<String> speed = new JComboBox<>(new String[]{"6 fps (0.1x)", "30 fps (0.5x)", "60 fps (1.0x)", "MAX"});
        speed.addActionListener(actionEvent -> {

            switch ((String) Objects.requireNonNull(speed.getSelectedItem())) {
                case "6 fps (0.1x)":
                    Main.fps = 6;
                    Main.settings.speed = Settings.Speed.SIX;
                    break;
                case "30 fps (0.5x)":
                    Main.fps = 30;
                    Main.settings.speed = Settings.Speed.THIRTY;
                    break;
                case "60 fps (1.0x)":
                    Main.fps = 60;
                    Main.settings.speed = Settings.Speed.SIXTY;
                    break;
                case "MAX":
                    Main.fps = 60000;
                    Main.settings.speed = Settings.Speed.MAX;
                    break;
            }
            try {
                Main.settings.save(Main.settingsFile);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.toString(), "Unable to save", JOptionPane.ERROR_MESSAGE);
            }
        });
        JTextField scale = new JTextField(Integer.toString(Main.settings.scale));
        scale.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                update();
            }

            private void update() {
                try {
                    int i = Integer.parseInt(scale.getText());
                    if (i < 1 || i > 9) return;
                    Main.settings.scale = i;
                    Main.settings.save(Main.settingsFile);
                    Main.nes.graphics.getContentPane().setPreferredSize(new Dimension(256 * Main.settings.scale, 240 * Main.settings.scale));
                    Main.nes.graphics.pack();
                    System.out.println(Main.settings.scale);
                } catch (Exception ignored) {
                }

            }
        });
        emulation.add(new JLabel("Default speed:"));
        emulation.add(speed);
        emulation.add(new JLabel("Window scale:"));
        emulation.add(scale);
        emulation.setPreferredSize(new Dimension(400, 100));
        pane.addTab("Emulation", emulation);

        JPanel keymap = new JPanel();
        JTable controllers = new JTable(new TableModel() {
            @Override
            public int getRowCount() {
                return 9;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public String getColumnName(int i) {
                return "Controller " + (i + 1);
            }

            @Override
            public Class<?> getColumnClass(int i) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int i, int i1) {
                return i > 0 && i1 > 0;
            }

            @Override
            public Object getValueAt(int i, int i1) {
                if (i == 0)
                    return new String[]{"Button", "Controller 1", "Controller 2"}[i1];
                switch (i1) {
                    case 0:
                        return buttonNames[i - 1];
                    case 1:
                        return KeyEvent.getKeyText(Main.settings.controller0[i - 1]);
                    case 2:
                        return KeyEvent.getKeyText(Main.settings.controller1[i - 1]);
                    default:
                        return "";
                }
            }

            @Override
            public void setValueAt(Object o, int i, int i1) {
                if (i1 == 1) {
                    Main.settings.controller0[i - 1] = (int) o;
                } else if (i1 == 2) {
                    Main.settings.controller1[i - 1] = (int) o;
                }
                try {
                    Main.settings.save(Main.settingsFile);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void addTableModelListener(TableModelListener tableModelListener) {

            }

            @Override
            public void removeTableModelListener(TableModelListener tableModelListener) {

            }
        });
        controllers.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object o, boolean b, boolean b1, int i, int i1) {
                boolean highlight = b1 && i > 0 && i1 > 0;
                return super.getTableCellRendererComponent(t, o, highlight, highlight, i, i1);
            }
        });
        controllers.setDefaultEditor(String.class, new KeyCellEditor(this));
        keymap.add(controllers);
        pane.addTab("Keymap", keymap);
        JComponent panel3 = new JPanel();
        pane.addTab("Paths", panel3);
    }

    private class KeyCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private JButton button;
        private int key;
        private JDialog dialog;

        KeyCellEditor(JFrame parent) {
            button = new JButton();
            button.setActionCommand("edit");
            button.addActionListener(this);
            button.setBorderPainted(false);
            dialog = new JDialog(parent);
            JLabel l = new JLabel("Press a key");
            l.setHorizontalAlignment(JLabel.CENTER);
            dialog.add(l);
            dialog.setLocationRelativeTo(null);
            dialog.setResizable(false);
            dialog.setPreferredSize(new Dimension(200, 100));

            dialog.pack();
            dialog.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    collect(e);
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    collect(e);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    collect(e);
                }

                private void collect(KeyEvent e) {
                    key = e.getKeyCode();
                    dialog.dispose();
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return key;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
            dialog.setTitle("Editing key for " + buttonNames[c - 1]);
            if (r == 1) {
                key = Main.settings.controller0[c - 1];
            } else if (r == 2) {
                key = Main.settings.controller1[c - 1];
            }
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("edit".equals(e.getActionCommand())) {
                button.setText(KeyEvent.getKeyText(key));
                dialog.setVisible(true);
            }
        }
    }
}
