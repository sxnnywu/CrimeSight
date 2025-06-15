import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("serial")
public class LineChartFrame extends JFrame {

    public LineChartFrame() {
        // Setup the frame
        setTitle("Line Chart Demo");
        setSize(800, 600);
        
        // Setup the chart
        CategoryDataset dataset = createDataset();
        JFreeChart chart = ChartFactory.createLineChart(
            "Car Sales by Year",  // Chart title
            "Year",               // Domain (x-axis) label
            "Cars Sold",          // Range (y-axis) label
            dataset,              // Data
            PlotOrientation.VERTICAL,  // Chart orientation
            true,                // Include legend
            false,               // Show tooltips
            false                // Show URLs
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        
        // Add the chart to the frame and make the frame visible
        add(chartPanel);
        setVisible(true);
    }

    // Fills the dataset for the line chart
    private static CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Car sales data
        dataset.addValue(80, "Car Sales", "2000");
        dataset.addValue(350, "Car Sales", "2005");
        dataset.addValue(200, "Car Sales", "2010");
        dataset.addValue(400, "Car Sales", "2015");
        dataset.addValue(500, "Car Sales", "2020");
        
        // Van sales data
        dataset.addValue(80, "Van Sales", "2000");
        dataset.addValue(50, "Van Sales", "2005");
        dataset.addValue(750, "Van Sales", "2010");
        dataset.addValue(75, "Van Sales", "2015");
        dataset.addValue(80, "Van Sales", "2020");
        
        return dataset;
    }
    

	public static void main(String[] args) {
        new LineChartFrame();
    }
}