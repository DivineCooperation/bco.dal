/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.hal.al;

import de.citec.dal.DALService;
import de.citec.dal.data.Location;
import de.citec.dal.exception.DALException;
import de.citec.dal.hal.device.philips.PH_Hue_E27Controller;
import de.citec.dal.util.DALRegistry;
import de.citec.jps.core.JPService;
import de.citec.jps.properties.JPHardwareSimulationMode;
import de.citec.jul.exception.VerificationFailedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.slf4j.LoggerFactory;
import rst.homeautomation.states.PowerType;

/**
 *
 * @author thuxohl
 */
public class LightRemoteTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DALService.class);

    private final LightRemote lightRemote = new LightRemote();
    private DALService dalService = new DALService(new LightRemoteTest.DeviceInitializerImpl());

    private static final Location location = new Location("paradise");
    private static final String label = "Light_Unit_Test";

    public LightRemoteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        JPService.registerProperty(JPHardwareSimulationMode.class, false);
        dalService = new DALService();
        dalService.activate();

        lightRemote.init(label, location);
        lightRemote.activate();
    }

    @After
    public void tearDown() {
        dalService.deactivate();
        try {
            lightRemote.deactivate();
        } catch (InterruptedException ex) {
            logger.warn("Could not deactivate light remote: ", ex);
        }
    }

    /**
     * Test of setPowerState method, of class LightRemote.
     */
    @Ignore
    public void testSetPowerState() throws Exception {
        System.out.println("setPowerState");
        PowerType.Power.PowerState state = PowerType.Power.PowerState.ON;
        lightRemote.setPowerState(state);
        while (!lightRemote.getData().getPowerState().equals(state)) {
            Thread.yield();
        }
        assertTrue("Color has not been set in time!", lightRemote.getData().getPowerState().equals(state));
    }

    /**
     * Test of notifyUpdated method, of class LightRemote.
     */
    @Ignore
    public void testNotifyUpdated() {
    }

    public static class DeviceInitializerImpl implements de.citec.dal.util.DeviceInitializer {

        @Override
        public void initDevices(final DALRegistry registry) {

//            try {
//                registry.register(new PH_Hue_E27Controller("PH_Hue_E27_000", label, location));
//            } catch (DALException | VerificationFailedException ex) {
//                logger.warn("Could not initialize unit test device!", ex);
//            }
        }
    }
}
