/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.visual;

import de.citec.dal.visual.util.StatusPanel;
import de.citec.jps.core.JPService;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.NotAvailableException;
import javax.swing.ImageIcon;

/**
 *
 * @author mpohling
 */
public class DalVisualRemote extends javax.swing.JFrame {

    protected static final Logger logger = LoggerFactory.getLogger(DalVisualRemote.class);

    private static DalVisualRemote instance;

    public synchronized static DalVisualRemote getInstance() throws NotAvailableException {
        if (instance == null) {
            throw new NotAvailableException(DalVisualRemote.class.getSimpleName());
        }
        return instance;
    }

    /**
     * Creates new form DalVisualRemote
     *
     * @throws de.citec.jul.exception.InstantiationException
     */
    public DalVisualRemote() throws InstantiationException, InterruptedException {
        try {
            instance = this;
            initComponents();
            setIconImage(new ImageIcon(ClassLoader.getSystemResource("dal-visual-remote.png")).getImage());
            selectorPanel.addObserver(genericUnitPanel.getUnitConfigObserver());
            selectorPanel.init();
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusPanel = new de.citec.dal.visual.util.StatusPanel();
        genericUnitPanel = new de.citec.dal.visual.unit.GenericUnitPanel();
        jPanel1 = new javax.swing.JPanel();
        try {
            selectorPanel = new de.citec.dal.visual.util.SelectorPanel();
        } catch (de.citec.jul.exception.InstantiationException e1) {
            e1.printStackTrace();
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dal Visual Remote");

        jPanel1.add(selectorPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(genericUnitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genericUnitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * l
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | java.lang.InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.warn("Could not setup look and feel!", ex);
        }
        //</editor-fold>

        //</editor-fold>
        JPService.setApplicationName("dal-visual-remote");
        JPService.parseAndExitOnError(args);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new DalVisualRemote().setVisible(true);
                } catch (Exception ex) {
                    ExceptionPrinter.printHistory(logger, ex);
                    System.exit(1);
                }
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.citec.dal.visual.unit.GenericUnitPanel genericUnitPanel;
    private javax.swing.JPanel jPanel1;
    private de.citec.dal.visual.util.SelectorPanel selectorPanel;
    private de.citec.dal.visual.util.StatusPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}