package com.evo.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractDomainRepository<D, E, ID> implements DomainRepository<D, ID> {

    private final JpaRepository<E, ID> repository;
    private final DomainEntityMapper<D, E> mapper;

    protected AbstractDomainRepository(JpaRepository<E, ID> repository, DomainEntityMapper<D, E> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<D> findById(ID id) {
        return repository.findById(id).map(mapper::toDomain).map(this::enrich);
    }

    @Override
    public List<D> findAllByIds(Collection<ID> ids) {
        return enrichList(repository.findAllById(ids).stream().map(mapper::toDomain).toList());
    }

    @Override
    public List<D> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public D save(D domain) {
        saveAll(List.of(domain));
        return domain;
    }

    @Override
    public Collection<D> saveAll(Collection<D> domains) {
        List<E> entities = domains.stream().map(mapper::toEntity).collect(Collectors.toList());
        List<E> savedEntities = repository.saveAll(entities);
        return savedEntities.stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    protected D enrich(D domain) {
        List<D> domains = List.of(domain);
        return this.enrichList(domains).getFirst();
    }

    protected List<D> enrichList(List<D> domains) {
        return domains;
    }
}
