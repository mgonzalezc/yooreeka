/**
 * 
 */
package iweb2.util.gui;

import java.awt.event.WindowEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.ApplicationFrame;
import org.jfree.chart.util.RefineryUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * This is going to be a convenience class for doing basic XY plots.
 * here is how it would be used within the Bean Shell interpreter:
 * 
 * <quote>
 *   bsh % double[] x = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
 *   bsh % double[] y = {1.0, 4.0, 9.0, 16.0, 20.0, 29.0, 35, 40., 42.0};
 *   bsh % gui = new iweb2.util.gui.XyGui ("A plot",x,y);
 *   bsh % gui.plot();
 * </quote>
 *
 * @author <a href="mailto:babis@marmanis.com">Babis Marmanis</a>
 *
 */
public class XyGui extends ApplicationFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2878334413514645876L;
	
	private StringBuilder errMsg;
	private int loopInt;
	
	public XyGui(String title, double[] x, double[] y) {
		
		super(title);
		
		errMsg = new StringBuilder();
		setLoopInt(x.length);
		
		if ( checkX(x) && checkY(x.length, y) ) {
		
			XYSeries xydata = new XYSeries("X-Y Plot");
		
			for (int i=0; i < loopInt; i++) {
				xydata.add(x[i],y[i]);
			}

			XYSeriesCollection xycollection = new XYSeriesCollection(xydata);
		
			final JFreeChart chart = ChartFactory.createXYLineChart(
					"XY Series",
					"X", 
					"Y", 
					xycollection,
					PlotOrientation.VERTICAL,
					true,
					true,
					false);

	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	        setContentPane(chartPanel);
		} else {
			System.err.println(errMsg.toString());
		}
	}
	
	/**
	 * @param title chart title
	 * @param nameForData1 identifier for a data group/series
	 * @param nameForData2 identifier for a data group/series
	 * @param items values/categories that correspond to data values
	 */
	public XyGui(String title, String nameForData1, String nameForData2, 
            String[] items, double[] data1, double[] data2) {
        
        super(title);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i = 0, n = items.length; i < n; i++) {
            dataset.addValue(data1[i], nameForData1, items[i]);
            dataset.addValue(data2[i], nameForData2, items[i]);
        }
            
        final JFreeChart chart = ChartFactory.createLineChart(
                "User Similarity", 
                "Items",  
                "Rating", 
                dataset, 
                PlotOrientation.VERTICAL, 
                true, 
                true, 
                false);
            
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            setContentPane(chartPanel);
    }

	
	public void plot() {
		this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
	}
	
	private boolean checkX(double[] val) {
		
		boolean isOK = true;
		
		if (val == null || val.length <= 0) {
			
			errMsg.append("The array of data for the X-axis is null or does not contain data!");
			isOK = false;
		}
		
		return isOK;
	}
	
	private boolean checkY(int n, double[] val) {
		
		boolean isOK = true;
		
		if (val == null || val.length <= 0) {
			errMsg.append("---------------------------------------------------------------------\n");
			errMsg.append("ERROR:\n");
			errMsg.append("The array of data for the Y-axis is null or does not contain data!");
			errMsg.append("---------------------------------------------------------------------\n");
			isOK = false;
		}
		
		if (val.length > n) {
		
			errMsg.append("---------------------------------------------------------------------\n");
			errMsg.append("WARNING: \n");
			errMsg.append("     The length of the array for the Y-axis data is greater than \n");
			errMsg.append(" the length of the array for the X-axis data. \n");
			errMsg.append(" Only the first "+n+" points will be considered in the plot.");
			errMsg.append("---------------------------------------------------------------------\n");

		} else if (val.length < n) {
			
			errMsg.append("---------------------------------------------------------------------\n");
			errMsg.append("WARNING:\n");
			errMsg.append("     The length of the array for the Y-axis data is less than \n");
			errMsg.append(" the length of the array for the X-axis data. \n");
			errMsg.append(" Only the first "+n+" points of the X-will be considered in the plot.");
			errMsg.append("---------------------------------------------------------------------\n");
			setLoopInt(val.length);
		}
		
		return isOK;
	}
	
	private void setLoopInt(int val) {
		loopInt = val;
	}
	
    /**
     * Listens for the main window closing, and shuts down the application.
     *
     * @param event  information about the window event.
     */
    @Override
	public void windowClosing(WindowEvent event) {
        if (event.getWindow() == this) {
            dispose();
            
            //Overriding the ApplicationFrame behavior
            // Do not shutdown the JVM
            // System.exit(0);
            //-----------------------------------------
        }
    }


}
