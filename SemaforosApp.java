import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SemaforosApp extends JFrame {
    private final TrafficLightPanel semaforo1;
    private final TrafficLightPanel semaforo2;

    public SemaforosApp() {
        setTitle("Simulador de Semaforos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2, 20, 20));

        semaforo1 = new TrafficLightPanel("Semaforo 1", 5000);
        semaforo2 = new TrafficLightPanel("Semaforo 2", 7000);

        add(semaforo1);
        add(semaforo2);

        pack();
        setLocationRelativeTo(null);

        new Thread(new TrafficLightController(semaforo1, 5000), "Hilo-Semaforo-1").start();
        new Thread(new TrafficLightController(semaforo2, 7000), "Hilo-Semaforo-2").start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new SemaforosApp().setVisible(true);
        });
    }

    private enum LightColor {
        ROJO("ROJO", Color.RED),
        VERDE("VERDE", Color.GREEN),
        AMARILLO("AMARILLO", Color.YELLOW);

        private final String label;
        private final Color awtColor;

        LightColor(String label, Color awtColor) {
            this.label = label;
            this.awtColor = awtColor;
        }
    }

    private static class TrafficLightController implements Runnable {
        private final TrafficLightPanel panel;
        private final int intervalMs;
        private final LightColor[] cycle = {
            LightColor.ROJO,
            LightColor.VERDE,
            LightColor.AMARILLO
        };

        TrafficLightController(TrafficLightPanel panel, int intervalMs) {
            this.panel = panel;
            this.intervalMs = intervalMs;
        }

        @Override
        public void run() {
            int index = 0;

            while (true) {
                LightColor current = cycle[index];
                SwingUtilities.invokeLater(() -> panel.setActiveColor(current));

                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                index = (index + 1) % cycle.length;
            }
        }
    }

    private static class TrafficLightPanel extends JPanel {
        private final JLabel titleLabel;
        private final JLabel stateLabel;
        private LightColor activeColor = LightColor.ROJO;

        TrafficLightPanel(String title, int intervalMs) {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setPreferredSize(new Dimension(280, 420));

            titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

            stateLabel = new JLabel("Color actual: " + activeColor.label, SwingConstants.CENTER);
            stateLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            infoPanel.add(titleLabel);
            infoPanel.add(stateLabel);
            add(infoPanel, BorderLayout.NORTH);
        }

        void setActiveColor(LightColor color) {
            activeColor = color;
            stateLabel.setText("Color actual: " + color.label);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int bodyX = (getWidth() - 120) / 2;
            int bodyY = 120;
            int bodyWidth = 120;
            int bodyHeight = 210;

            g2.setColor(new Color(45, 45, 45));
            g2.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 30, 30);

            drawLight(g2, LightColor.ROJO, bodyX + 20, bodyY + 20);
            drawLight(g2, LightColor.AMARILLO, bodyX + 20, bodyY + 75);
            drawLight(g2, LightColor.VERDE, bodyX + 20, bodyY + 130);

            g2.setColor(new Color(80, 80, 80));
            g2.fillRect(bodyX + 52, bodyY + bodyHeight, 16, 55);

            g2.dispose();
        }

        private void drawLight(java.awt.Graphics2D g2, LightColor light, int x, int y) {
            Color offColor = new Color(70, 70, 70);
            g2.setColor(light == activeColor ? light.awtColor : offColor);
            g2.fillOval(x, y, 80, 45);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 80, 45);
        }
    }
}
