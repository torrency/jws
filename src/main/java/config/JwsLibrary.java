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
    AssembleModule.class
  }
)
public interface JwsLibrary {

  JwsService jwsService();
}
