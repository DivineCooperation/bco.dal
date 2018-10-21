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
import org.openbase.bco.dal.lib.layer.service.provider.MotionStateProviderService;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rst.processing.TimestampJavaTimeTransform;
import org.openbase.jul.processing.StringProcessor;
import rst.domotic.action.ActionDescriptionType.ActionDescription;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.domotic.state.MotionStateType.MotionState;
import rst.domotic.state.MotionStateType.MotionState.State;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class MotionStateServicePanel extends AbstractServicePanel<MotionStateProviderService, ConsumerService, OperationService> {

    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);

    /**
     * Creates new form BrightnessService
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public MotionStateServicePanel() throws org.openbase.jul.exception.InstantiationException {
        initComponents();

        try {
            applyMotionStateButton.setVisible(JPService.getProperty(JPProviderControlMode.class).getValue());
        } catch (JPNotAvailableException ex) {
            applyMotionStateButton.setVisible(false);
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        motionStatePanel = new javax.swing.JPanel();
        motionStatusLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lastMovementValueLabel = new javax.swing.JLabel();
        applyMotionStateButton = new javax.swing.JButton();

        motionStatePanel.setBackground(new java.awt.Color(204, 204, 204));
        motionStatePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 5, true));
        motionStatePanel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        motionStatusLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        motionStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        motionStatusLabel.setText("MotionState");
        motionStatusLabel.setFocusable(false);
        motionStatusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout motionStatePanelLayout = new javax.swing.GroupLayout(motionStatePanel);
        motionStatePanel.setLayout(motionStatePanelLayout);
        motionStatePanelLayout.setHorizontalGroup(
            motionStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(motionStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        motionStatePanelLayout.setVerticalGroup(
            motionStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(motionStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );

        jLabel1.setText("Last Movement:");

        lastMovementValueLabel.setText("N/A");

        applyMotionStateButton.setText("MotionState");
        applyMotionStateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyMotionStateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(applyMotionStateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(motionStatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lastMovementValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(motionStatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lastMovementValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyMotionStateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applyMotionStateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyMotionStateButtonActionPerformed
        try {
            MotionState motionState;
            switch (getProviderService().getMotionState().getValue()) {
                case MOTION:
                    motionState = MotionState.newBuilder().setValue(MotionState.State.NO_MOTION).build();
                    break;
                default:
                    motionState = MotionState.newBuilder().setValue(MotionState.State.MOTION).build();
                    break;
            }
            ActionDescription action = ActionDescriptionProcessor.generateActionDescriptionBuilder(motionState, ServiceType.MOTION_STATE_SERVICE, getUnitRemote()).build();
            notifyActionProcessing(getUnitRemote().applyAction(action));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }//GEN-LAST:event_applyMotionStateButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyMotionStateButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lastMovementValueLabel;
    private javax.swing.JPanel motionStatePanel;
    private javax.swing.JLabel motionStatusLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    protected void updateDynamicComponents() {
        try {
            switch (getProviderService().getMotionState().getValue()) {
                case MOTION:
                    motionStatusLabel.setForeground(Color.BLACK);
                    motionStatePanel.setBackground(Color.GREEN.darker());
                    break;
                case NO_MOTION:
                    motionStatusLabel.setForeground(Color.BLACK);
                    motionStatePanel.setBackground(Color.LIGHT_GRAY);
                    break;
                case UNKNOWN:
                    motionStatusLabel.setForeground(Color.BLACK);
                    motionStatePanel.setBackground(Color.ORANGE.darker());
                    break;
                default:
                    throw new InvalidStateException("State[" + getProviderService().getMotionState().getValue() + "] is unknown.");
            }
            motionStatusLabel.setText(StringProcessor.transformUpperCaseToCamelCase(getProviderService().getMotionState().getValue().name()));

            switch (getProviderService().getMotionState().getValue()) {
                case MOTION:
                    applyMotionStateButton.setText(StringProcessor.transformUpperCaseToCamelCase(MotionState.State.NO_MOTION.name()));
                    break;
                default:
                    applyMotionStateButton.setText(StringProcessor.transformUpperCaseToCamelCase(MotionState.State.MOTION.name()));
                    break;
            }

            try {
                try {
                    lastMovementValueLabel.setText(dateFormat.format(new Date(TimestampJavaTimeTransform.transform(Services.getLatestValueOccurrence(State.MOTION, getProviderService().getMotionState())))));
                } catch (NotAvailableException ex) {
                    lastMovementValueLabel.setText("Never");
                }
            } catch (Exception ex) {
                lastMovementValueLabel.setText("N/A");
                ExceptionPrinter.printHistory(new CouldNotPerformException("Could not format: [" + Services.getLatestValueOccurrence(State.MOTION, getProviderService().getMotionState()).getTime() + "]!", ex), logger, LogLevel.ERROR);
            }
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }
}
