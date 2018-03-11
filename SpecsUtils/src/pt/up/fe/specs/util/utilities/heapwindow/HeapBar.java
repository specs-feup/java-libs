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
import java.awt.Font;
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

    private javax.swing.JProgressBar jProgressBar1;

    private Timer timer;
    private final MemProgressBarUpdater memProgressBar;

    /** Creates new form HeapWindow */
    public HeapBar() {
        initComponents();

        memProgressBar = new MemProgressBarUpdater(jProgressBar1);

        timer = null;
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
    private void initComponents() {
        setLayout(new BorderLayout());
        // UIManager.put("ProgressBar.background", Color.CYAN);
        // UIManager.put("ProgressBar.foreground", Color.BLACK);
        // UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
        // UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
        jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar1.setToolTipText("Click to run spGarbage Collector");
        jProgressBar1.addMouseListener(GenericMouseListener.click(HeapBar::performGC));
        jProgressBar1.setFont(jProgressBar1.getFont().deriveFont(Font.BOLD, 12f));
        this.add(jProgressBar1, BorderLayout.CENTER);
    }

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

}