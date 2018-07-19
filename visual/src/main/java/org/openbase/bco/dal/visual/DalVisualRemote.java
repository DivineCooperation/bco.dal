package org.openbase.bco.dal.visual;

/*
 * #%L
 * BCO DAL Visualisation
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;

import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.authentication.lib.jp.JPAuthentication;
import org.openbase.bco.registry.lib.BCO;
import org.openbase.bco.registry.login.SystemLogin;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.unit.lib.UnitRegistry;
import org.openbase.jps.core.JPService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class DalVisualRemote extends javax.swing.JFrame {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DalVisualRemote.class);

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
     * @throws org.openbase.jul.exception.InstantiationException
     * @throws java.lang.InterruptedException
     */
    public DalVisualRemote() throws InstantiationException, InterruptedException {
        try {
            instance = this;

            SystemLogin.loginBCOUser();
            initComponents();
            loadImage();

            selectorPanel.addObserver(genericUnitPanel.getUnitConfigObserver());
            init();
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    private void loadImage() {
        try {
            setIconImage(new ImageIcon(ClassLoader.getSystemResource("dal-visual-remote.png")).getImage());
        } catch (Exception ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not load app icon!", ex), LOGGER, LogLevel.WARN);
        }
    }

    public final void init() throws InterruptedException, CouldNotPerformException {
        GlobalCachedExecutorService.execute(() -> {
            try {
                selectorPanel.init();
            } catch (InterruptedException | CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(ex, LOGGER);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusPanel = new org.openbase.bco.dal.visual.util.StatusPanel();
        genericUnitPanel = new org.openbase.bco.dal.visual.unit.GenericUnitPanel();
        jPanel1 = new javax.swing.JPanel();
        try {
            selectorPanel = new org.openbase.bco.dal.visual.util.SelectorPanel();
        } catch (org.openbase.jul.exception.InstantiationException e1) {
            e1.printStackTrace();
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BCO Visual Remote");

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
     *
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static void main(String args[]) throws InterruptedException, InvocationTargetException {
        BCO.printLogo();
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
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not setup look and feel!", ex), LOGGER, LogLevel.WARN);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        JPService.setApplicationName(DalVisualRemote.class);
        JPService.registerProperty(JPAuthentication.class);
        JPService.parseAndExitOnError(args);


        try {

            SystemLogin.loginBCOUser();
        } catch (CouldNotPerformException e) {
            System.out.println("Local system login failed. Please login via user interface to get system permissions!");
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeAndWait(() -> {
            try {
                new DalVisualRemote().setVisible(true);
            } catch (Exception ex) {
                ExceptionPrinter.printHistory(ex, LOGGER);
                System.exit(1);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openbase.bco.dal.visual.unit.GenericUnitPanel genericUnitPanel;
    private javax.swing.JPanel jPanel1;
    private org.openbase.bco.dal.visual.util.SelectorPanel selectorPanel;
    private org.openbase.bco.dal.visual.util.StatusPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
