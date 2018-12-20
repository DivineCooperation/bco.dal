package org.openbase.bco.dal.visual.service;

/*-
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

import org.openbase.bco.dal.lib.layer.service.consumer.ConsumerService;
import org.openbase.bco.dal.lib.layer.service.operation.ActivityMultiStateOperationService;
import org.openbase.bco.dal.lib.layer.service.provider.ActivityMultiStateProviderService;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.provider.DataProvider;
import org.openbase.type.domotic.activity.ActivityConfigType.ActivityConfig;
import org.openbase.type.domotic.registry.ActivityRegistryDataType.ActivityRegistryData;
import org.openbase.type.domotic.state.ActivityStateType.ActivityState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:pLeminoq@openbase.org">Tamino Huxohl</a>
 */
public class ActivityMultiStateServicePanel extends AbstractServicePanel<ActivityMultiStateProviderService, ConsumerService, ActivityMultiStateOperationService> {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox activityComboBox;
    private javax.swing.JLabel activityStateLabel;
    private javax.swing.JPanel activityStatePanel;

    /**
     * Creates new form ActivityStateServicePanel
     *
     * @throws org.openbase.jul.exception.InstantiationException
     */
    public ActivityMultiStateServicePanel() throws org.openbase.jul.exception.InstantiationException {
        initComponents();
        initDynamicComponents();
    }

    private void initDynamicComponents() throws org.openbase.jul.exception.InstantiationException {
        try {
            Registries.getActivityRegistry(true);
            updateComboBoxModel();

            Registries.getActivityRegistry().addDataObserver((final DataProvider<ActivityRegistryData> source, ActivityRegistryData data) -> {
                updateComboBoxModel();
            });
        } catch (CouldNotPerformException ex) {
            throw new org.openbase.jul.exception.InstantiationException(this, "Could not init dynamic components", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void updateComboBoxModel() throws CouldNotPerformException {
        Object selected = activityComboBox.getModel().getSelectedItem();

        // if selection is invalid reset it
        if (!(selected instanceof ActivityConfigHolder)) {
            selected = null;
        }

        final List<ActivityConfigHolder> activityConfigHolderList = new ArrayList<>();
        for (ActivityConfig activityConfig : Registries.getActivityRegistry().getActivityConfigs()) {
            activityConfigHolderList.add(new ActivityConfigHolder(activityConfig));
        }
        Collections.sort(activityConfigHolderList);
        activityComboBox.setModel(new DefaultComboBoxModel(activityConfigHolderList.toArray()));

        if (selected != null) {
            activityComboBox.getModel().setSelectedItem(selected);
        } else {
            activityComboBox.getModel().setSelectedItem(activityComboBox.getModel().getElementAt(0));
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        activityStatePanel = new javax.swing.JPanel();
        activityStateLabel = new javax.swing.JLabel();
        activityComboBox = new javax.swing.JComboBox();

        activityStatePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        activityStateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        activityStateLabel.setText("ActivityState");

        javax.swing.GroupLayout activityStatePanelLayout = new javax.swing.GroupLayout(activityStatePanel);
        activityStatePanel.setLayout(activityStatePanelLayout);
        activityStatePanelLayout.setHorizontalGroup(
                activityStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(activityStateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
        );
        activityStatePanelLayout.setVerticalGroup(
                activityStatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(activityStateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
        );

        activityComboBox.setModel(new DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        activityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activityComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(activityStatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(activityComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(activityStatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(activityComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void activityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activityComboBoxActionPerformed
        Object selected = activityComboBox.getModel().getSelectedItem();
        if (!(selected instanceof ActivityConfigHolder)) {
            return;
        }

        ActivityConfigHolder selectedActivity = (ActivityConfigHolder) selected;
        try {
            notifyActionProcessing(getOperationService().addActivityState(selectedActivity.getActivityConfig().getId()));
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not set user activity state!", ex), logger);
        }
    }//GEN-LAST:event_activityComboBoxActionPerformed
    // End of variables declaration//GEN-END:variables

    @Override
    protected void updateDynamicComponents() {
        try {
            String activityList = "";
            for (final String activityId : getProviderService().getActivityMultiState().getActivityIdList()) {

                if (!activityList.isEmpty()) {
                    activityList += ", ";
                }

                ActivityConfig activityConfig = Registries.getActivityRegistry().getActivityConfigById(activityId);
                logger.info("state: " + activityConfig.getLabel());
//                    activityComboBox.getModel().setSelectedItem(new ActivityConfigHolder(activityConfig));
                activityList += LabelProcessor.getBestMatch(activityConfig.getLabel());
            }
            activityStateLabel.setText("Current Activities = " + activityList);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger);
        }
    }

    public static class ActivityConfigHolder implements Comparable<ActivityConfigHolder> {

        private final ActivityConfig activityConfig;

        public ActivityConfigHolder(final ActivityConfig activityConfig) {
            this.activityConfig = activityConfig;
        }

        @Override
        public String toString() {
            try {
                return LabelProcessor.getBestMatch(this.activityConfig.getLabel());
            } catch (NotAvailableException e) {
                return "?";
            }
        }

        @Override
        public int compareTo(ActivityConfigHolder o) {
            return toString().compareTo(o.toString());
        }

        public ActivityConfig getActivityConfig() {
            return activityConfig;
        }
    }
}
