/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.remote.control.agent;

import de.citec.dal.hal.device.AbstractDeviceController;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.processing.StringProcessor;
import rst.homeautomation.control.agent.AgentConfigType;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;

/**
 *
 * @author <a href="mailto:DivineThreepwood@gmail.com">Divine Threepwood</a>
 */
public class AgentFactory implements AgentFactoryInterface {

    @Override
    public AgentInterface newAgent(final AgentConfigType.AgentConfig config) throws CouldNotPerformException {
        try {
            if (config == null) {
                throw new NotAvailableException("agentconfig");
            }
            if (!config.hasType()) {
                throw new NotAvailableException("agentype");
            }
            final Class agentClass = Thread.currentThread().getContextClassLoader().loadClass(getAgentClass (config));
            return (AgentInterface) agentClass.getConstructor(AgentConfig.class).newInstance(config);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not instantiate Agent[" + config.getId() + "]!", ex);
        }
    }

    private String getAgentClass(final AgentConfigType.AgentConfig config) {
        return AbstractDeviceController.class.getPackage().getName() + "."
                + StringProcessor.transformUpperCaseToCamelCase(config.getType().name())
                + "Agent";
    }
}