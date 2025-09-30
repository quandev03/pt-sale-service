package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.OrganizationUserEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUserEntity, String>{
    Optional<OrganizationUserEntity> findByUserId(String userId);

    List<OrganizationUserEntity> getAllOrganizationUsersByOrgId(String orgId);

    @Query("SELECT o from OrganizationUserEntity o where (:userName IS NULL OR UPPER(o.userName) = UPPER(:userName)) AND (:orgId IS NULL OR o.orgId = :orgId) ORDER BY o.userName ASC")
    List<OrganizationUserEntity> getByUnit(@Param("userName") String username, @Param("orgId") String orgId);

    @Query("SELECT o from OrganizationUserEntity o where (:userName IS NULL OR UPPER(o.userName) = UPPER(:userName)) AND (:orgId IS NULL OR o.orgId = :orgId) ORDER BY o.userName ASC")
    Page<OrganizationUserEntity> getByUnit(@Param("userName") String username, @Param("orgId") String orgId, Pageable page);

    @Modifying
    @Query(value = "delete from ORGANIZATION_USER where USER_ID = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") String userId);

    @Query(value = "select o from OrganizationUserEntity o where o.userId = :userId")
    List<OrganizationUserEntity> findOrgByUserId(@Param("userId") String userId);

    @Modifying
    @Query(value = "delete from ORGANIZATION_USER where USER_ID in :userIds", nativeQuery = true)
    void deleteByUserIdIn(@Param("userIds") List<String> userIds);

    OrganizationUserEntity findByUserIdAndIsCurrent(String currentUserId, Integer aTrue);

    @Query("select count(o) > 0 from OrganizationUserEntity o where o.userId = :userId and o.orgId = :orgId")
    boolean existsByUserIdAndOrgId(@Param("userId") String userId, @Param("orgId") String orgId);

    @Query(value = """
        SELECT
              ou.ID AS CHILD_ID,
              ou.ORG_NAME AS CHILD_NAME
        FROM
              ORGANIZATION_USER our
        JOIN  ORGANIZATION_UNIT ou
          ON
              our.ORG_id = ou.ID
        WHERE
              our.IS_CURRENT = 1 AND our.USER_ID = :userId and our.STATUS = :status and ou.STATUS = :status
        """, nativeQuery = true)
    Tuple getOrganizationUserIdCurrent(@Param("userId") String userId, Integer status);

    @Query(value = """
        SELECT
              ou.ID AS ORG_ID, ou.ORG_CODE,
              ou.ORG_NAME AS ORG_NAME, our.IS_CURRENT
        FROM
              ORGANIZATION_USER our
        JOIN  ORGANIZATION_UNIT ou
          ON
              our.ORG_id = ou.ID
        WHERE
             our.USER_ID = :userId
             and our.STATUS = :status
             and ou.STATUS = :status
        """, nativeQuery = true)
    List<Tuple> getOrganizationUser(@Param("userId") String userId, Integer status);

    @Query(value = """
        select count(*) over () as TOTAL, o.USER_ID, o.USER_NAME, o.USER_FULLNAME,
               listagg(ou.ORG_NAME, ', ') within group (order by ORG_ID) as ORG_NAME
        from ORGANIZATION_USER o
        join ORGANIZATION_UNIT ou on o.ORG_ID = ou.ID
        where o.CLIENT_ID = :clientId
        and (:q is null or o.USER_NAME like :q or o.USER_FULLNAME like :q)
        and o.STATUS = 1
        group by o.USER_ID, o.USER_NAME, o.USER_FULLNAME
        offset :offset rows fetch next :row rows only
    """, nativeQuery = true)
    List<Tuple> getUserByClientId(@Param("q") String q, @Param("clientId") String clientId,
                                  @Param("offset") long offset, @Param("row") int pageSize);

    @Query(value = """
        SELECT
            ou.id as CHILD_ID,
            ou.ORG_NAME as CHILD_NAME,
            ou.PARENT_ID as PARENT_ID,
            CASE
                WHEN ou.id = :id THEN 1
                ELSE 0
            END as IS_CURRENT
        FROM organization_unit ou
        where CLIENT_ID = :clientId
        START WITH id = :id
        CONNECT BY PRIOR id = parent_id
    """, nativeQuery = true)
    List<Tuple> findAllChildByParentId(String id, String clientId);

    @Query(value = """
        SELECT oun.ORG_NAME FROM ORGANIZATION_USER ou
        JOIN ORGANIZATION_UNIT oun ON ou.ORG_ID  = oun.ID
        WHERE ou.USER_ID = :userId
    """, nativeQuery = true)
    String findByOrganizationUnitByUserId(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE ORGANIZATION_USER ou\s
        SET ou.ORG_ID  = :orgID
        WHERE ou.USER_ID = :userID
    """, nativeQuery = true)
    void updateOrgUnit(@Param("orgID") String orgId, @Param("userID") String userId);

    @Query(value = """
        SELECT ous.USER_ID as USER_ID, ou.ORG_NAME as ORG_NAME
        FROM ORGANIZATION_USER ous
        JOIN ORGANIZATION_UNIT ou ON ous.ORG_ID = ou.ID
        START WITH ou.ID = :parentId
        CONNECT BY PRIOR ou.ID = ou.PARENT_ID
    """, nativeQuery = true)
    List<Tuple> getChildUserTuples(@Param("parentId") String parentId);
}
