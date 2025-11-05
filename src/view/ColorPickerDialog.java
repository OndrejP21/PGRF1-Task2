package view;

import constants.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Modální dialog pro výběr barvy.
 */
public final class ColorPickerDialog extends JDialog {

    private final JColorChooser chooser;
    private Color selectedColor;
    private boolean approved = false;

    private ColorPickerDialog(java.awt.Window owner, String title, Color initial) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.chooser = new JColorChooser(initial != null ? initial : Color.WHITE);
        this.selectedColor = chooser.getColor();

        // Tlačítka
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(this::onOk);

        JButton cancelBtn = new JButton("Zrušit");
        cancelBtn.addActionListener(e -> onCancel());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        // Layout
        setLayout(new BorderLayout());
        add(chooser, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // Základní parametry
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(owner);
    }

    private void onOk(ActionEvent e) {
        approved = true;
        selectedColor = chooser.getColor();
        dispose();
    }

    private void onCancel() {
        approved = false;
        dispose();
    }

    /** Otevře dialog, počká na výběr a vrátí zvolenou barvu */
    public static OptionalInt showDialog(Panel parent, String title) {
        java.awt.Window owner = parent != null ? javax.swing.SwingUtilities.getWindowAncestor(parent) : null;
        ColorPickerDialog dlg = new ColorPickerDialog(owner, title != null ? title : "Výběr barvy", new Color(Constants.COLOR));
        dlg.setVisible(true);
        return OptionalInt.of(dlg.selectedColor.getRGB() & 0x00FFFFFF);

    }
}
