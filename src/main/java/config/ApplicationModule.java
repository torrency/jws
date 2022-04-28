package config;

import ga.rugal.jws.core.service.JwsService;
import ga.rugal.jws.core.service.impl.JwsServiceImpl;

import dagger.Binds;
import dagger.Module;

/**
 * Module regarding application.
 *
 * @author Rugal Bernstein
 */
@Module
public interface ApplicationModule {

  @Binds
  JwsService bindJwsService(JwsServiceImpl impl);
}
