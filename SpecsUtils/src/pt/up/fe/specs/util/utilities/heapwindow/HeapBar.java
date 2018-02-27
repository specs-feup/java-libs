/*
 * Copyright 2010 SPeCS Research Group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

/*
 * HeapWindow.java
 * 
 * Created on 24/Jun/2010, 16:08:44
 */

package pt.up.fe.specs.util.utilities.heapwindow;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import pt.up.fe.specs.util.swing.GenericMouseListener;

/**
 * Shows a Swing frame with information about the current and maximum memory of the heap.
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class HeapBar extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final long UPDATE_PERIOD_MS = 500;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
    private final String jLabel2Prefix = "Max. Size: ";
    private Timer timer;
    private final MemProgressBarUpdater memProgressBar;

    /** Creates new form HeapWindow */
    public HeapBar() {
        initComponents();
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        long maxSizeMb = (long) (heapMaxSize / (Math.pow(1_048_576, 2)));
        jLabel2.setText(jLabel2Prefix + maxSizeMb + "Mb");

        memProgressBar = new MemProgressBarUpdater(jProgressBar1);
        jProgressBar1.setToolTipText("Click to run Garbage Collector");
        jProgressBar1.addMouseListener(GenericMouseListener.click(HeapBar::performGC));
        timer = null;

        /*
        SwingWorker swingWorker = new SwingWorker() {
        
         @Override
         protected Object doInBackground() throws Exception {
            long heapSize = Runtime.getRuntime().totalMemory();
            long heapFreeSize = Runtime.getRuntime().freeMemory();
            long usedMemory = heapSize - heapFreeSize;
        
            //jProgressBar1.setString(jLabel2Prefix);
            System.err.println(jProgressBar1.isStringPainted());
        
            return null;
         }
          };
        */
    }

    private static void performGC(MouseEvent e) {
        System.gc();
    }

    private TimerTask buildTimerTask(MemProgressBarUpdater memProgressBar) {
        return new TimerTask() {

            @Override
            public void run() {
                try {
                    // (new MemProgressBarUpdater(jProgressBar1)).doInBackground();
                    memProgressBar.doInBackground();
                } catch (Exception ex) {
                    Logger.getLogger(HeapBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        // jProgressBar1.setPreferredSize(new Dimension(10, 10));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setText("Heap Use/Size");

        jLabel2.setText("Max. Size:");

        setLayout(new BorderLayout());

        this.add(jProgressBar1, BorderLayout.CENTER);// , javax.swing.GroupLayout.PREFERRED_SIZE,
        // javax.swing.GroupLayout.DEFAULT_SIZE,
        // javax.swing.GroupLayout.PREFERRED_SIZE)

        // javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        // this.setLayout(layout);
        // layout.setHorizontalGroup(
        // layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        // .addGroup(
        // layout.createSequentialGroup()
        // .addContainerGap()
        // .addGroup(
        // layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        // .addGroup(
        // layout.createSequentialGroup()
        // .addComponent(jProgressBar1,
        // javax.swing.GroupLayout.DEFAULT_SIZE,
        // 172, Short.MAX_VALUE)
        // .addContainerGap())
        // .addGroup(
        // layout.createSequentialGroup()
        // .addComponent(jLabel1)
        // .addPreferredGap(
        // javax.swing.LayoutStyle.ComponentPlacement.RELATED,
        // 19, Short.MAX_VALUE)
        // .addComponent(jLabel2)
        // .addContainerGap(44, Short.MAX_VALUE)))));
        // layout.setVerticalGroup(
        // layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        // .addGroup(
        // layout.createSequentialGroup()
        // .addContainerGap()
        // .addGroup(
        // layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        // .addComponent(jLabel1)
        // .addComponent(jLabel2))
        // .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        // .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE,
        // javax.swing.GroupLayout.DEFAULT_SIZE,
        // javax.swing.GroupLayout.PREFERRED_SIZE)
        // .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    public void run() {

        java.awt.EventQueue.invokeLater(() -> {
            timer = new Timer();
            timer.scheduleAtFixedRate(buildTimerTask(memProgressBar), 0, HeapBar.UPDATE_PERIOD_MS);
            setVisible(true);
        });
    }

    public void close() {
        java.awt.EventQueue.invokeLater(() -> {
            HeapBar.this.timer.cancel();
            setVisible(false);
        });
    }

    /**
     * @param args
     *            the command line arguments
     */
    /*
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HeapWindow().setVisible(true);
            }
        });
    }
     *
     */

}
