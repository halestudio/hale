/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.status.war.components.memory;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.apache.wicket.util.time.Duration;

/**
 * The main page for the memory view. It contains labels that show the current
 * memory situtation and a self-updating chart showing the memory values of the
 * last 60 seconds.
 * 
 * @author Michel Kraemer
 */
public class MemoryPanel extends Panel {

	private static final long serialVersionUID = 5228052421880544175L;

	/**
	 * Default constructor
	 * 
	 * @param id the component ID
	 */
	public MemoryPanel(String id) {
		super(id);

		// calculate start values
		long curMax = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		long curTotal = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		long curUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;

		// create labels that show the current memory situation (they will be
		// updated via Ajax)
		final Label maxLabel = new Label("max_memory_label", String.valueOf(curMax));
		final Label totalLabel = new Label("total_memory_label", String.valueOf(curTotal));
		final Label usedLabel = new Label("used_memory_label", String.valueOf(curUsed));
		maxLabel.setOutputMarkupId(true);
		totalLabel.setOutputMarkupId(true);
		usedLabel.setOutputMarkupId(true);

		// create a container that redraws itself every second
		WebMarkupContainer redrawContainer = new WebMarkupContainer("redraw_container");
		redrawContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)) {

			private static final long serialVersionUID = -5579424950201876034L;

			/**
			 * These arrays store the memory values of the last 61 seconds (one
			 * second more, because the chart goes from 0 to 60)
			 */
			private final long[] maxMemory = new long[61];
			private final long[] totalMemory = new long[61];
			private final long[] usedMemory = new long[61];

			/**
			 * Shifts all values in the given array to the right and sets the
			 * first entry to the given value
			 * 
			 * @param arr the array to shift
			 * @param val the new value to insert at position 0
			 */
			private void updateArray(long[] arr, long val) {
				System.arraycopy(arr, 0, arr, 1, arr.length - 1);
				arr[0] = val;
			}

			/**
			 * Creates a string that can be used as plotting data for the memory
			 * chart
			 * 
			 * @param arr the array that contains the memory values of the last
			 *            61 seconds
			 * @return the plot data string
			 */
			private String makePlot(long[] arr) {
				StringBuffer result = new StringBuffer();
				for (int i = 0; i < arr.length; ++i) {
					if (arr[i] != 0) {
						if (result.length() > 0) {
							result.append(",");
						}
						result.append("[" + i + "," + arr[i] + "]");
					}
				}
				return result.toString();
			}

			/**
			 * Gets the maximum value from an array
			 * 
			 * @param arr the array
			 * @return the maximum value in <code>arr</code>
			 */
			private long getMax(long[] arr) {
				long result = 0;
				for (int i = 0; i < arr.length; ++i) {
					if (arr[i] > result) {
						result = arr[i];
					}
				}
				return result;
			}

			@Override
			protected void onPostProcessTarget(final AjaxRequestTarget target) {
				// get current memory situation
				long curMax = Runtime.getRuntime().maxMemory() / 1024 / 1024;
				long curTotal = Runtime.getRuntime().totalMemory() / 1024 / 1024;
				long curUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
						.freeMemory()) / 1024 / 1024;

				// update plot data of last 61 seconds
				updateArray(totalMemory, curTotal);
				updateArray(usedMemory, curUsed);

				// create plot data string
				String total = makePlot(totalMemory);
				String free = makePlot(usedMemory);

				// calculate the maximum for the chart's y axis (+ 50 MB)
				long ymax = getMax(totalMemory) + 50;

				// reset labels
				maxLabel.setDefaultModelObject(String.valueOf(curMax));
				totalLabel.setDefaultModelObject(String.valueOf(curTotal));
				usedLabel.setDefaultModelObject(String.valueOf(curUsed));
				target.add(maxLabel);
				target.add(totalLabel);
				target.add(usedLabel);

				// redraw chart
				target.appendJavaScript("chart_plot.axes.xaxis.max = " + (maxMemory.length - 1)
						+ ";" + "chart_plot.axes.yaxis.max = " + ymax + ";"
						+ "chart_plot.series[0].data = [" + total + "];"
						+ "chart_plot.series[1].data = [" + free + "];" + "chart_plot.replot();");
			}
		});
		redrawContainer.add(maxLabel);
		redrawContainer.add(totalLabel);
		redrawContainer.add(usedLabel);
		add(redrawContainer);

		// add a button that performs garbage collection
		AjaxFallbackLink<Object> performGC = new AjaxFallbackLink<Object>("perform-gc") {

			private static final long serialVersionUID = -1411055574233411598L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				System.gc();
			}
		};
		redrawContainer.add(performGC);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.render(JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(
				MemoryPanel.class, "jquery.jqplot.min.js")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(MemoryPanel.class,
				"jquery.jqplot.min.css")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(
				MemoryPanel.class, "update.js")));
	}
}
