package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;


import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.BaseJPARepository;
import com.vnsky.bcss.projectbase.shared.utils.PreSaveCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;


@SuppressWarnings("all")
// use default only class of adapter extend
public class BaseJPAAdapter<T, ID, R extends BaseJPARepository<T, ID>> {

    private static final Logger log = LoggerFactory.getLogger(BaseJPAAdapter.class);
    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    protected R repository;

    public BaseJPAAdapter() {
        //document why this constructor is empty
    }

    public List<T> findAll() {
        return this.repository.findAll();
    }

    public List<T> findAll(Specification<T> specification) {
        return this.repository.findAll(specification);
    }

    public Page<T> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    public List<T> findAll(Sort sort) {
        return this.repository.findAll(sort);
    }

    public T get(ID id) {
        return this.repository.findById(id).orElse(null);
    }

    public T save(T entity) {
        return this.repository.save(entity);
    }

    public List<T> saveAll(List<T> entity) {
        return this.repository.saveAll(entity);
    }

    @PreSaveCheck
    public T saveAndFlush(T entity) {
        return this.repository.saveAndFlush(entity);
    }

    public List<T> saveAllAndFlush(List<T> entity) {
        List<T> temp = this.repository.saveAll(entity);
        this.repository.flush();
        return temp;
    }

    public T update(T entity) {
        return this.repository.save(entity);
    }

    public void delete(ID id) {
        this.repository.deleteById(id);
    }

    public Object getFromTableAndColumnAndValue(String table, String column, Object idValue) {
        String sql = String.format("SELECT %s FROM %s WHERE %s = :%s", column, table, column, column);
        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(column, idValue), (ResultSetExtractor<Object>) rs -> {
            if (rs.next()) return rs.getArray(column);
            else return null;
        });
    }

    public boolean isExistsCodeWithDifferenceId(String table, String column, Object id, Object fieldValue) {
        String sql = String.format("SELECT %s FROM %s WHERE %s = :%s AND id != :id", column, table, column, column);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(column, fieldValue).addValue("id", id), rs -> {
            return rs.next();
        }));
    }
}
