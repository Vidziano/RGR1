

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;

public class JFreeChartMainFrame extends JFrame {

    private final JPanel jPanel;

    private JTextField jTextFieldFx;
    private JTextField jTextFieldA;
    private JTextField jTextFieldStart;
    private JTextField jTextFieldStop;
    private JTextField jTextFieldStep;

    private XYSeries xySeriesExpression;
    private XYSeries xySeriesDerivative;

    private final TreeMap<Double, Double> points;

    public JFreeChartMainFrame() {

        points = new TreeMap<>();

        setResizable(false);
        setTitle("RGR №1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 650);

        jPanel = new JPanel();
        jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        jPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(jPanel);

        initConfigureSouthPanel();
        initConfigureNorthPanel();

        JFreeChart jFreeChart = createChart();
        ChartPanel chartPanel = new ChartPanel(jFreeChart);
        jPanel.add(chartPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                JFreeChartMainFrame jFreeChartMainFrame = new JFreeChartMainFrame();
                jFreeChartMainFrame.setVisible(true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void expressionCount() {

        if (!points.isEmpty()) {
            points.clear();
        }

        double start = Double.parseDouble(jTextFieldStart.getText());
        double stop = Double.parseDouble(jTextFieldStop.getText());
        double step = Double.parseDouble(jTextFieldStep.getText());
        double a = Double.parseDouble(jTextFieldA.getText());

        xySeriesExpression.clear();
        xySeriesDerivative.clear();

        String expression_ = jTextFieldFx.getText();
        xySeriesExpression.setKey(expression_);
        xySeriesDerivative.setKey(expression_ + " derivative");

        for (double x = start; x < stop; x += step) {
            Expression expression = new ExpressionBuilder(expression_)
                    .variables("x", "a")
                    .build()
                    .setVariable("x", x)
                    .setVariable("a", a);
            double result = (expression.setVariable("x", x + 0.0001).evaluate()
                    - expression.setVariable("x", x - 0.0001).evaluate()) / 0.0002;
            points.put(x, expression.evaluate());
            xySeriesExpression.add(x, expression.evaluate());
            xySeriesDerivative.add(x, result);
        }
    }

    private void expressionCountFromFile() {

        double start = Double.parseDouble(jTextFieldStart.getText());
        double stop = Double.parseDouble(jTextFieldStop.getText());
        double step = Double.parseDouble(jTextFieldStep.getText());
        double a = Double.parseDouble(jTextFieldA.getText());

        xySeriesExpression.clear();
        xySeriesDerivative.clear();

        String expression_ = jTextFieldFx.getText();
        xySeriesExpression.setKey(expression_);
        xySeriesDerivative.setKey(expression_ + " derivative");

        Iterator<Double> iterator = points.keySet().iterator();

        for (double x = start; x < stop; x += step) {
            Expression expression = new ExpressionBuilder(expression_)
                    .variables("x", "a")
                    .build()
                    .setVariable("x", x)
                    .setVariable("a", a);
            double result = (expression.setVariable("x", x + 0.0001).evaluate()
                    - expression.setVariable("x", x - 0.0001).evaluate()) / 0.0002;

            Double key = iterator.next();
            xySeriesExpression.add(x, points.get(key));
            xySeriesDerivative.add(x, result);
        }
    }

    private void initConfigureNorthPanel() {
        JPanel panelData = new JPanel();
        jPanel.add(panelData, BorderLayout.NORTH);

        JLabel jLabelFx = new JLabel("f(x):");
        panelData.add(jLabelFx);
        jTextFieldFx = new JTextField();
        jTextFieldFx.setText("sin(ax)/x");
        panelData.add(jTextFieldFx);
        jTextFieldFx.setColumns(15);

        JLabel jLabelA = new JLabel("a:");
        panelData.add(jLabelA);
        jTextFieldA = new JTextField();
        jTextFieldA.setText("1.0");
        panelData.add(jTextFieldA);
        jTextFieldA.setColumns(6);

        JLabel jLabelStart = new JLabel("Start:");
        panelData.add(jLabelStart);
        jTextFieldStart = new JTextField();
        jTextFieldStart.setText("-6");
        panelData.add(jTextFieldStart);
        jTextFieldStart.setColumns(6);

        JLabel jLabelStop = new JLabel("Stop:");
        panelData.add(jLabelStop);
        jTextFieldStop = new JTextField();
        jTextFieldStop.setText("6");
        panelData.add(jTextFieldStop);
        jTextFieldStop.setColumns(6);

        JLabel jLabelStep = new JLabel("Step:");
        panelData.add(jLabelStep);
        jTextFieldStep = new JTextField();
        jTextFieldStep.setText("0.01");
        panelData.add(jTextFieldStep);
        jTextFieldStep.setColumns(6);
    }

    private void initConfigureSouthPanel() {
        JPanel jPanelButtons = new JPanel();
        jPanel.add(jPanelButtons, BorderLayout.SOUTH);

        JButton jButtonOpen = new JButton("Open");
        jPanelButtons.add(jButtonOpen);

        JButton jButtonSave = new JButton("Save");
        jPanelButtons.add(jButtonSave);

        JButton jButtonPlot = new JButton("Plot");
        jButtonPlot.addActionListener(e -> expressionCount());
        jPanelButtons.add(jButtonPlot);

        JButton jButtonExit = new JButton("Exit");
        jButtonExit.addActionListener(event -> System.exit(0));
        jPanelButtons.add(jButtonExit);

        jButtonOpen.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser(new File("expressions"));
            jFileChooser.setDialogTitle("Open a file");
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (jFileChooser.showDialog(null, "Open") == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser.getSelectedFile();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()));

                    if (!points.isEmpty()) {
                        points.clear();
                    }

                    String line;
                    if ((line = bufferedReader.readLine()) != null) {
                        String[] str = line.split(", ");
                        jTextFieldFx.setText(str[0]);
                        jTextFieldA.setText(str[1]);
                        jTextFieldStart.setText(str[2]);
                        jTextFieldStop.setText(str[3]);
                        jTextFieldStep.setText(str[4]);
                    }

                    while ((line = bufferedReader.readLine()) != null) {
                        String[] str = line.split(", ");
                        points.put(Double.parseDouble(str[0]), Double.parseDouble(str[1]));
                    }
                    bufferedReader.close();
                    expressionCountFromFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        jButtonSave.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser(new File("expressions"));
            jFileChooser.setDialogTitle("Save file");
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (jFileChooser.showDialog(null, "Save") == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser.getSelectedFile();
                try {
                    FileWriter fileWriter = new FileWriter(file.getPath() + "\\" + getLastPathName());

                    fileWriter.write(jTextFieldFx.getText() + ", " +
                            jTextFieldA.getText() + ", " +
                            jTextFieldStart.getText() + ", " +
                            jTextFieldStop.getText() + ", " +
                            jTextFieldStep.getText() + ", " + "\n"
                    );
                    for (Map.Entry<Double, Double> entry : points.entrySet()) {
                        fileWriter.write(entry.getKey() + ", " + entry.getValue() + "\n");
                    }

                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private JFreeChart createChart() {
        String expression_ = jTextFieldFx.getText();
        xySeriesExpression = new XYSeries(expression_);
        xySeriesDerivative = new XYSeries(expression_ + " derivative");

        expressionCount();

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(xySeriesExpression);
        dataset.addSeries(xySeriesDerivative);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        return chart;
    }

    private String getLastPathName() {

        Collection<File> files = FileUtils.listFiles(
                new File("expressions"), new String[]{"csv"}, true);

        String name;
        if (files.isEmpty()) {
            name = "file_1.csv";
        } else {
            name = replaceNumberWithOneMore(getLastElement(files).getName());
        }

        return name;
    }

    private <T> T getLastElement(final Iterable<T> elements) {
        T lastElement = null;

        for (T element : elements) {
            lastElement = element;
        }

        return lastElement;
    }

    private String replaceNumberWithOneMore(String input) {
        int startIndex = -1;
        int endIndex;
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == -1) {
            return input;
        }

        endIndex = startIndex;
        while (endIndex < input.length() && Character.isDigit(input.charAt(endIndex))) {
            endIndex++;
        }

        int number = Integer.parseInt(input.substring(startIndex, endIndex));
        String replacement = Integer.toString(number + 1);

        return input.substring(0, startIndex) + replacement + input.substring(endIndex);
    }
}