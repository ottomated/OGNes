package net.ottomated.OGNes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class DownloaderWindow extends JFrame {

    private Nes nes;

    DownloaderWindow(Nes nes) {
        super("(Completely Legal) Downloader");
        this.nes = nes;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel pane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pane.setPreferredSize(new Dimension(400, 200));
        JTextField search = new JTextField();
        DefaultListModel<Downloader.NetRom> resultsModel = new DefaultListModel<>();
        JList<Downloader.NetRom> results = new JList<>(resultsModel);
        DownloaderWindow self = this;
        results.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Downloader.NetRom rom = results.getSelectedValue();
                    try {
                        File f = Downloader.download(rom);
                        nes.loadRom(f.getPath());
                        dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(self, ex.toString(), "Download Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        results.setLayoutOrientation(JList.VERTICAL);
        results.setVisibleRowCount(5);
        JScrollPane scroll = new JScrollPane(results);
        scroll.setPreferredSize(new Dimension(400, 150));
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                resultsModel.removeAllElements();
                try {
                    for (Downloader.NetRom res : Downloader.search(search.getText())) {
                        resultsModel.addElement(res);
                    }
                } catch (Exception ex) {
                    resultsModel.addElement(new Downloader.NetRom(ex.toString(), ""));
                }
            }
        });
        search.setColumns(10);
        //search.setUI(new HintTextFieldUI("Search...", false, Color.gray));
        pane.add(search);
        pane.add(scroll);
        getContentPane().add(pane);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
}
