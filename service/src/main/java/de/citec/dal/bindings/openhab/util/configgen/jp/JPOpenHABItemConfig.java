/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.bindings.openhab.util.configgen.jp;

import de.citec.jps.core.JPService;
import de.citec.jps.exception.JPNotAvailableException;
import de.citec.jps.preset.AbstractJPFile;
import de.citec.jps.tools.FileHandler;
import java.io.File;

/**
 *
 * @author mpohling
 */
public class JPOpenHABItemConfig extends AbstractJPFile {
    private static final String[] COMMAND_IDENTIFIERS = {"--item-config"};

    public JPOpenHABItemConfig() {
        super(COMMAND_IDENTIFIERS, FileHandler.ExistenceHandling.Must, FileHandler.AutoMode.On);
    }

    @Override
    protected File getPropertyDefaultValue() throws JPNotAvailableException {
        return new File(new File(JPService.getProperty(JPOpenHABConfiguration.class).getValue(), "items"), "dal-generated.items");
    }

    @Override
    public String getDescription() {
        return "Define the openhab item config file which is generated by dal.";
    }

}
