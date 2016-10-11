package org.openbase.bco.dal.remote.service;

/*
 * #%L
 * DAL Remote
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import java.awt.Color;
import java.util.Collection;
import org.openbase.bco.dal.lib.layer.service.collection.ColorStateOperationServiceCollection;
import org.openbase.bco.dal.lib.layer.service.operation.ColorStateOperationService;
import org.openbase.bco.dal.lib.transform.HSBColorToRGBColorTransformer;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.service.ServiceTemplateType;
import rst.domotic.state.ColorStateType.ColorState;
import rst.timing.TimestampType.Timestamp;
import rst.vision.ColorType;
import rst.vision.HSBColorType.HSBColor;

/**
 *
 * * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ColorStateServiceRemote extends AbstractServiceRemote<ColorStateOperationService, ColorState> implements ColorStateOperationServiceCollection {

    public ColorStateServiceRemote() {
        super(ServiceTemplateType.ServiceTemplate.ServiceType.COLOR_STATE_SERVICE);
    }

    @Override
    public Collection<ColorStateOperationService> getColorStateOperationServices() {
        return getServices();
    }

    /**
     * {@inheritDoc}
     * Computes the average RGB color.
     *
     * @throws CouldNotPerformException {@inheritDoc}
     */
    @Override
    protected void computeServiceState() throws CouldNotPerformException {
        double averageRed = 0;
        double averageGreen = 0;
        double averageBlue = 0;
        int amount = getColorStateOperationServices().size();
        Collection<ColorStateOperationService> colorStateOperationServicCollection = getColorStateOperationServices();
        for (ColorStateOperationService service : colorStateOperationServicCollection) {
            if (!((UnitRemote) service).isDataAvailable()) {
                amount--;
                continue;
            }

            Color color = HSBColorToRGBColorTransformer.transform(service.getColorState().getColor().getHsbColor());
            averageRed += color.getRed();
            averageGreen += color.getGreen();
            averageBlue += color.getBlue();
        }
        averageRed = averageRed / amount;
        averageGreen = averageGreen / amount;
        averageBlue = averageBlue / amount;

        HSBColor hsbColor = HSBColorToRGBColorTransformer.transform(new Color((int) averageRed, (int) averageGreen, (int) averageBlue));
        serviceState = ColorState.newBuilder().setColor(ColorType.Color.newBuilder().setType(ColorType.Color.Type.HSB).setHsbColor(hsbColor)).setTimestamp(Timestamp.newBuilder().setTime(System.currentTimeMillis())).build();
    }

    @Override
    public ColorState getColorState() throws NotAvailableException {
        return getServiceState();
    }
}
