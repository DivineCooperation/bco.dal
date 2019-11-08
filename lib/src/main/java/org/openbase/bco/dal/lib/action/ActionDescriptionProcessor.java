package org.openbase.bco.dal.lib.action;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.openbase.bco.authentication.lib.SessionManager;
import org.openbase.bco.dal.lib.layer.service.Service;
import org.openbase.bco.dal.lib.layer.service.ServiceJSonProcessor;
import org.openbase.bco.dal.lib.layer.service.Services;
import org.openbase.bco.dal.lib.layer.unit.Unit;
import org.openbase.bco.dal.lib.layer.unit.user.User;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.unit.lib.UnitRegistry;
import org.openbase.jul.annotation.Experimental;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.VerificationFailedException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.extension.type.processing.TimestampProcessor;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription.Builder;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescriptionOrBuilder;
import org.openbase.type.domotic.action.ActionEmphasisType.ActionEmphasis.Category;
import org.openbase.type.domotic.action.ActionInitiatorType.ActionInitiator;
import org.openbase.type.domotic.action.ActionInitiatorType.ActionInitiator.InitiatorType;
import org.openbase.type.domotic.action.ActionParameterType.ActionParameter;
import org.openbase.type.domotic.action.ActionParameterType.ActionParameterOrBuilder;
import org.openbase.type.domotic.action.ActionPriorityType.ActionPriority;
import org.openbase.type.domotic.action.ActionPriorityType.ActionPriority.Priority;
import org.openbase.type.domotic.action.ActionReferenceType.ActionReference;
import org.openbase.type.domotic.action.ActionReferenceType.ActionReferenceOrBuilder;
import org.openbase.type.domotic.service.ServiceStateDescriptionType.ServiceStateDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.language.MultiLanguageTextType.MultiLanguageText;
import org.openbase.type.language.MultiLanguageTextType.MultiLanguageText.MapFieldEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/*-
 * #%L
 * BCO DAL Library
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ActionDescriptionProcessor {

    public static final String INITIATOR_KEY = "$INITIATOR";
    public static final String SERVICE_TYPE_KEY = "$SERVICE_TYPE";
    public static final String UNIT_LABEL_KEY = "$UNIT_LABEL";
    public static final String SERVICE_STATE_KEY = "SERVICE_STATE";
    public static final String GENERIC_ACTION_LABEL = UNIT_LABEL_KEY + "[" + SERVICE_STATE_KEY + "]";

    public static final Map<String, String> GENERIC_ACTION_DESCRIPTION_MAP = new HashMap<>();
    public static final ActionIdGenerator ACTION_ID_GENERATOR = new ActionIdGenerator();

    static {
        GENERIC_ACTION_DESCRIPTION_MAP.put("en", INITIATOR_KEY + " changed " + SERVICE_TYPE_KEY + " of " + UNIT_LABEL_KEY + " to " + SERVICE_STATE_KEY + ".");
        GENERIC_ACTION_DESCRIPTION_MAP.put("de", INITIATOR_KEY + " hat " + SERVICE_TYPE_KEY + "  von " + UNIT_LABEL_KEY + " zu " + SERVICE_STATE_KEY + " geändert.");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDescriptionProcessor.class);
    private static final ServiceJSonProcessor JSON_PROCESSOR = new ServiceJSonProcessor();

    public static ActionParameter.Builder generateDefaultActionParameter(final Message serviceState, final ServiceType serviceType, final UnitType unitType) throws CouldNotPerformException {
        return generateDefaultActionParameter(generateServiceStateDescription(serviceState, serviceType).setUnitType(unitType).build(), true);
    }

    public static ActionParameter.Builder generateDefaultActionParameter(final Message serviceState, final ServiceType serviceType, final Unit<?> unit) throws CouldNotPerformException {
        return generateDefaultActionParameter(generateServiceStateDescription(serviceState, serviceType, unit), true);
    }

    public static ActionParameter.Builder generateDefaultActionParameter(final Message serviceState, final ServiceType serviceType, final Unit<?> unit, final boolean authenticated) throws CouldNotPerformException {
        return generateDefaultActionParameter(generateServiceStateDescription(serviceState, serviceType, unit), authenticated);
    }

    public static ActionParameter.Builder generateDefaultActionParameter(final Message serviceState, final ServiceType serviceType) throws CouldNotPerformException {
        return generateDefaultActionParameter(generateServiceStateDescription(serviceState, serviceType).build(), true);
    }


    public static ActionParameter.Builder generateDefaultActionParameter(final ServiceStateDescription serviceStateDescription) {
        return generateDefaultActionParameter(serviceStateDescription, true);
    }

    /**
     * Generates a message of default {@code ActionParameter}.
     * <p>
     * These are:
     * <ul>
     * <li>Priority = NORMAL</li>
     * <li>ExecutionTimePeriod = 0</li>
     * </ul>
     *
     * @param serviceStateDescription the description of which service how to manipulate.
     *
     * @return an ActionParameter type with the described values.
     */
    public static ActionParameter.Builder generateDefaultActionParameter(final ServiceStateDescription serviceStateDescription, final boolean authenticated) {
        ActionParameter.Builder actionParameter = ActionParameter.getDefaultInstance().toBuilder();
        actionParameter.setServiceStateDescription(serviceStateDescription);
        actionParameter.setActionInitiator(detectActionInitiatorId(authenticated));
        return actionParameter;
    }

    /**
     * Build an ActionReference from a given ActionDescription which can be added to an action chain.
     *
     * @param actionDescription the ActionDescription from which the ActionReference is generated.
     *
     * @return an ActionReference for the given ActionDescription.
     */
    public static ActionReference generateActionReference(final ActionDescriptionOrBuilder actionDescription) {
        ActionReference.Builder actionReference = ActionReference.newBuilder();
        actionReference.setActionId(actionDescription.getId());
        actionReference.setActionInitiator(actionDescription.getActionInitiator());
        actionReference.setServiceStateDescription(actionDescription.getServiceStateDescription());
        actionReference.setExecutionTimePeriod(actionDescription.getExecutionTimePeriod());
        actionReference.setInterruptible(actionDescription.getInterruptible());
        actionReference.setPriority(actionDescription.getPriority());
        actionReference.setSchedulable(actionDescription.getSchedulable());
        actionReference.setTimestamp(actionDescription.getTimestamp());
        actionReference.addAllCategory(actionDescription.getCategoryList());
        actionReference.setIntermediary(actionDescription.getIntermediary());
        return actionReference.build();
    }

    /**
     * Updates the ActionChain which is a description of actions that lead to this action.
     * The action chain is updated in a way that the immediate parent is the first element of
     * the chain. The index of the chain indicates how many actions are in between this
     * action and the causing action.
     *
     * @param actionDescription the ActionDescription which is updated
     * @param cause             the ActionDescription of the action which is the cause for the new action
     *
     * @return the updated ActionDescription
     */
    public static ActionDescription.Builder updateActionCause(final ActionDescription.Builder actionDescription, final ActionDescriptionOrBuilder cause) {
        actionDescription.clearActionCause();
        actionDescription.addActionCause(generateActionReference(cause));
        actionDescription.addAllActionCause(cause.getActionCauseList());
        return actionDescription;
    }

    /**
     * Return the initial initiator of an action. According to {@link #updateActionCause(Builder, ActionDescriptionOrBuilder)}
     * the immediate parent of an action is the first element in its chain. Thus, the last element of the chain contains
     * the original initiator. If the action chain is empty, the initiator of the action is returned.
     *
     * @param actionDescription the action description from which the original initiator is resolved.
     *
     * @return the initial initiator of an action as described above.
     */
    public static ActionInitiator getInitialInitiator(final ActionDescriptionOrBuilder actionDescription) {
        if (actionDescription.getActionCauseList().isEmpty()) {
            return actionDescription.getActionInitiator();
        } else {
            return actionDescription.getActionCause(actionDescription.getActionCauseCount() - 1).getActionInitiator();
        }
    }

    /**
     * Method generates a description for the given action chain.
     *
     * @param actionDescriptionCollection a collection of depending action descriptions.
     *
     * @return a human readable description of the action pipeline.
     */
    public static String getDescription(final Collection<ActionDescription> actionDescriptionCollection) {
        String description = "";
        for (ActionDescription actionDescription : actionDescriptionCollection) {
            if (!description.isEmpty()) {
                description += " > ";
            }
            description += actionDescription.getDescription();
        }
        return description;
    }


//    /**
//     * Generates an action description according to the configuration of this unit remote.
//     * The action description is generated using the ActionDescriptionProcessor.
//     * Additionally the initiator and the authority is detected by using the session manager as well as the user id is properly configured.
//     *
//     * @param serviceState the service attribute that will be applied by this action
//     * @param serviceType      the service type according to the service attribute
//     * @param authorized       flag to define if this action should be authorized by the currently authenticated user or should be performed with OTHER rights.
//     *
//     * @return the generated action description
//     *
//     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
//     *                                  verified or serialized
//     */
//    public static ActionDescription.Builder generateActionDescriptionBuilder(final Message serviceState, final ServiceType serviceType, final boolean authorized) throws CouldNotPerformException {
//        final ActionDescription.Builder actionDescriptionBuilder = ActionDescriptionProcessor.generateActionDescriptionBuilder(serviceState, serviceType);
//        actionDescriptionBuilder.setActionInitiator(detectActionInitiatorId(authorized));
//        return updateActionDescription(actionDescriptionBuilder, serviceState, serviceType);
//    }
//

    /**
     * Generates an action description according to the configuration of this unit remote.
     * The action description is generated using the ActionDescriptionProcessor.
     * Additionally the initiator and the authority is detected by using the session manager as well as the user id is properly configured.
     *
     * @param serviceState the service attribute that will be applied by this action
     * @param serviceType  the service type according to the service attribute
     *
     * @return the generated action description
     *
     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
     *                                  verified or serialized
     */
    public static ActionDescription.Builder generateActionDescriptionBuilder(final Message serviceState, final ServiceType serviceType) throws CouldNotPerformException {
        return generateActionDescriptionBuilder(generateDefaultActionParameter(serviceState, serviceType));
    }
//
//    /**
//     * Generates an action description according to the given attributes.
//     * The action description is generated using the ActionDescriptionProcessor.
//     * Additionally the initiator and the authority is detected by using the session manager as well as the user id is properly configured.
//     *
//     * @param serviceState the service attribute that will be applied by this action
//     * @param serviceType      the service type according to the service attribute
//     * @param unitType         the service type according to the service attribute
//     * @param authorized       flag to define if this action should be authorized by the currently authenticated user or should be performed with OTHER rights.
//     *
//     * @return the generated action description
//     *
//     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
//     *                                  verified or serialized
//     */
//    public static ActionDescription.Builder generateActionDescriptionBuilder(final Message serviceState, ServiceType serviceType, final UnitType unitType, final boolean authorized) throws CouldNotPerformException {
//
//        // generate default description
//        final ActionDescription.Builder actionDescriptionBuilder = generateActionDescriptionBuilder(serviceState, serviceType, authorized);
//
//        // update unit type
//        actionDescriptionBuilder.getServiceStateDescriptionBuilder().setUnitType(unitType);
//
//        // return
//        return actionDescriptionBuilder;
//    }
//

    /**
     * Generates an action description according to the given attributes.
     * The action description is generated using the ActionDescriptionProcessor.
     * Additionally the initiator is detected by using the session manager as well as the user id is properly configured.
     *
     * @param serviceState the service attribute that will be applied by this action
     * @param serviceType  the service type according to the service attribute
     * @param unitType     the service type according to the service attribute
     *
     * @return the generated action description
     *
     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
     *                                  verified or serialized
     */
    public static ActionDescription.Builder generateActionDescriptionBuilder(final Message serviceState, ServiceType serviceType, final UnitType unitType) throws CouldNotPerformException {
        return generateActionDescriptionBuilder(generateDefaultActionParameter(serviceState, serviceType, unitType));
    }
//
//    /**
//     * Generates an action description according to the configuration of the given unit.
//     * The action description is generated using the ActionDescriptionProcessor.
//     * This method will set the service state description according to the service attribute and service type
//     * and replace several keys in the description to make it human readable.
//     * Additionally the initiator and the authority is detected by using the session manager as well as the user id is properly configured.
//     *
//     * @param serviceState the service attribute that will be applied by this action
//     * @param serviceType      the service type according to the service attribute
//     * @param unit             the unit to control.
//     * @param authorized       flag to define if this action should be authrorized by the currently authenticated user.
//     *
//     * @return the generated action description
//     *
//     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
//     *                                  verified or serialized
//     */
//    public static ActionDescription.Builder generateActionDescriptionBuilderAndUpdate(final Message serviceState, final ServiceType serviceType, final Unit<?> unit, final boolean authorized) throws CouldNotPerformException {
//        return updateActionDescription(generateActionDescriptionBuilder(serviceState, serviceType, authorized), serviceState, serviceType, unit);
//    }
//

    /**
     * Generates an action description according to the configuration of the given unit.
     * The action description is generated using the ActionDescriptionProcessor.
     * This method will set the service state description according to the service attribute and service type.
     * Additionally the initiator and the authority is detected by using the session manager as well as the user id is properly configured.
     *
     * @param serviceState the service attribute that will be applied by this action
     * @param serviceType  the service type according to the service attribute
     * @param unit         the unit to control.
     *
     * @return the generated action description
     *
     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
     *                                  verified or serialized
     */
    public static ActionDescription.Builder generateActionDescriptionBuilder(final Message serviceState, final ServiceType serviceType, final Unit<?> unit) throws CouldNotPerformException {
        return generateActionDescriptionBuilder(generateDefaultActionParameter(serviceState, serviceType, unit));
    }

    /**
     * Generates an {@code ActionDescription} which is based on the given {@code ActionParameter}.
     *
     * @param actionParameter type which contains all needed parameters to generate an {@code ActionDescription}
     *
     * @return an {@code ActionDescription} that only misses unit and service information
     *
     * @throws CouldNotPerformException is thrown if the passed action parameter are invalid, e.g. if the service description is missing.
     */
    public static ActionDescription.Builder generateActionDescriptionBuilder(ActionParameterOrBuilder actionParameter) throws CouldNotPerformException {
        ActionDescription.Builder actionDescriptionBuilder = ActionDescription.newBuilder();

        // validate
        actionParameter = verifyActionParameter(actionParameter);

        // add values from ActionParameter
        actionDescriptionBuilder.addAllCategory(actionParameter.getCategoryList());
        actionDescriptionBuilder.setLabel(actionParameter.getLabel());
        actionDescriptionBuilder.setActionInitiator(actionParameter.getActionInitiator());
        actionDescriptionBuilder.setServiceStateDescription(actionParameter.getServiceStateDescription());
        actionDescriptionBuilder.setExecutionTimePeriod(actionParameter.getExecutionTimePeriod());
        actionDescriptionBuilder.setPriority(actionParameter.getPriority());
        actionDescriptionBuilder.setInterruptible(actionParameter.getInterruptible());
        actionDescriptionBuilder.setSchedulable(actionParameter.getSchedulable());

        // if an initiator action is defined in ActionParameter the actionChain is updated
        if (actionParameter.hasCause()) {
            updateActionCause(actionDescriptionBuilder, actionParameter.getCause());
        }

        return actionDescriptionBuilder;
    }

    public static <AP extends ActionParameterOrBuilder> AP verifyActionParameter(final AP actionParameterOrBuilder) throws VerificationFailedException {
        ActionParameter.Builder actionParameterBuilder;

        if (actionParameterOrBuilder instanceof ActionParameter) {
            actionParameterBuilder = ((ActionParameter) actionParameterOrBuilder).toBuilder();
        } else {
            actionParameterBuilder = (ActionParameter.Builder) actionParameterOrBuilder;
        }

        if (!actionParameterBuilder.hasServiceStateDescription()) {
            throw new VerificationFailedException("Given action parameter do not provide a service state description!");
        }

        // priority and execution time period are valid by its default values so no checks necessary.

        if (!actionParameterBuilder.hasActionInitiator()) {
            actionParameterBuilder.setActionInitiator(detectActionInitiatorId(true));
        } else if (!actionParameterBuilder.getActionInitiator().hasInitiatorId()) {
            actionParameterBuilder.setActionInitiator(detectActionInitiatorId(actionParameterBuilder.getActionInitiatorBuilder(), true));
        }

        if (actionParameterOrBuilder instanceof ActionParameter) {
            return (AP) actionParameterBuilder.build();
        } else {
            return (AP) actionParameterBuilder;
        }
    }

    /**
     * Update an action description according to the configuration of this unit remote.
     * This method will set the service state description according to the service attribute and service type.
     *
     * @param serviceState the service attribute that will be applied by this action
     * @param serviceType  the service type according to the service attribute
     * @param unit         the unit to control.
     *
     * @return the action description
     *
     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
     *                                  verified or serialized
     */
    public static ServiceStateDescription generateServiceStateDescription(final Message serviceState, final ServiceType serviceType, final Unit<?> unit) throws CouldNotPerformException {
        return generateServiceStateDescription(serviceState, serviceType).setUnitId(unit.getId()).build();
    }

    /**
     * Update an action description according to the configuration of this unit remote.
     * This method will set the service state description according to the service attribute and service type.
     *
     * @param serviceState the service attribute that will be applied by this action
     * @param serviceType  the service type according to the service attribute
     *
     * @return the action description builder
     *
     * @throws CouldNotPerformException if accessing the unit registry fails or if the service attribute cannot be
     *                                  verified or serialized
     */
    public static ServiceStateDescription.Builder generateServiceStateDescription(final Message serviceState, final ServiceType serviceType) throws CouldNotPerformException {
        ServiceStateDescription.Builder serviceStateDescriptionBuilder = ServiceStateDescription.newBuilder();
        serviceStateDescriptionBuilder.setServiceState(JSON_PROCESSOR.serialize(Services.verifyAndRevalidateServiceState(serviceState)));
        serviceStateDescriptionBuilder.setServiceStateClassName(JSON_PROCESSOR.getServiceStateClassName(serviceState));
        serviceStateDescriptionBuilder.setServiceType(serviceType);
        return serviceStateDescriptionBuilder;
    }

    /**
     * Method detects the initiator triggering this action.
     *
     * @param authorized if the flag is false the initiator is cleared otherwise the initiator is auto detected via the session manager.
     *
     * @return the updated actionInitiator.
     */
    @Experimental
    public static ActionInitiator detectActionInitiatorId(final boolean authorized) {
        // todo pleminoq: can this be performed at the controller via the discovered authentication data?
        return detectActionInitiatorId(ActionInitiator.newBuilder(), authorized).build();
    }

    /**
     * Method detects the initiator triggering this action.
     *
     * @param actionInitiatorBuilder the builder to update.
     * @param authorized             if the flag is false the initiator is cleared otherwise the initiator is auto detected via the session manager.
     *
     * @return the given actionInitiatorBuilder.
     */
    @Experimental
    public static ActionInitiator.Builder detectActionInitiatorId(final ActionInitiator.Builder actionInitiatorBuilder, final boolean authorized) {
        // todo pleminoq: can this be performed at the controller via the discovered authentication data?
        if (authorized && SessionManager.getInstance().isLoggedIn()) {
            if (!SessionManager.getInstance().getUserClientPair().getUserId().isEmpty()) {
                actionInitiatorBuilder.setInitiatorId(SessionManager.getInstance().getUserClientPair().getUserId());
            } else {
                actionInitiatorBuilder.setInitiatorId(SessionManager.getInstance().getUserClientPair().getClientId());
            }
        } else {
            actionInitiatorBuilder.clearInitiatorId();
        }
        return actionInitiatorBuilder;
    }

    /**
     * Update the impacts of an actions. This methods differentiates between impacted actions that impacted actions on
     * their own and those that do not. If an action impacted further actions its impacts are taken over and else
     * the action itself is an impact.
     *
     * @param impactingAction the actions which impacted the other one.
     * @param impactedAction  the action which was impacted by the other one.
     *
     * @return the impacting actions with its impacts updated accordingly.
     */
    public static ActionDescription.Builder updateActionImpacts(final ActionDescription.Builder impactingAction, final ActionDescriptionOrBuilder impactedAction) {
        if (impactedAction.getActionImpactList().isEmpty()) {
            impactingAction.addActionImpact(generateActionReference(impactedAction));
        } else {
            impactingAction.addAllActionImpact(impactedAction.getActionImpactList());
        }
        return impactingAction;
    }

    /**
     * Prepare an action description. This sets the timestamp, the action initiator type, the id, labels and descriptions.
     *
     * @param actionDescriptionBuilder the action description builder which is prepared.
     * @param unit                     the unit on which the action description is applied.
     *
     * @throws CouldNotPerformException if preparing fails.
     */
    public static void prepare(final ActionDescription.Builder actionDescriptionBuilder, final Unit<?> unit) throws CouldNotPerformException {
        prepare(actionDescriptionBuilder, unit.getConfig(), JSON_PROCESSOR.deserialize(actionDescriptionBuilder.getServiceStateDescription().getServiceState(), actionDescriptionBuilder.getServiceStateDescription().getServiceStateClassName()));
    }

    /**
     * Prepare an action description. This sets the timestamp, the action initiator type, the id, labels and descriptions.
     *
     * @param actionDescriptionBuilder the action description builder which is prepared.
     * @param unitConfig               the config of the unit on which the action description is applied.
     *
     * @throws CouldNotPerformException if preparing fails.
     */
    public static void prepare(final ActionDescription.Builder actionDescriptionBuilder, final UnitConfig unitConfig) throws CouldNotPerformException {
        prepare(actionDescriptionBuilder, unitConfig, JSON_PROCESSOR.deserialize(actionDescriptionBuilder.getServiceStateDescription().getServiceState(), actionDescriptionBuilder.getServiceStateDescription().getServiceStateClassName()));
    }

    /**
     * Prepare an action description. This sets the timestamp, the action initiator type, the id, labels and descriptions.
     *
     * @param actionDescriptionBuilder the action description builder which is prepared.
     * @param unitConfig               the config of the unit on which the action description is applied.
     * @param serviceState             the de-serialized service state as contained in the action description.
     *
     * @throws CouldNotPerformException if preparing fails.
     */
    private static void prepare(final ActionDescription.Builder actionDescriptionBuilder, final UnitConfig unitConfig, final Message serviceState) throws CouldNotPerformException {

        // setup creation time if still missing
        if (!TimestampProcessor.hasTimestamp(actionDescriptionBuilder)) {
            TimestampProcessor.updateTimestampWithCurrentTime(actionDescriptionBuilder);
        }

        // setup service state time if still missing
        if (!TimestampProcessor.hasTimestamp(serviceState)) {
            TimestampProcessor.copyTimestamp(actionDescriptionBuilder, serviceState);
        }

        // prepare parameters from causes if required.
        // prepare execution time period from cause if not available
        actionDescriptionBuilder.setExecutionTimePeriod(getExecutionTimePeriod(actionDescriptionBuilder));
        actionDescriptionBuilder.getActionInitiatorBuilder().setInitiatorId(getInitiatorId(actionDescriptionBuilder));
        actionDescriptionBuilder.setSchedulable(getSchedulable(actionDescriptionBuilder));
        actionDescriptionBuilder.setInterruptible(getInterruptible(actionDescriptionBuilder));
        actionDescriptionBuilder.addAllCategory(getCategoryList(actionDescriptionBuilder));
        actionDescriptionBuilder.setPriority(getPriority(actionDescriptionBuilder));

        // update initiator type

        if (actionDescriptionBuilder.getActionInitiator().hasInitiatorId() && !actionDescriptionBuilder.getActionInitiator().getInitiatorId().isEmpty() && actionDescriptionBuilder.getActionInitiator().getInitiatorId() != User.OTHER) {
            // resolve type via registry
            final UnitConfig initiatorUnitConfig = Registries.getUnitRegistry().getUnitConfigById(actionDescriptionBuilder.getActionInitiator().getInitiatorId());
            if ((initiatorUnitConfig.getUnitType() == UnitType.USER && !initiatorUnitConfig.getUserConfig().getSystemUser())) {
                actionDescriptionBuilder.getActionInitiatorBuilder().setInitiatorType(InitiatorType.HUMAN);
            } else {
                actionDescriptionBuilder.getActionInitiatorBuilder().setInitiatorType(InitiatorType.SYSTEM);
            }
        } else if (!actionDescriptionBuilder.getActionInitiator().hasInitiatorType()) {
            // if no initiator is defined than use the system as initiator.
            actionDescriptionBuilder.getActionInitiatorBuilder().setInitiatorType(InitiatorType.SYSTEM);
        }


        // if the action is not schedulable it is also not interruptible
        if (!actionDescriptionBuilder.getSchedulable()) {
            actionDescriptionBuilder.setInterruptible(false);
        }

        // humans action should be executed some time before they get rejected by automation routines.
        if (actionDescriptionBuilder.getActionInitiator().getInitiatorType() == InitiatorType.HUMAN && actionDescriptionBuilder.getExecutionTimePeriod() == 0) {
            actionDescriptionBuilder.setExecutionTimePeriod(TimeUnit.MINUTES.toMicros(15));
        }

        // validate id field
        if (actionDescriptionBuilder.hasId()) {
            throw new InvalidStateException(toString(actionDescriptionBuilder) + " is already initialized and can not prepared twice!");
        }

        // prepare
        actionDescriptionBuilder.setId(ACTION_ID_GENERATOR.generateId(actionDescriptionBuilder.build()));
        LabelProcessor.addLabel(actionDescriptionBuilder.getLabelBuilder(), Locale.ENGLISH, GENERIC_ACTION_LABEL);

        // generate or update action description
        generateDescription(actionDescriptionBuilder, serviceState, unitConfig);
    }

    /**
     * Get the execution time period of an action. If the action description itself does not contain one its causes
     * are queried.
     *
     * @param actionDescription the action description of which the execution time period is retrieved.
     *
     * @return the execution time period of the action.
     *
     * @throws NotAvailableException if neither the action description nor one of its causes have an execution time period.
     */
    public static long getExecutionTimePeriod(final ActionDescriptionOrBuilder actionDescription) throws NotAvailableException {
        if (actionDescription.hasExecutionTimePeriod() || actionDescription.getExecutionTimePeriod() != 0) {
            return actionDescription.getExecutionTimePeriod();
        }

        for (final ActionReference actionReference : actionDescription.getActionCauseList()) {
            if (actionReference.hasExecutionTimePeriod() || actionReference.getExecutionTimePeriod() != 0) {
                return actionReference.getExecutionTimePeriod();
            }
        }

        throw new NotAvailableException("ExecutionTimePeriod");
    }

    /**
     * Get the initiator id of an action. If the action description itself does not contain one its causes
     * are queried.
     *
     * @param actionDescription the action description of which the initiator id is retrieved.
     *
     * @return the id of the initiator of the action.
     *
     * @throws NotAvailableException if neither the action description nor one of its causes have an initiator id.
     */
    public static String getInitiatorId(final ActionDescriptionOrBuilder actionDescription) throws NotAvailableException {
        if (!actionDescription.getActionInitiator().getInitiatorId().isEmpty()) {
            return actionDescription.getActionInitiator().getInitiatorId();
        }

        for (final ActionReference actionReference : actionDescription.getActionCauseList()) {
            if (!actionReference.getActionInitiator().getInitiatorId().isEmpty()) {
                return actionReference.getActionInitiator().getInitiatorId();
            }
        }

        throw new NotAvailableException("InitiatorId");
    }

    /**
     * Get the category list of an action. If the action description itself does not contain at least one category its
     * causes are queried.
     *
     * @param actionDescription the action description of which the category list is retrieved.
     *
     * @return the category list of an action.
     */
    public static List<Category> getCategoryList(final ActionDescriptionOrBuilder actionDescription) {
        if (actionDescription.getCategoryCount() > 0) {
            return actionDescription.getCategoryList();
        }

        for (final ActionReference actionReference : actionDescription.getActionCauseList()) {
            if (actionReference.getCategoryCount() > 0) {
                return actionReference.getCategoryList();
            }
        }

        return actionDescription.getCategoryList();
    }

    /**
     * Get the priority of an action. If the action description itself does not contain one its
     * causes are queried.
     *
     * @param actionDescription the action description of which the priority is retrieved.
     *
     * @return the action priority. If the action and none of its causes have one, the default value is returned.
     */
    public static Priority getPriority(final ActionDescriptionOrBuilder actionDescription) {
        if (actionDescription.hasPriority() && actionDescription.getPriority() != Priority.UNKNOWN) {
            return actionDescription.getPriority();
        }

        for (final ActionReference actionReference : actionDescription.getActionCauseList()) {
            if (actionReference.hasPriority() && actionReference.getPriority() != Priority.UNKNOWN) {
                return actionReference.getPriority();
            }
        }

        return ActionDescription.getDefaultInstance().getPriority();
    }

    /**
     * Get the inerruptible flag of an action. If the action description itself does not contain one its
     * causes are queried.
     *
     * @param actionDescription the action description of which the interruptible flag is retrieved.
     *
     * @return the interruptible flag. If the action and none of its causes have one, the default value is returned.
     */
    public static boolean getInterruptible(final ActionDescriptionOrBuilder actionDescription) {
        if (actionDescription.hasInterruptible()) {
            return actionDescription.getInterruptible();
        }

        for (final ActionReference actionReference : actionDescription.getActionCauseList()) {
            if (actionReference.hasInterruptible()) {
                return actionReference.getInterruptible();
            }
        }

        return ActionDescription.getDefaultInstance().getInterruptible();
    }

    /**
     * Get the schedulable flag of an action. If the action description itself does not contain one its
     * causes are queried.
     *
     * @param actionDescription the action description of which the schedulable flag is retrieved.
     *
     * @return the schedulable flag. If the action and none of its causes have one, the default value is returned.
     */
    public static boolean getSchedulable(final ActionDescriptionOrBuilder actionDescription) {
        if (actionDescription.hasSchedulable()) {
            return actionDescription.getSchedulable();
        }

        for (final ActionReference actionReference : actionDescription.getActionCauseList()) {
            if (actionReference.hasSchedulable()) {
                return actionReference.getSchedulable();
            }
        }

        return ActionDescription.getDefaultInstance().getSchedulable();
    }

    /**
     * Verify an action description. This triggers an internal call to {@link #verifyActionDescription(Builder, Unit, boolean)}
     * with prepare set to false.
     *
     * @param actionDescription the action description which is verified.
     * @param unit              the unit on which the action description is applied.
     *
     * @return a de-serialized and updated service state.
     *
     * @throws VerificationFailedException if verifying the action description failed.
     */
    public static Message verifyActionDescription(final ActionDescriptionOrBuilder actionDescription, final Unit<?> unit) throws VerificationFailedException {
        ActionDescription.Builder actionDescriptionBuilder;
        if (actionDescription instanceof ActionDescription.Builder) {
            actionDescriptionBuilder = (ActionDescription.Builder) actionDescription;
        } else {
            actionDescriptionBuilder = ((ActionDescription) actionDescription).toBuilder();
        }
        return verifyActionDescription(actionDescriptionBuilder, unit, false);
    }

    /**
     * Verify an action description. This triggers an internal call to {@link #verifyActionDescription(Builder, UnitConfig, boolean)}
     * with prepare set to false.
     *
     * @param actionDescription the action description which is verified.
     * @param unitConfig        the config of the unit on which the action description is applied.
     *
     * @return a de-serialized and updated service state.
     *
     * @throws VerificationFailedException if verifying the action description failed.
     */
    public static Message verifyActionDescription(final ActionDescriptionOrBuilder actionDescription, final UnitConfig unitConfig) throws VerificationFailedException {
        ActionDescription.Builder actionDescriptionBuilder;
        if (actionDescription instanceof ActionDescription.Builder) {
            actionDescriptionBuilder = (ActionDescription.Builder) actionDescription;
        } else {
            actionDescriptionBuilder = ((ActionDescription) actionDescription).toBuilder();
        }
        return verifyActionDescription(actionDescriptionBuilder, unitConfig, false);
    }


    /**
     * Verify an action description. If the prepare flag is set to true, the method {@link #prepare(Builder, UnitConfig, Message)}
     * is called to update the action description. Therefore, this method only allows to verify a builder.
     * In addition, this method returns a de-serialized and updated service state contained in the action description.
     * The reason for this is to minimize de-serializing operations because verifying a service state also updates it.
     *
     * @param actionDescriptionBuilder the action description builder which is verified and updated if prepare is set.
     * @param unit                     the unit on which the action description is applied.
     * @param prepare                  flag determining if the action description should be prepared.
     *
     * @return a de-serialized and updated service state.
     *
     * @throws VerificationFailedException if verifying the action description failed.
     */
    public static Message verifyActionDescription(final ActionDescription.Builder actionDescriptionBuilder, final Unit<?> unit, final boolean prepare) throws VerificationFailedException {
        try {
            return verifyActionDescription(actionDescriptionBuilder, unit.getConfig(), prepare);
        } catch (NotAvailableException ex) {
            throw new VerificationFailedException("Given target unit seems not to be ready!", ex);
        }
    }

    /**
     * Verify an action description. If the prepare flag is set to true, the method {@link #prepare(Builder, UnitConfig, Message)}
     * is called to update the action description. Therefore, this method only allows to verify a builder.
     * In addition, this method returns a de-serialized and updated service state contained in the action description.
     * The reason for this is to minimize de-serializing operations because verifying a service state also updates it.
     *
     * @param actionDescriptionBuilder the action description builder which is verified and updated if prepare is set.
     * @param unitConfig               the config of the unit on which the action description is applied.
     * @param prepare                  flag determining if the action description should be prepared.
     *
     * @return a de-serialized and updated service state.
     *
     * @throws VerificationFailedException if verifying the action description failed.
     */
    public static Message verifyActionDescription(final ActionDescription.Builder actionDescriptionBuilder, final UnitConfig unitConfig, final boolean prepare) throws VerificationFailedException {
        try {
            if (actionDescriptionBuilder == null) {
                throw new NotAvailableException("ActionDescription");
            }

            if (!actionDescriptionBuilder.hasServiceStateDescription()) {
                throw new NotAvailableException("ActionDescription.ServiceStateDescription");
            }

            if (!actionDescriptionBuilder.getServiceStateDescription().hasUnitId() || actionDescriptionBuilder.getServiceStateDescription().getUnitId().isEmpty()) {
                throw new NotAvailableException("ActionDescription.ServiceStateDescription.UnitId");
            }

            if (!unitConfig.hasId() || unitConfig.getId().isEmpty()) {
                throw new NotAvailableException("executor unit id");
            }

            if (!actionDescriptionBuilder.getServiceStateDescription().getUnitId().equals(unitConfig.getId())) {
                String targetUnitLabel;
                try {
                    targetUnitLabel = LabelProcessor.getBestMatch(Registries.getUnitRegistry().getUnitConfigById(actionDescriptionBuilder.getServiceStateDescription().getUnitId()).getLabel());
                } catch (CouldNotPerformException ex) {
                    targetUnitLabel = actionDescriptionBuilder.getServiceStateDescription().getUnitId();
                }
                throw new InvalidStateException("Referred Unit[" + targetUnitLabel + ":" + actionDescriptionBuilder.getServiceStateDescription().getUnitId() + "] is not compatible with the registered UnitController[" + LabelProcessor.getBestMatch(unitConfig.getLabel(), unitConfig.getId()) + "]!");
            }

            for (ActionReference actionReference : actionDescriptionBuilder.getActionCauseList()) {
                if (!actionReference.hasActionId() || actionReference.getActionId().isEmpty()) {
                    throw new InvalidStateException("Action is caused by an unidentifiable action [" + actionReference + "] (id is missing)");
                }
            }

            // validate initiator field
            if (!actionDescriptionBuilder.hasActionInitiator()) {
                throw new InvalidStateException("Action initiator missing!");
            }

            // validate initiator id
            if (!actionDescriptionBuilder.getActionInitiator().hasInitiatorId() || actionDescriptionBuilder.getActionInitiator().getInitiatorId().isEmpty()) {
                throw new InvalidStateException("Action initiator id missing!");
            }

            // validate if service state can be deserialized
            Message serviceState = JSON_PROCESSOR.deserialize(actionDescriptionBuilder.getServiceStateDescription().getServiceState(), actionDescriptionBuilder.getServiceStateDescription().getServiceStateClassName());
            serviceState = Services.verifyAndRevalidateServiceState(serviceState);

            // prepare or validate preparation
            if (prepare) {
                prepare(actionDescriptionBuilder, unitConfig, serviceState);
            } else {
                // validate action id
                if (!actionDescriptionBuilder.hasId()) {
                    throw new NotAvailableException("Action Id!");
                }
            }

            // validate that execution time period is set
            if (actionDescriptionBuilder.getExecutionTimePeriod() == 0) {
                throw new NotAvailableException("executionTimePeriod");
            }

            return serviceState;
        } catch (CouldNotPerformException ex) {
            throw new VerificationFailedException("Given ActionDescription[" + actionDescriptionBuilder + "] is invalid!", ex);
        }
    }

    /**
     * Generate a description for an action description. Descriptions are generated as defined in {@link #GENERIC_ACTION_DESCRIPTION_MAP}.
     *
     * @param actionDescriptionBuilder the action description builder in which descriptions are generated.
     * @param serviceState             the de-serialized service state as contained in the action description.
     * @param unitConfig               the config of the unit on which the action is applied.
     */
    private static void generateDescription(final ActionDescription.Builder actionDescriptionBuilder, final Message serviceState, final UnitConfig unitConfig) {
        final MultiLanguageText.Builder multiLanguageTextBuilder = MultiLanguageText.newBuilder();
        for (Entry<String, String> languageDescriptionEntry : GENERIC_ACTION_DESCRIPTION_MAP.entrySet()) {
            String description = languageDescriptionEntry.getValue();
            try {
                // setup unit label
                description = description.replace(UNIT_LABEL_KEY, LabelProcessor.getBestMatch(languageDescriptionEntry.getKey(), unitConfig.getLabel()));

                // setup service type
                description = description.replace(SERVICE_TYPE_KEY,
                        StringProcessor.transformToPascalCase(actionDescriptionBuilder.getServiceStateDescription().getServiceType().name()));

                // setup initiator
                description = description.replace(INITIATOR_KEY, getInitiatorName(getInitialInitiator(actionDescriptionBuilder)));

                // setup service attribute
                description = description.replace(SERVICE_STATE_KEY,
                        StringProcessor.transformCollectionToString(Services.generateServiceStateStringRepresentation(serviceState, actionDescriptionBuilder.getServiceStateDescription().getServiceType()), " "));

                // format
                description = StringProcessor.formatHumanReadable(description);

                // generate
                multiLanguageTextBuilder.addEntry(MapFieldEntry.newBuilder().setKey(languageDescriptionEntry.getKey()).setValue(description).build());
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not generate action description!", ex, LOGGER);
            }
            actionDescriptionBuilder.setDescription(multiLanguageTextBuilder);
        }
    }

    /**
     * Returns the name of the initiator of the action.
     * In case the initiator is a user than its username is used, otherwise the best match of the unit label is used.
     *
     * @param initialInitiator the initiator..
     *
     * @return the label or username.
     *
     * @throws NotAvailableException is thrown when the name is not available.
     */
    public static String getInitiatorName(final ActionInitiator initialInitiator) throws NotAvailableException {
        if (initialInitiator.hasInitiatorId() && !initialInitiator.getInitiatorId().isEmpty() && !initialInitiator.getInitiatorId().equals(User.OTHER)) {
            final UnitConfig initiatorUnitConfig = Registries.getUnitRegistry().getUnitConfigById(initialInitiator.getInitiatorId());
            if (initiatorUnitConfig.getUnitType() == UnitType.USER) {
                return initiatorUnitConfig.getUserConfig().getUserName();
            } else {
                return LabelProcessor.getBestMatch(initiatorUnitConfig.getLabel());
            }
        } else {
            return User.OTHER;
        }
    }

    /**
     * Method generates and set a new responsible action of a service state.
     *
     * @param serviceStateBuilder the builder where the responsible action should be set for.
     * @param serviceType         the type of service which is controlled.
     * @param targetUnit          the unit where this action takes place.
     * @param executionTimePeriod defines how long this action is valid in time.
     * @param timeUnit            the time unit of the @{executionTimePeriod} argument.
     *
     * @return the builder instance in just returned.
     *
     * @throws CouldNotPerformException is thrown if the setup failed.
     */
    public static <MB extends Message.Builder> MB generateAndSetResponsibleAction(final MB serviceStateBuilder, final ServiceType serviceType, final Unit<?> targetUnit, final long executionTimePeriod, final TimeUnit timeUnit) throws CouldNotPerformException {
        return generateAndSetResponsibleAction(serviceStateBuilder, serviceType, targetUnit, executionTimePeriod, timeUnit, true, true, Priority.NORMAL, null);
    }

    /**
     * Method generates and set a new responsible action of a service state.
     *
     * @param serviceStateBuilder the builder where the responsible action should be set for.
     * @param serviceType         the type of service which is controlled.
     * @param targetUnit          the unit where this action takes place.
     * @param executionTimePeriod defines how long this action is valid in time.
     * @param timeUnit            the time unit of the @{executionTimePeriod} argument.
     * @param interruptible       defines if this action can be interrupted.
     * @param schedulable         defines if this action can be scheduled.
     * @param priority            defines the priority of this action.
     * @param actionInitiator     prototype of the action initiator.
     *
     * @return the builder instance in just returned.
     *
     * @throws CouldNotPerformException is thrown if the setup failed.
     */
    public static <MB extends Message.Builder> MB generateAndSetResponsibleAction(final MB serviceStateBuilder, final ServiceType serviceType, final Unit<?> targetUnit, final long executionTimePeriod, final TimeUnit timeUnit, final boolean interruptible, final boolean schedulable, final ActionPriority.Priority priority, final ActionInitiator actionInitiator) throws CouldNotPerformException {
        try {
            // generate action parameter
            final ActionParameter.Builder actionParameter = ActionDescriptionProcessor.generateDefaultActionParameter(serviceStateBuilder.build(), serviceType, targetUnit);
            actionParameter.setInterruptible(interruptible);
            actionParameter.setSchedulable(schedulable);
            actionParameter.setPriority(priority);
            actionParameter.setExecutionTimePeriod(timeUnit.toMicros(executionTimePeriod));

            if (actionInitiator != null) {
                actionParameter.getActionInitiatorBuilder().mergeFrom(actionInitiator);
            }

            return generateAndSetResponsibleAction(serviceStateBuilder, targetUnit, actionParameter);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not setup responsible action!", ex);
        }
    }

    public static <MB extends Message.Builder> MB generateAndSetResponsibleAction(final MB serviceStateBuilder, final ServiceType serviceType, final Unit<?> targetUnit, final ActionDescription cause) throws CouldNotPerformException {
        // prepare action parameter
        final ActionParameter.Builder actionParameterBuilder = ActionDescriptionProcessor.generateDefaultActionParameter(serviceStateBuilder.build(), serviceType, targetUnit);

        // set cause
        if (cause != null) {
            actionParameterBuilder.setCause(cause);
        }

        // generate responsible action
        return generateAndSetResponsibleAction(serviceStateBuilder, targetUnit, actionParameterBuilder);
    }

    /**
     * Method generates and set a new responsible action of a service state.
     *
     * @param serviceStateBuilder the builder where the responsible action should be set for.
     * @param targetUnit          the unit where this action takes place.
     *
     * @return the builder instance in just returned.
     *
     * @throws CouldNotPerformException is thrown if the setup failed.
     */
    public static <MB extends Message.Builder> MB generateAndSetResponsibleAction(final MB serviceStateBuilder, final Unit<?> targetUnit, final ActionParameterOrBuilder actionParameter) throws CouldNotPerformException {
        try {
            // generate responsible action
            final Builder actionDescriptionBuilder = ActionDescriptionProcessor.generateActionDescriptionBuilder(actionParameter);
            ActionDescriptionProcessor.verifyActionDescription(actionDescriptionBuilder, targetUnit, true);

            // register as responsible action
            Services.setResponsibleAction(actionDescriptionBuilder, serviceStateBuilder);
            return serviceStateBuilder;
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not setup responsible action!", ex);
        }
    }


    public static String toString(final ActionParameterOrBuilder actionParameter) {
        if (actionParameter == null) {
            return "Action[?]";
        }
        return "Action["
                + "Unit:" + resolveUnitLabel(actionParameter.getServiceStateDescription().getUnitId())
                + (actionParameter.hasServiceStateDescription() ? (actionParameter.getServiceStateDescription().getServiceType().name() + "=" + actionParameter.getServiceStateDescription().getServiceState() + "|") : "")
                + "]";
    }

    public static String toString(final ActionDescriptionOrBuilder actionDescription) {
        if (actionDescription == null) {
            return "Action[?]";
        }

        return "Action["
                + (actionDescription.hasId() ? actionDescription.getId() + "|" : "")
                + (actionDescription.hasIntermediary() ? "Intermediary:" + actionDescription.getIntermediary() + "|" : "")
                + "Unit:" + resolveUnitLabel(actionDescription.getServiceStateDescription().getUnitId())
                + (actionDescription.hasServiceStateDescription() ? (actionDescription.getServiceStateDescription().getServiceType().name() + "=" + actionDescription.getServiceStateDescription().getServiceState() + "|") : "")
                + "State:" + actionDescription.getActionState().getValue().name()
                + "]";
    }

    public static String toString(final ActionReferenceOrBuilder actionReference) {
        if (actionReference == null) {
            return "Action[?]";
        }
        return "Action["
                + (actionReference.hasActionId() ? actionReference.getActionId() + "|" : "")
                + (actionReference.hasIntermediary() ? "Intermediary:" + actionReference.getIntermediary() + "|" : "")
                + "Unit:" + resolveUnitLabel(actionReference.getServiceStateDescription().getUnitId())
                + (actionReference.hasServiceStateDescription() ? (actionReference.getServiceStateDescription().getServiceType().name() + "=" + actionReference.getServiceStateDescription().getServiceState() + "|") : "")
                + "]";
    }

    private static String resolveUnitLabel(final String unitId) {
        try {
            return LabelProcessor.getBestMatch(Registries.getUnitRegistry(false).getUnitConfigById(unitId).getLabel());
        } catch (CouldNotPerformException | InterruptedException ex) {
            return unitId;
        }
    }
}
