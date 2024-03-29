package com.peralex.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.peralex.utilities.ui.graphs.lineGraph.MultiLineGraph;
import com.peralex.utilities.ui.graphs.lineGraph.GeneratedLineData;

/**
 *
 * @author Noel Grandin
 */
public class GraphWithMultipleLines extends javax.swing.JPanel {
  
	private static final String LINE_1 = "Line1";
	private static final String LINE_2 = "Line2";
	
	private final com.peralex.utilities.ui.graphs.graphBase.GraphWrapper graphWrapper;
  private final MultiLineGraph lineGraph;
  private final GeneratedLineData line1Data;
  private final GeneratedLineData line2Data;
  
  private final javax.swing.Timer dataTimer;
  
  /** Creates new form GraphWithMultipleLines */
  public GraphWithMultipleLines() {
    initComponents();
    
		lineGraph = new MultiLineGraph();
    lineGraph.setGridXMinMax(0, 100);
    lineGraph.setGridYMinMax(-20, 20);
		lineGraph.setFrameLimitingEnabled(true);
		lineGraph.setGridVisible(true);
		lineGraph.setZoomEnabled(true);
		
    graphWrapper = new com.peralex.utilities.ui.graphs.graphBase.GraphWrapper(lineGraph);
    graphWrapper.setTitle("Multiple Line Graph");
    graphWrapper.setAxisTitlesAndUnits("X", "froobles", "Y", "bagets");
    
    graphPanel.add(graphWrapper, BorderLayout.CENTER);
    
		lineGraph.setLineColor(LINE_1, Color.RED);
		line1ColorComboBox.setSelectedItem("Red");
		lineGraph.setLineColor(LINE_2, Color.GREEN);
		line2ColorComboBox.setSelectedItem("Green");
    
		line1Data = new GeneratedLineData(0, 0, 0, new float[0]);
		line1Data.setXValues(0, 100, 100);
		final float [] yValues1 = new float[100];
		line1Data.setYValues(yValues1);
    
		line2Data = new GeneratedLineData(0, 0, 0, new float[0]);
		line2Data.setXValues(0, 100, 100);
		final float [] yValues2 = new float[100];
		line2Data.setYValues(yValues2);
		
		dataTimer = new javax.swing.Timer(500, new ActionListener() {
    	public void actionPerformed(ActionEvent e)
    	{
    		final int noPoints = (Integer) noXPointsSpinner.getValue();
    		for (int i=0; i<noPoints; i++) {
    			yValues1[i] = (float) (Math.random() * 10) - 10;
    			yValues2[i] = (float) (Math.random() * 10);
    		}
    		lineGraph.setGraphData(LINE_1, line1Data);
    		lineGraph.setGraphData(LINE_2, line2Data);
    	}
    });
		dataTimer.start();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        graphPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        dataPeriodSpinner = new javax.swing.JSpinner();
        gridVisibleCheckBox = new javax.swing.JCheckBox();
        frameRateLimitingCheckBox = new javax.swing.JCheckBox();
        zoomEnabledCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        line1ColorComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        line2ColorComboBox = new javax.swing.JComboBox();
        framePeriodSpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        noXPointsSpinner = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        graphPanel.setLayout(new java.awt.BorderLayout());
        add(graphPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Data Period (ms)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 13, 0, 0);
        controlPanel.add(jLabel4, gridBagConstraints);

        dataPeriodSpinner.setModel(new javax.swing.SpinnerNumberModel(500, 1, 10000, 1));
        dataPeriodSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dataPeriodSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        controlPanel.add(dataPeriodSpinner, gridBagConstraints);

        gridVisibleCheckBox.setSelected(true);
        gridVisibleCheckBox.setText("Grid Visible");
        gridVisibleCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridVisibleCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(32, 8, 0, 0);
        controlPanel.add(gridVisibleCheckBox, gridBagConstraints);

        frameRateLimitingCheckBox.setSelected(true);
        frameRateLimitingCheckBox.setText("Frame Rate Limiting");
        frameRateLimitingCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameRateLimitingCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        controlPanel.add(frameRateLimitingCheckBox, gridBagConstraints);

        zoomEnabledCheckBox.setSelected(true);
        zoomEnabledCheckBox.setText("Zoom Enabled");
        zoomEnabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomEnabledCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        controlPanel.add(zoomEnabledCheckBox, gridBagConstraints);

        jLabel1.setText("Line 1 Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 13, 0, 0);
        controlPanel.add(jLabel1, gridBagConstraints);

        line1ColorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Red", "Green", "Blue" }));
        line1ColorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                line1ColorComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 0);
        controlPanel.add(line1ColorComboBox, gridBagConstraints);

        jLabel2.setText("Line 2 Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 13, 0, 0);
        controlPanel.add(jLabel2, gridBagConstraints);

        line2ColorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Red", "Green", "Blue" }));
        line2ColorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                line2ColorComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        controlPanel.add(line2ColorComboBox, gridBagConstraints);

        framePeriodSpinner.setModel(new javax.swing.SpinnerNumberModel(50, 1, 1000, 1));
        framePeriodSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                framePeriodSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        controlPanel.add(framePeriodSpinner, gridBagConstraints);

        jLabel3.setText("Frame Period (ms)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 20, 0, 0);
        controlPanel.add(jLabel3, gridBagConstraints);

        jLabel5.setText("No. X Points");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 13, 0, 0);
        controlPanel.add(jLabel5, gridBagConstraints);

        noXPointsSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 1, 5000, 1));
        noXPointsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                noXPointsSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        controlPanel.add(noXPointsSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        controlPanel.add(jSeparator1, gridBagConstraints);

        add(controlPanel, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

  private void gridVisibleCheckBoxActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridVisibleCheckBoxActionPerformed
		lineGraph.setGridVisible(gridVisibleCheckBox.isSelected());
  }//GEN-LAST:event_gridVisibleCheckBoxActionPerformed

  private void zoomEnabledCheckBoxActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomEnabledCheckBoxActionPerformed
		lineGraph.setZoomEnabled(zoomEnabledCheckBox.isSelected());
  }//GEN-LAST:event_zoomEnabledCheckBoxActionPerformed

  private void frameRateLimitingCheckBoxActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameRateLimitingCheckBoxActionPerformed
		lineGraph.setFrameLimitingEnabled(frameRateLimitingCheckBox.isSelected());
		framePeriodSpinner.setEnabled(frameRateLimitingCheckBox.isSelected());
  }//GEN-LAST:event_frameRateLimitingCheckBoxActionPerformed

  private void line1ColorComboBoxActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_line1ColorComboBoxActionPerformed
		lineGraph.setLineColor(LINE_1, toColor((String)line1ColorComboBox.getSelectedItem()));
  }//GEN-LAST:event_line1ColorComboBoxActionPerformed

  private void line2ColorComboBoxActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_line2ColorComboBoxActionPerformed
		lineGraph.setLineColor(LINE_2, toColor((String)line2ColorComboBox.getSelectedItem()));
  }//GEN-LAST:event_line2ColorComboBoxActionPerformed

	private void framePeriodSpinnerStateChanged(@SuppressWarnings("unused") javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_framePeriodSpinnerStateChanged
		lineGraph.setFrameLimitingPeriod((Integer) framePeriodSpinner.getValue());
	}//GEN-LAST:event_framePeriodSpinnerStateChanged

	private void dataPeriodSpinnerStateChanged(@SuppressWarnings("unused") javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dataPeriodSpinnerStateChanged
		dataTimer.setDelay((Integer) dataPeriodSpinner.getValue());
	}//GEN-LAST:event_dataPeriodSpinnerStateChanged

	private void noXPointsSpinnerStateChanged(@SuppressWarnings("unused") javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_noXPointsSpinnerStateChanged
	}//GEN-LAST:event_noXPointsSpinnerStateChanged
  
  private final Color toColor(String s) {
  	if (s.equalsIgnoreCase("red")) {
  		return Color.RED;
  	}
  	if (s.equalsIgnoreCase("blue")) {
  		return Color.BLUE;
  	}
  	if (s.equalsIgnoreCase("green")) {
  		return Color.GREEN;
  	}
  	throw new IllegalStateException(s);
  }
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPanel;
    private javax.swing.JSpinner dataPeriodSpinner;
    private javax.swing.JSpinner framePeriodSpinner;
    private javax.swing.JCheckBox frameRateLimitingCheckBox;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JCheckBox gridVisibleCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox line1ColorComboBox;
    private javax.swing.JComboBox line2ColorComboBox;
    private javax.swing.JSpinner noXPointsSpinner;
    private javax.swing.JCheckBox zoomEnabledCheckBox;
    // End of variables declaration//GEN-END:variables
  
}
