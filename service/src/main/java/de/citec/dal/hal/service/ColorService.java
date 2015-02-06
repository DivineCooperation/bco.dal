/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.hal.service;

import de.citec.dal.hal.provider.ColorProvider;
import de.citec.jul.exception.CouldNotPerformException;
import rst.vision.HSVColorType;

/**
 *
 * @author mpohling
 */
public interface ColorService extends Service, ColorProvider {

    public void setColor(HSVColorType.HSVColor color) throws CouldNotPerformException;
}
