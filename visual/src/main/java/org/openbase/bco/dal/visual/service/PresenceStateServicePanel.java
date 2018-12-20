package org.openbase.bco.dal.visual.service;

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
import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import org.openbase.bco.dal.lib.action.ActionDescriptionProcessor;
import org.openbase.bco.dal.lib.jp.JPProviderControlMode;

import org.openbase.bco.dal.lib.layer.service.Services;
import org.openbase.bco.dal.lib.layer.service.consumer.ConsumerService;
import org.openbase.bco.dal.lib.layer.service.operation.OperationService;
import org.openbase.bco.dal.lib.layer.service.provider.PresenceStateProviderService;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.type.processing.TimestampJavaTimeTransform;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.service.ServiceTemplateType;
import org.openbase.type.domotic.state.PresenceStateType.PresenceState;
import org.openbase.type.domotic.state.PresenceStateType.PresenceState.State;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PresenceStateServicePanel extends AbstractServicePanel<PresenceStateProviderService, ConsumerService, OperationService> {

    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
    
    /**
     * Creates new form PresenceStateServicePanel
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public PresenceStateServicePanel() throws org.openbase.jul.exception.InstantiationException {
        initComponents();
        
        try {
            applyPresenceStateButton.setVisible(JPService.getProperty(JPProviderControlMode.class).getValue());
        } catch (JPNotAvailableException ex) {
            applyPresenceStateButton.setVisible(false);
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        presenceStatePanel = new javax.swing.JPanel();
        stateLabel = new javax.swing.JLabel();
        lastPresenceValueLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        applyPresenceStateButton = new javax.swing.JButton();

        presenceStatePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        stateLabel.setFont(new java.awt.Font("Dialog", 1, 15)); // NOI18N
        stateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stateLabel.setText("jLabel1");

        javax.swing.GroupLayout presenceStatePanelLayout = new javax.swing.GroupLayout(presenceStatePanel);
        presenceStatePanel.setLayout(presenceStatePanelLayout);
        presenceStatePanelLayout.setHorizontalGroup(
            presenceStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(presenceStatePanelLayout.createSequentialGroup()
                .addComponent(stateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        presenceStatePanelLayout.setVerticalGroup(
            presenceStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(stateLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        lastPresenceValueLabel.setText("N/A");

        jLabel2.setText("Last Presence:");

        applyPresenceStateButton.setText("PresenseState");
        applyPresenceStateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyPresenceStateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(presenceStatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(applyPresenceStateButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lastPresenceValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(presenceStatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(applyPresenceStateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(64, 64, 64)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lastPresenceValueLabel)
                        .addComponent(jLabel2))
                    .addContainerGap(74, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applyPresenceStateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyPresenceStateButtonActionPerformed
        try {
            PresenceState presenceState;
            switch (getProviderService().getPresenceState().getValue()) {
                case PRESENT:
                presenceState = PresenceState.newBuilder().setValue(PresenceState.State.ABSENT).build();
                break;
                default:
                presenceState = PresenceState.newBuilder().setValue(PresenceState.State.PRESENT).build();
                break;
            }
            ActionDescription action = ActionDescriptionProcessor.generateActionDescriptionBuilder(presenceState, ServiceTemplateType.ServiceTemplate.ServiceType.PRESENCE_STATE_SERVICE, getUnitRemote()).build();
            notifyActionProcessing(getUnitRemote().applyAction(action));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }//GEN-LAST:event_applyPresenceStateButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyPresenceStateButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lastPresenceValueLabel;
    private javax.swing.JPanel presenceStatePanel;
    private javax.swing.JLabel stateLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    protected void updateDynamicComponents() {
        try {
            stateLabel.setText(StringProcessor.transformUpperCaseToCamelCase(getProviderService().getPresenceState().getValue().name()));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger);
        }
        try {
            switch (getProviderService().getPresenceState().getValue()) {
                case ABSENT:
                    presenceStatePanel.setForeground(Color.LIGHT_GRAY);
                    presenceStatePanel.setBackground(Color.BLUE.brighter());
                    break;
                case PRESENT:
                    presenceStatePanel.setForeground(Color.BLACK);
                    presenceStatePanel.setBackground(Color.GREEN.darker());
                    break;
                case UNKNOWN:
                    presenceStatePanel.setForeground(Color.BLACK);
                    presenceStatePanel.setBackground(Color.ORANGE.darker());
                    break;
                default:
                    throw new InvalidStateException("State[" + getProviderService().getPresenceState().getValue() + "] is unknown.");
            }
            
            try {
                try {
                    lastPresenceValueLabel.setText(dateFormat.format(new Date(TimestampJavaTimeTransform.transform(Services.getLatestValueOccurrence(State.PRESENT, getProviderService().getPresenceState())))));
                } catch (NotAvailableException ex) {
                    lastPresenceValueLabel.setText("Never");
                }
            } catch (Exception ex) {
                lastPresenceValueLabel.setText("N/A");
                ExceptionPrinter.printHistory(new CouldNotPerformException("Could not format: [" + Services.getLatestValueOccurrence(State.PRESENT, getProviderService().getPresenceState()).getTime() + "]!", ex), logger, LogLevel.ERROR);
            }
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }
}
