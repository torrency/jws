package config;

import javax.inject.Singleton;

import ga.rugal.torrency.jws.core.service.JwsDecodeService;

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

  JwsDecodeService jwsDecodeService();
}
