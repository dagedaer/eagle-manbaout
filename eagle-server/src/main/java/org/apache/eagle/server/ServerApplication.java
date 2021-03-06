/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.typesafe.config.Config;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.apache.eagle.alert.coordinator.Coordinator;
import org.apache.eagle.alert.resource.SimpleCORSFiler;
import org.apache.eagle.app.service.ApplicationHealthCheckService;
import org.apache.eagle.app.service.ApplicationProviderService;
import org.apache.eagle.app.spi.ApplicationProvider;
import org.apache.eagle.common.Version;
import org.apache.eagle.log.base.taggedlog.EntityJsonModule;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.repo.EntityRepositoryScanner;
import org.apache.eagle.metadata.service.ApplicationStatusUpdateService;
import org.apache.eagle.server.security.BasicAuthBuilder;
import org.apache.eagle.server.security.BasicAuthResourceFilterFactory;
import org.apache.eagle.server.task.ManagedService;
import org.apache.eagle.server.module.GuiceBundleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static org.apache.eagle.app.service.impl.ApplicationHealthCheckServiceImpl.HEALTH_CHECK_PATH;

public class ServerApplication extends Application<ServerConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(ServerApplication.class);
    @Inject
    private ApplicationStatusUpdateService applicationStatusUpdateService;
    @Inject
    private ApplicationHealthCheckService applicationHealthCheckService;
    @Inject
    private ApplicationProviderService applicationProviderService;
    @Inject
    private Injector injector;
    @Inject
    private Config config;

    @Override
    public void initialize(Bootstrap<ServerConfig> bootstrap) {
        LOG.debug("Loading and registering guice bundle");
        GuiceBundle<ServerConfig> guiceBundle = GuiceBundleLoader.load();
        bootstrap.addBundle(guiceBundle);

        LOG.debug("Loading and registering static AssetsBundle on /assets");
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html", "/"));

        LOG.debug("Initializing guice injector context for current ServerApplication");
        guiceBundle.getInjector().injectMembers(this);

        try {
            EntityRepositoryScanner.scan();
        } catch (IllegalAccessException | InstantiationException e) {
            LOG.error("Failed to scan entity repository", e);
        }
    }

    @Override
    public String getName() {
        return ServerConfig.getServerName();
    }

    @Override
    public void run(ServerConfig configuration, Environment environment) throws Exception {
        environment.getApplicationContext().setContextPath(ServerConfig.getContextPath());
        environment.jersey().register(RESTExceptionMapper.class);
        environment.jersey().setUrlPattern(ServerConfig.getApiBasePath());
        environment.getObjectMapper().setFilters(TaggedLogAPIEntity.getFilterProvider());
        environment.getObjectMapper().registerModule(new EntityJsonModule());

        // Automatically scan all REST resources
        new PackagesResourceConfig(ServerConfig.getResourcePackage()).getClasses().forEach(environment.jersey()::register);

        // Swagger resources
        environment.jersey().register(ApiListingResource.class);

        BeanConfig swaggerConfig = new BeanConfig();
        swaggerConfig.setTitle(ServerConfig.getServerName());
        swaggerConfig.setVersion(ServerConfig.getServerVersion().version);
        swaggerConfig.setBasePath(ServerConfig.getApiBasePath());
        swaggerConfig.setResourcePackage(ServerConfig.getResourcePackage());
        swaggerConfig.setLicense(ServerConfig.getLicense());
        swaggerConfig.setLicenseUrl(ServerConfig.getLicenseUrl());
        swaggerConfig.setDescription(Version.str());
        swaggerConfig.setScan(true);

        // Simple CORS filter
        environment.servlets().addFilter(SimpleCORSFiler.class.getName(), new SimpleCORSFiler())
            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        // Register authentication provider
        BasicAuthBuilder authBuilder = new BasicAuthBuilder(configuration.getAuthConfig(), environment);
        environment.jersey().register(authBuilder.getBasicAuthProvider());
        if (configuration.getAuthConfig().isEnabled()) {
            environment.jersey().getResourceConfig().getResourceFilterFactories()
                .add(new BasicAuthResourceFilterFactory(authBuilder.getBasicAuthenticator()));
        }
        registerAppServices(environment);
    }

    private void registerAppServices(Environment environment) {
        LOG.debug("Registering CoordinatorService");
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                Coordinator.startSchedule();
            }

            @Override
            public void stop() throws Exception {

            }
        });

        // Run application status service in background
        LOG.debug("Registering ApplicationStatusUpdateService");
        Managed updateAppStatusTask = new ManagedService(applicationStatusUpdateService);
        environment.lifecycle().manage(updateAppStatusTask);

        // Initialize application extended health checks.
        if (config.hasPath(HEALTH_CHECK_PATH)) {
            LOG.debug("Registering ApplicationHealthCheckService");
            applicationHealthCheckService.init(environment);
            environment.lifecycle().manage(new ManagedService(applicationHealthCheckService));
        }

        // Load application shared extension services.
        LOG.debug("Registering application shared extension services");
        for (ApplicationProvider<?> applicationProvider : applicationProviderService.getProviders()) {
            applicationProvider.getSharedServices(config).ifPresent((services -> {
                services.forEach(service -> {
                    LOG.info("Registering {} for {}", service.getClass().getCanonicalName(),applicationProvider.getApplicationDesc().getType());
                    injector.injectMembers(service);
                    environment.lifecycle().manage(new ManagedService(service));
                });
                LOG.info("Registered {} services for {}", services.size(), applicationProvider.getApplicationDesc().getType());
            }));
        }
    }
}