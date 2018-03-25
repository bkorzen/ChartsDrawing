import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.SortOrder;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

class Chart extends ApplicationFrame {

    private int plotNumber = 1;
    private ArrayList<Float> allValues;
    private ArrayList<Float> RtRmValues;
    private float Dd, RiRm;
    private ChartPanel chartPanel;
    private JLabel labelDd, labelRiRm;
    private JButton buttonUp, buttonDown, buttonClose;
    private JPanel mainPanel;

    Chart() {

        super("Projekt 1");

        allValues = getFileInfo();

        RtRmValues = new ArrayList<>();

        for (int i = 122; i < allValues.size(); i += 123) {
            if (!RtRmValues.contains(allValues.get(i)))
                RtRmValues.add(allValues.get(i));
        }

        Dd = allValues.get(120 + (plotNumber - 1) * 4674);
        RiRm = allValues.get(121 + (plotNumber - 1) * 4674);

        createMainPanel();
    }

    private void createChartPanel() {
        chartPanel = new ChartPanel(getChart());
        //chartPanel.setPreferredSize(new java.awt.Dimension(mainPanel.getWidth(),mainPanel.getHeight()-40)); //rozmiar wykresu
    }

    private void createMainPanel() {
        mainPanel = new JPanel();

        mainPanel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                chartPanel.setPreferredSize(new java.awt.Dimension(mainPanel.getWidth(), mainPanel.getHeight()-40));
                chartPanel.setSize(new java.awt.Dimension(mainPanel.getWidth(), mainPanel.getHeight()-40));
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        createChartPanel();

        SpringLayout layout = new SpringLayout();
        layout.preferredLayoutSize(mainPanel);

        mainPanel.setLayout(layout);

        ListenForButton lForButton = new ListenForButton();

        buttonUp = new JButton("Up");
        buttonUp.addActionListener(lForButton);

        buttonDown = new JButton("Down");
        buttonDown.addActionListener(lForButton);

        buttonClose = new JButton("Close");
        buttonClose.addActionListener(lForButton);

        labelDd = new JLabel("D/d = " + Dd);

        if(plotNumber >= 39)
            labelRiRm = new JLabel("Ri/Rm = " + RiRm);
        else
            labelRiRm = new JLabel("");

        if(plotNumber <= 38)
            buttonDown.setEnabled(false);
        else if (plotNumber == 608)
            buttonUp.setEnabled(false);

        mainPanel.add(buttonUp);
        mainPanel.add(buttonDown);
        mainPanel.add(buttonClose);

        mainPanel.add(labelDd);
        mainPanel.add(labelRiRm);


        mainPanel.add(chartPanel);


        //LAYOUT SETTING
        layout.putConstraint(SpringLayout.WEST, buttonDown,
                10,
                SpringLayout.EAST, buttonUp);
        layout.putConstraint(SpringLayout.WEST, buttonClose,
                10,
                SpringLayout.EAST, buttonDown);
        layout.putConstraint(SpringLayout.WEST, labelDd,
                10,
                SpringLayout.EAST, buttonClose);
        layout.putConstraint(SpringLayout.WEST, labelRiRm,
                10,
                SpringLayout.EAST, labelDd);
        layout.putConstraint(SpringLayout.NORTH, labelDd,
                5,
                SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.NORTH, labelRiRm,
                5,
                SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.NORTH, chartPanel,
                40,
                SpringLayout.NORTH, buttonUp);

        setContentPane(mainPanel);
    }

    private void renderChart() {

        Dd = allValues.get(120 + (plotNumber - 1) * 4674);
        RiRm = allValues.get(121 + (plotNumber - 1) * 4674);

        labelDd.setText("D/d = " + Dd);
        if(!(plotNumber == 1))
            labelRiRm.setText("Ri/Rm = " + RiRm);
        else
            labelRiRm.setText("");

        chartPanel.setChart(getChart());
    }

    private JFreeChart getChart() {

        ArrayList<Float> seriesValues = new ArrayList<>();
        ArrayList<Integer> DdRiRtIndexes = new ArrayList<>();

        for (int i = 120 + (plotNumber - 1) * 4674; i < (120 + 123 * 38 + (plotNumber - 1) * 4674); i += 123) {
            DdRiRtIndexes.add(i);
            DdRiRtIndexes.add(i + 1);
            DdRiRtIndexes.add(i + 2);
        }

        for (int i = 120 + (plotNumber - 1) * 4674; i < (120 + 123 * 38 + (plotNumber - 1) * 4674); i++) {
            if (!DdRiRtIndexes.contains(i))
                seriesValues.add(allValues.get(i));
        }

        ArrayList<XYSeries> allSeries = new ArrayList<>();

        for (int i = 0; i < 38; i++) {
            XYSeries singleSerie = new XYSeries(RtRmValues.get(i));
            for (int j = 0; j < 120; j++) {
                singleSerie.add(java.lang.Math.pow(10, allValues.get(j)), java.lang.Math.pow(10, seriesValues.get(j + i * 120)));
            }
            allSeries.add(singleSerie);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (XYSeries xy : allSeries)
            dataset.addSeries(xy);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",          // chart title
                "Category",               // domain axis label
                "Value",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,
                false
        );

        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setSortOrder(SortOrder.DESCENDING);

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );

        for(int i=0; i<38; i++) {
            renderer.setSeriesShapesVisible(i,false);
            renderer.setSeriesStroke(i,new BasicStroke(2.0f));
        }

        plot.setRenderer(renderer);

        NumberAxis domainAxis = new LogarithmicAxis("L/d");
        NumberAxis rangeAxis = new LogarithmicAxis("Ra/Rm");

        domainAxis.setRange(0.1, 100);
        rangeAxis.setRange(0.1, 100000);

        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        chart.setBackgroundPaint(Color.white);

        plot.setOutlinePaint(Color.black);

        return chart;
    }

    private ArrayList<Float> getFileInfo() {

        File file = new File("/home/barti/IdeaProjects/ChartsDrawing/BAZA2.dat");
        ArrayList<Float> listOfValues = new ArrayList<>();

        boolean eof = false;

        try{
            DataInputStream getData = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

            while(!eof) {
                byte[] c = new byte[4];
                for(int i=0; i<c.length; i++)
                    c[i] = getData.readByte();
                float f = ByteBuffer.wrap(c).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                listOfValues.add(f);
            }
            getData.close();

        } catch (EOFException e) {
            eof = true;
        } catch (FileNotFoundException e) {
            System.out.println("No File Exception");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("I/O Exception");
            System.exit(0);
        }

        return listOfValues;
    }

    private class ListenForButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == buttonUp) {
                if(plotNumber <= 38) {
                    plotNumber=39;
                    renderChart();
                    buttonDown.setEnabled(true);
                } else {
                    plotNumber++;
                    if(plotNumber==608) {
                        renderChart();
                        buttonUp.setEnabled(false);
                    } else {
                        renderChart();
                    }
                }
            } else if(e.getSource() == buttonDown) {
                if(plotNumber <= 39) {
                    plotNumber=1;
                    renderChart();
                    buttonDown.setEnabled(false);
                } else if(plotNumber==608) {
                    plotNumber--;
                    renderChart();
                    buttonUp.setEnabled(true);
                } else {
                    plotNumber--;
                    renderChart();
                }
            } else if(e.getSource() == buttonClose)
                System.exit(0);
        }
    }

}