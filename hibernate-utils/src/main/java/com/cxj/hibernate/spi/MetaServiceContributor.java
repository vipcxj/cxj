package com.cxj.hibernate.spi;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceContributor;

/**
 * Created by vipcxj on 2018/8/10.
 */
public class MetaServiceContributor implements ServiceContributor {
    @Override
    public void contribute(StandardServiceRegistryBuilder serviceRegistryBuilder) {
        serviceRegistryBuilder.addInitiator(MetaServiceInitiator.INSTANCE);
    }
}
