package com.evo.support;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DomainRepository<D, ID> {
    Optional<D> findById(ID id);

    List<D> findAllByIds(Collection<ID> ids);

    List<D> findAll();

    D save(D domain);

    Collection<D> saveAll(Collection<D> domains);

    D getById(ID id);

    void existsById(ID id);
}
