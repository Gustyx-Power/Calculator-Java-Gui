import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorGUI extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder input = new StringBuilder();

    public CalculatorGUI() {
        /* Look & Feel */
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Kalkulator Modern");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Display */
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("SansSerif", Font.BOLD, 32));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        display.setBackground(Color.WHITE);
        add(display, BorderLayout.NORTH);

        /* Buttons */
        JPanel panel = new JPanel(new GridLayout(5, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttons = {
            "C", "±", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", "⌫"
        };

        for (String text : buttons) {
            JButton b = new JButton(text);
            b.setFont(new Font("SansSerif", Font.BOLD, 24));
            b.setFocusPainted(false);
            b.addActionListener(this);
            panel.add(b);
        }
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    /* ---------- actions ---------- */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "C":
                input.setLength(0);
                display.setText("");
                break;
            case "=":
                try {
                    String res = evaluate(input.toString());
                    display.setText(res);
                    input.setLength(0);
                    input.append(res);
                } catch (Exception ex) {
                    display.setText("Error");
                    input.setLength(0);
                }
                break;
            case "⌫":
                if (input.length() > 0) {
                    input.setLength(input.length() - 1);
                    display.setText(input.toString());
                }
                break;
            case "±":
                if (input.length() > 0) {
                    if (input.charAt(0) == '-') input.deleteCharAt(0);
                    else input.insert(0, '-');
                    display.setText(input.toString());
                }
                break;
            default:
                input.append(cmd.replace("×", "*").replace("÷", "/"));
                display.setText(input.toString());
                break;
        }
    }

    /* ---------- evaluator ---------- */
    private String evaluate(String expr) throws Exception {
        double val = parse(expr.replaceAll("\\s+", ""));
        return (val == (long) val) ? String.valueOf((long) val)
                                   : String.valueOf(val);
    }

    /* ---- mini recursive-descent parser ---- */
    private double parse(String s) { return parseAddSub(new StringBuilder(s)); }

    private double parseAddSub(StringBuilder s) {
        double left = parseMulDiv(s);
        while (s.length() > 0) {
            char op = s.charAt(0);
            if (op != '+' && op != '-') break;
            s.deleteCharAt(0);
            double right = parseMulDiv(s);
            left = (op == '+') ? left + right : left - right;
        }
        return left;
    }

    private double parseMulDiv(StringBuilder s) {
        double left = parseUnary(s);
        while (s.length() > 0) {
            char op = s.charAt(0);
            if (op != '*' && op != '/') break;
            s.deleteCharAt(0);
            double right = parseUnary(s);
            left = (op == '*') ? left * right : left / right;
        }
        return left;
    }

    private double parseUnary(StringBuilder s) {
        boolean neg = false;
        while (s.length() > 0 && s.charAt(0) == '-') {
            neg = !neg;
            s.deleteCharAt(0);
        }
        double v = parseAtom(s);
        return neg ? -v : v;
    }

    private double parseAtom(StringBuilder s) {
        if (s.length() == 0) throw new RuntimeException("Empty atom");
        StringBuilder num = new StringBuilder();
        while (s.length() > 0 && (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.')) {
            num.append(s.charAt(0));
            s.deleteCharAt(0);
        }
        if (num.length() == 0) throw new RuntimeException("Expected number");
        return Double.parseDouble(num.toString());
    }

    /* ---------- main ---------- */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }
}