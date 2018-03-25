import static org.jfree.ui.RefineryUtilities.centerFrameOnScreen;

public class ChartsDrawing {

    public static void main(final String[] args) {

        Chart chart = new Chart();
        chart.pack();
        chart.setSize(1100,900);
        centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}