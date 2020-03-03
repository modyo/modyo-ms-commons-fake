package com.modyo.commons.core.repository;


/**
 * Interface base para crear un repositorio REST
 */

public interface RestRepository<T, F> {

  F execute(T request);
}
