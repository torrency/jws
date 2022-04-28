package config;

import javax.inject.Singleton;

import ga.rugal.jws.core.service.JwsService;

import dagger.Component;

/**
 * Application assembler.
 *
 * @author Rugal Bernstein
 */
@Singleton
@Component(
  modules = {
    ApplicationModule.class
  }
)
public interface JwsApplication {

  JwsService jwsService();
}
