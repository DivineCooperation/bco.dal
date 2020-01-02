package org.openbase.bco.dal.visual.service;

/*
 * #%L
 * BCO DAL Visualisation
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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
import org.openbase.bco.dal.lib.layer.service.provider.ButtonStateProviderService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.processing.StringProcessor;
import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import org.openbase.bco.dal.lib.action.ActionDescriptionProcessor;
import org.openbase.bco.dal.lib.jp.JPProviderControlMode;
import org.openbase.bco.dal.lib.layer.service.Services;
import org.openbase.bco.dal.lib.layer.service.consumer.ConsumerService;
import org.openbase.bco.dal.lib.layer.service.operation.OperationService;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPNotAvailableException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.type.processing.TimestampJavaTimeTransform;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.service.ServiceTemplateType;
import org.openbase.type.domotic.state.ButtonStateType;
import org.openbase.type.domotic.state.ButtonStateType.ButtonState;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ButtonStateServicePanel extends AbstractServicePanel<ButtonStateProviderService, ConsumerService, OperationService> {

    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
    
    /**
     * Creates new form BrightnessService
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public ButtonStateServicePanel() throws org.openbase.jul.exception.InstantiationException {
        initComponents();
        
        try {
            applyButtonStateButton.setVisible(JPService.getProperty(JPProviderControlMode.class).getValue());
        } catch (JPNotAvailableException ex) {
            applyButtonStateButton.setVisible(false);
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        standbyStatePanel = new javax.swing.JPanel();
        buttonStatusLabel = new javax.swing.JLabel();
        applyButtonStateButton = new javax.swing.JButton();
        lastButtonPressLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        standbyStatePanel.setBackground(new java.awt.Color(204, 204, 204));
        standbyStatePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 5, true));
        standbyStatePanel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        buttonStatusLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        buttonStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonStatusLabel.setText("ButtonState");
        buttonStatusLabel.setFocusable(false);
        buttonStatusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout standbyStatePanelLayout = new javax.swing.GroupLayout(standbyStatePanel);
        standbyStatePanel.setLayout(standbyStatePanelLayout);
        standbyStatePanelLayout.setHorizontalGroup(
            standbyStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
        standbyStatePanelLayout.setVerticalGroup(
            standbyStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        );

        applyButtonStateButton.setText("Press the Button");
        applyButtonStateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonStateButtonActionPerformed(evt);
            }
        });

        lastButtonPressLabel.setText("N/A");

        jLabel2.setText("Last Press:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(standbyStatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastButtonPressLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(applyButtonStateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(standbyStatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastButtonPressLabel)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyButtonStateButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applyButtonStateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonStateButtonActionPerformed
        try {
            ButtonState buttonState;
            switch (getProviderService().getButtonState().getValue()) {
                case PRESSED:
                buttonState = ButtonState.newBuilder().setValue(ButtonState.State.RELEASED).build();
                break;
                default:
                buttonState = ButtonState.newBuilder().setValue(ButtonState.State.PRESSED).build();
                break;
            }
            ActionDescription action = ActionDescriptionProcessor.generateActionDescriptionBuilder(buttonState, ServiceTemplateType.ServiceTemplate.ServiceType.BUTTON_STATE_SERVICE, getUnitRemote()).build();
            notifyActionProcessing(getUnitRemote().applyAction(action));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }//GEN-LAST:event_applyButtonStateButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButtonStateButton;
    private javax.swing.JLabel buttonStatusLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lastButtonPressLabel;
    private javax.swing.JPanel standbyStatePanel;
    // End of variables declaration//GEN-END:variables

    @Override
    protected void updateDynamicComponents() {
        try {
            switch (getProviderService().getButtonState().getValue()) {
                case PRESSED:
                    buttonStatusLabel.setForeground(Color.BLACK);
                    applyButtonStateButton.setText("Release the Button");
                    standbyStatePanel.setBackground(Color.GREEN.darker());
                    break;
                case DOUBLE_PRESSED:
                    buttonStatusLabel.setForeground(Color.BLACK);
                    standbyStatePanel.setBackground(Color.GREEN.brighter());
                    break;
                case RELEASED:
                    buttonStatusLabel.setForeground(Color.BLACK);
                    applyButtonStateButton.setText("Press the Button");
                    standbyStatePanel.setBackground(Color.LIGHT_GRAY);
                    break;
                case UNKNOWN:
                    buttonStatusLabel.setForeground(Color.BLACK);
                    standbyStatePanel.setBackground(Color.ORANGE.darker());
                    break;
                default:
                    throw new InvalidStateException("State[" + getProviderService().getButtonState().getValue() + "] is unknown.");
            }
            buttonStatusLabel.setText("Current ButtonState = " + StringProcessor.transformUpperCaseToPascalCase(getProviderService().getButtonState().getValue().name()));
            
            try {
                try {
                    lastButtonPressLabel.setText(dateFormat.format(new Date(TimestampJavaTimeTransform.transform(Services.getLatestValueOccurrence(ButtonStateType.ButtonState.State.PRESSED, getProviderService().getButtonState())))));
                } catch (NotAvailableException ex) {
                    lastButtonPressLabel.setText("Never");
                }
            } catch (Exception ex) {
                lastButtonPressLabel.setText("N/A");
                ExceptionPrinter.printHistory(new CouldNotPerformException("Could not format: [" + Services.getLatestValueOccurrence(ButtonStateType.ButtonState.State.PRESSED, getProviderService().getButtonState()).getTime() + "]!", ex), logger, LogLevel.ERROR);
            }
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }
}
