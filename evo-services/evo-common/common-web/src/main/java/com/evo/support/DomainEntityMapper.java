package com.evo.support;

import java.util.List;

public interface DomainEntityMapper<D, E> {
    D toDomain(E entity);

    List<D> toDomainList(List<E> entities);

    E toEntity(D domain);

    List<E> toEntityList(List<D> domains);
}
