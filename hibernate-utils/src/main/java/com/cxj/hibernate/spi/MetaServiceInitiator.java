package com.cxj.hibernate.spi;

import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

import java.util.Map;

/**
 * Created by vipcxj on 2018/8/10.
 */
public class MetaServiceInitiator implements StandardServiceInitiator<MetaService> {

    public final static MetaServiceInitiator INSTANCE = new MetaServiceInitiator();

    @Override
    public Class<MetaService> getServiceInitiated() {
        return MetaService.class;
    }

    @Override
    public MetaService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new MetaServiceImpl();
    }
}
