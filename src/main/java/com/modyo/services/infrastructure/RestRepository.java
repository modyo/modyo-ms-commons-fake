package com.modyo.services.infrastructure;


/**
 * Repositorio base para crear un repositorio
 */

public interface RestRepository<T, F> {

  F execute(T request);
}
