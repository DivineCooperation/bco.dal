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
import java.text.DecimalFormat;
import org.openbase.bco.dal.lib.layer.service.consumer.ConsumerService;
import org.openbase.bco.dal.lib.layer.service.operation.TargetTemperatureStateOperationService;
import org.openbase.bco.dal.lib.layer.service.provider.TargetTemperatureStateProviderService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.type.domotic.state.TemperatureStateType.TemperatureState;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class TargetTemperatureStateServicePanel extends AbstractServicePanel<TargetTemperatureStateProviderService, ConsumerService, TargetTemperatureStateOperationService> {

    private final DecimalFormat numberFormat = new DecimalFormat("#.##");

    /**
     * Creates new form BrightnessService
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public TargetTemperatureStateServicePanel() throws org.openbase.jul.exception.InstantiationException {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        setTargetTemperatureValueTextField = new javax.swing.JTextField();
        dataUnitLabel2 = new javax.swing.JLabel();
        setTargetTemperatureLabel = new javax.swing.JLabel();
        dataUnitLabel1 = new javax.swing.JLabel();
        currentTargetTemperatureValueTextField = new javax.swing.JTextField();

        jLabel1.setText("Current TargetTemperatur:");

        setTargetTemperatureValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTargetTemperatureValueTextFieldActionPerformed(evt);
            }
        });

        dataUnitLabel2.setText("°C");

        setTargetTemperatureLabel.setText("Set TargetTemperatur:");

        dataUnitLabel1.setText("°C");

        currentTargetTemperatureValueTextField.setEditable(false);
        currentTargetTemperatureValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentTargetTemperatureValueTextFieldActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(currentTargetTemperatureValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(dataUnitLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(setTargetTemperatureLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(setTargetTemperatureValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(dataUnitLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(currentTargetTemperatureValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dataUnitLabel1))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(setTargetTemperatureValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dataUnitLabel2))
                    .addComponent(setTargetTemperatureLabel))
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void setTargetTemperatureValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTargetTemperatureValueTextFieldActionPerformed

        try {
            double value = Double.parseDouble(setTargetTemperatureValueTextField.getText());
            notifyActionProcessing(getOperationService().setTargetTemperatureState(TemperatureState.newBuilder().setTemperature(value).build()));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not set target temperature!", ex), logger, LogLevel.ERROR);
        }
    }//GEN-LAST:event_setTargetTemperatureValueTextFieldActionPerformed

    private void currentTargetTemperatureValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentTargetTemperatureValueTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currentTargetTemperatureValueTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField currentTargetTemperatureValueTextField;
    private javax.swing.JLabel dataUnitLabel1;
    private javax.swing.JLabel dataUnitLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel setTargetTemperatureLabel;
    private javax.swing.JTextField setTargetTemperatureValueTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    protected void updateDynamicComponents() {
        try {
            currentTargetTemperatureValueTextField.setText(numberFormat.format(getProviderService().getTargetTemperatureState().getTemperature()));
            dataUnitLabel1.setText(getProviderService().getTargetTemperatureState().getTemperatureDataUnit().name());
            dataUnitLabel2.setText(getProviderService().getTargetTemperatureState().getTemperatureDataUnit().name());
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.ERROR);
        }
    }
}
