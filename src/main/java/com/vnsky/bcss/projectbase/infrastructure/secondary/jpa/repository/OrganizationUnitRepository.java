package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.OrganizationDeliveryInfoEntity;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationUnitRepository extends BaseJPARepository<OrganizationUnitEntity, String> {
    @Query(value = "SELECT o FROM OrganizationUnitEntity o WHERE o.orgCode = :code AND (:id IS NULL OR o.id <> :id) AND o.orgType = :orgType AND o.clientId = :clientId")
    Optional<OrganizationUnitEntity> findByCode(@Param("id") String id, @Param("code") String code, @Param("orgType") String orgType, @Param("clientId") String clientId);

    @Query(value = "select o from OrganizationUnitEntity o where o.orgCode = :code")
    Optional<OrganizationUnitEntity> findByCode(@Param("code") String code);

    @Query(value = """
            SELECT
                o.ID,
                o.PARENT_ID,
                o.ORG_CODE,
                o.ORG_NAME,
                o.CREATED_BY,
                o.CREATED_DATE,
                o.MODIFIED_BY,
                o.MODIFIED_DATE,
                o.STATUS,
                o.CLIENT_ID
            FROM ORGANIZATION_UNIT o
            WHERE (:status IS NULL OR o.STATUS = :status)
              AND (
                    :textSearch IS NULL
                    OR (o.ORG_CODE LIKE '%' || :textSearch || '%'
                     OR  o.ORG_NAME LIKE '%' || :textSearch || '%')
                  )
              AND o.ORG_TYPE    = :orgType
              AND o.CLIENT_ID   = :clientId
              AND (:orgSubType IS NULL OR o.ORG_SUB_TYPE = :orgSubType)
            CONNECT BY NOCYCLE PRIOR o.ID = o.PARENT_ID
            START WITH o.ID = :currentOrgId
            ORDER SIBLINGS BY o.MODIFIED_DATE DESC
            """, nativeQuery = true)
    List<Tuple> getAllOrganizationUnit(@Param("status") Integer status, @Param("orgType") String orgType, @Param("clientId") String clientId, String orgSubType, @Param("textSearch") String textSearch, @Param("currentOrgId") String currentOrgId);

    @Query(value = """
        SELECT o.ID,
               o.PARENT_ID,
               o.ORG_CODE,
               o.ORG_NAME,
               o.CREATED_BY,
               o.CREATED_DATE,
               o.MODIFIED_BY,
               o.MODIFIED_DATE,
               o.STATUS,
               o.CLIENT_ID
        FROM ORGANIZATION_UNIT o
        WHERE STATUS = 1
            AND ORG_TYPE = :orgType
            and o.id not in (Select ol.org_id from ORGANIZATION_LIMIT ol)
        """, nativeQuery = true)
    List<Tuple> getPartnersWithoutOrganizationLimit(String orgType);

    @Query(value = "SELECT o FROM OrganizationUnitEntity o WHERE o.parentId IS NULL AND (:id IS NULL OR o.id <> :id) AND o.orgType = :orgType AND o.clientId = :clientId")
    Optional<OrganizationUnitEntity> findOrgRoot(@Param("id") String id, @Param("orgType") String orgType, @Param("clientId") String clientId);

    @Query(value = """
      SELECT CASE
                 WHEN EXISTS(SELECT 1
                             FROM ORGANIZATION_UNIT parent_unit
                             WHERE ID = :id -- Kiểm tra đơn vị cha có ID là :parent_id
                               AND ORG_TYPE = :orgType
                               AND EXISTS (SELECT 1
                                           FROM ORGANIZATION_UNIT child_unit
                                           WHERE child_unit.PARENT_ID = parent_unit.ID
                                             AND child_unit.STATUS = :status -- Kiểm tra phần tử con đang hoạt động (STATUS = 1)
                             ))
                     THEN 1
                 ELSE 0
                 END
      FROM DUAL
      """, nativeQuery = true)
    int countOrganizationUnitActive(@Param("id") String id, @Param("status") Integer status, @Param("orgType") String orgType);

    @Query(value = """
      SELECT CASE
                 WHEN EXISTS(SELECT 1
                             FROM ORGANIZATION_UNIT parent_unit
                             WHERE ID = :id -- Kiểm tra đơn vị cha có ID là :parent_id
                               AND ORG_TYPE = :orgType
                               AND EXISTS (SELECT 1
                                           FROM ORGANIZATION_UNIT child_unit
                                           WHERE child_unit.PARENT_ID = parent_unit.ID
                             ))
                     THEN 1
                 ELSE 0
                 END
      FROM DUAL
      """, nativeQuery = true)
    int countChildOrganizationUnit(@Param("id") String id, @Param("orgType") String orgType);

    @Query(value = """
        select count(o) > 0 from OrganizationUnitEntity o
        where (:id is null or o.id <> :id)
        and o.orgCode = :code
        and o.orgType = :type
    """)
    boolean existsByCode(@Param("id") String id, @Param("code") String code, @Param("type") String type);

    @Query(value = """
        select count (*) over () as total, ID, ORG_CODE, ORG_NAME, ORG_SUB_TYPE,
               CREATED_DATE, CREATED_BY, MODIFIED_BY, MODIFIED_DATE, STATUS, APPROVAL_STATUS, CLIENT_ID
        from ORGANIZATION_UNIT
        where (:q is null or LOWER(ORG_CODE) like LOWER(:q) ESCAPE '\\'  or LOWER(ORG_NAME) like LOWER(:q) ESCAPE '\\' )
            and ORG_TYPE = :orgType
            and (:partnerType is null or ORG_SUB_TYPE = :partnerType)
            and (:status is null or STATUS = :status)
            and (:approvalStatus is null or APPROVAL_STATUS = :approvalStatus)
            and PARENT_ID is null
        order by MODIFIED_DATE desc
        offset :offset rows fetch next :pageSize rows only
    """, nativeQuery = true)
    List<Tuple> search(@Param("q") String q, @Param("orgType") String orgType, @Param("partnerType") String partnerType,
                       @Param("status") Integer status, @Param("approvalStatus") Integer approvalStatus, @Param("offset") long offset, @Param("pageSize") int pageSize);

    @Query(value = "select o from OrganizationUnitEntity o where o.id = :id")
    Optional<OrganizationUnitEntity> findByIdLazy(@Param("id") String id);

    @Query(value = "update ORGANIZATION_UNIT set STATUS = :status where id = :id", nativeQuery = true)
    @Modifying
    void updateStatus(@Param("id") String id, @Param("status") Integer status);

    @Query(value = "select o from OrganizationUnitEntity o where o.clientId = :clientId and o.orgType = :partnerType and o.parentId IS NULL")
    OrganizationUnitEntity getDetailPartnerByClientId(@Param("clientId") String clientId, @Param("partnerType") String partnerType);

    boolean existsByIdAndOrgType(String id, String orgType);

    @Query("""
        select o from OrganizationUnitEntity o
        where o.clientId = :clientId
        and o.orgType = :orgType
        and o.parentId is null
    """)
    OrganizationUnitEntity getOrgRootByClientId(@Param("clientId") String clientId, @Param("orgType") String orgType);

    @Query("""
        select o from OrganizationUnitEntity o
        where o.clientId = :clientId
        and o.orgType = :orgType
    """)
    OrganizationUnitEntity getOrgByClientId(@Param("clientId") String clientId, @Param("orgType") String orgType);

    @Query(value = """
        select o.id from OrganizationUnitEntity o
        where o.clientId = :clientId
        and o.parentId is null
        and o.orgType = :orgType
    """)
    Long getRootOrgByClientId(@Param("clientId") String clientId, @Param("orgType") String type);

    List<OrganizationUnitEntity> getAllByIdIn(List<Long> orgIds);

    List<OrganizationUnitEntity> getAllByOrgType(String orgType);

    @Query(value = "Select ou.id from OrganizationUnitEntity ou where ou.parentId = :parentId")
    List<Long> findByParentId(Long parentId);

    @Query(value = "select o from OrganizationUnitEntity o where o.orgCode = :orgCode and o.orgType = :orgType")
    OrganizationUnitEntity findByOrgCodeFromRoot(@Param("orgCode") String orgCode, @Param("orgType") String type);

    @Query("select o from OrganizationDeliveryInfoEntity o where o.orgId = :id")
    OrganizationDeliveryInfoEntity findDeliveryByOrgId(String id);

    @Query(value = """
        SELECT o.ID, o.ORG_CODE, o.ORG_NAME, o.CREATED_BY, o.CREATED_DATE,
               o.MODIFIED_BY, o.MODIFIED_DATE, o.STATUS
        FROM ORGANIZATION_UNIT o
        WHERE o.ORG_TYPE = :orgType
        and (:orgCode is null or o.ORG_CODE = :orgCode)
        and (:clientId is null or o.CLIENT_ID = :clientId)
    """, nativeQuery = true)
    List<Tuple> getAllUnitByClientId(@Param("orgCode") String orgCode, @Param("clientId") String clientId, @Param("orgType") String orgType);

    @Query(value = """
        SELECT o.ID, o.ORG_CODE, o.ORG_NAME, o.CREATED_BY, o.CREATED_DATE,
               o.MODIFIED_BY, o.MODIFIED_DATE, o.STATUS, o.EMAIL
        FROM ORGANIZATION_UNIT o
        WHERE o.CLIENT_ID <> '000000000000'
        and o.ORG_TYPE = :orgType
        and (:q is null or o.ORG_CODE like :q or o.ORG_NAME like :q)
    """, nativeQuery = true)
    List<Tuple> getAllOrganizationByType(String q, String orgType);

    @Query(value = """
        Select * from ORGANIZATION_UNIT where CLIENT_ID = :clientId and ORG_TYPE = :orgType and STATUS = :status
    """, nativeQuery = true)
    OrganizationUnitEntity getCurrentPartnerByUserIdAndClientId(String clientId, int status, String orgType);

    @Query(value = "update ORGANIZATION_UNIT set APPROVAL_STATUS = :status where id = :id", nativeQuery = true)
    @Modifying
    void updateApprovalStatus(String id, Integer status);

    @Query(value = "update ORGANIZATION_UNIT set CLIENT_ID = :clientId where id = :id", nativeQuery = true)
    @Modifying
    void updateClientId(String id, String clientId);

    @Query(value = "select o.* from ORGANIZATION_UNIT o where (o.ID like :orgIdentity or o.ORG_CODE = :orgIdentity) and o.CLIENT_ID = :clientId", nativeQuery = true)
    OrganizationUnitEntity getUnitByOrgIdentity( @Param("orgIdentity") String orgIdentity, @Param("clientId") String clientId);

    @Query(value = """
        SELECT o.ID, o.ORG_CODE, o.ORG_NAME, o.CREATED_BY, o.CREATED_DATE,
               o.MODIFIED_BY, o.MODIFIED_DATE, o.STATUS, o.PROVINCE_CODE,
               o.DISTRICT_CODE, o.WARD_CODE, o.ADDRESS, o.NOTE
        FROM ORGANIZATION_UNIT o
        WHERE o.CLIENT_ID = :clientId
        and o.ORG_SUB_TYPE = :orgSubType
        and o.ORG_TYPE = :orgType
        and (:status is null or o.STATUS = :status)
    """, nativeQuery = true)
    List<Tuple> getAllOrganizationUnitByStatusAndTypeAndClient(Integer status, String orgType, String orgSubType, String clientId);

    @Query(value = """
            SELECT ID, PARENT_ID , ORG_CODE , ORG_NAME FROM ORGANIZATION_UNIT
            WHERE ORG_TYPE = 'NBO'    """,
        nativeQuery = true)
    List<Tuple> getInfoOrganization();

    @Query(value = """
        SELECT o.ID, o.ORG_CODE, o.ORG_NAME, o.CREATED_BY, o.CREATED_DATE,
               o.MODIFIED_BY, o.MODIFIED_DATE, o.STATUS, o.PROVINCE_CODE,
               o.DISTRICT_CODE, o.WARD_CODE, o.ADDRESS, o.NOTE
        FROM ORGANIZATION_UNIT o
        JOIN ORGANIZATION_USER ou2 ON o.ID = ou2.ORG_ID AND ou2.USER_ID = :userId
        WHERE o.CLIENT_ID = :clientId
        and (:orgSubType is null or o.ORG_SUB_TYPE = :orgSubType)
        and o.ORG_TYPE = :orgType
        and (:status is null or o.STATUS = :status)
    """, nativeQuery = true)
    List<Tuple> getAllAuthorizedOrganization(Integer status, String orgType, String orgSubType, String clientId, String userId);

    @Query(value = """
        SELECT CASE
                 WHEN EXISTS (
                   SELECT 1
                   FROM ORGANIZATION_UNIT
                   START WITH ID = :parentId
                   CONNECT BY PRIOR ID = PARENT_ID
                   AND ID IS NOT NULL
                   AND ID = :childId
                 ) THEN 1
                 ELSE 0
               END AS IS_DESCENDANT
        FROM DUAL
    """, nativeQuery = true)
    int checkConnectParentId(String parentId, String childId);

    @Query(value = """
        SELECT *
        FROM ORGANIZATION_UNIT
        START WITH ID = :parentId
        CONNECT BY PRIOR ID = PARENT_ID
    """, nativeQuery = true)
    List<OrganizationUnitEntity> getListChildOrganizationUnit(@Param("parentId") String parentId);

    @Query(value = """
        SELECT ID, PARENT_ID , ORG_CODE , ORG_NAME
              FROM ORGANIZATION_UNIT ou
              where ou.CLIENT_ID = :clientId and ou.PARENT_ID is not null and AND ORG_TYPE = 'NBO'
    """, nativeQuery = true)
    List<Tuple> getInfoOrganizationByParentId(@Param("clientId") String orgId);

    @Query(value = """
        SELECT ou.*
        FROM ORGANIZATION_UNIT ou
        JOIN ORGANIZATION_USER ouser ON ou.ID = ouser.ORG_ID
        WHERE ouser.USER_ID = :userId
        FETCH FIRST 1 ROWS ONLY
    """, nativeQuery = true)
    OrganizationUnitEntity findByUserId(@Param("userId") String userId);

    @Query(value = """
        SELECT nbo.*
        FROM ORGANIZATION_UNIT nbo
        JOIN ORGANIZATION_UNIT partner ON partner.ORG_CODE = nbo.ORG_CODE
        WHERE partner.ID = :partnerId
            AND nbo.ORG_TYPE = 'NBO'
        FETCH FIRST 1 ROWS ONLY
    """, nativeQuery = true)
    OrganizationUnitEntity findNBOByPartnerId(@Param("partnerId") String partnerId);

    @Query(value = """
    SELECT so.ORG_ID
    FROM SALE_ORDER so
    JOIN ESIM_REGISTRATION er ON er.ORDER_ID = so.ID AND er.ID = :esimRegistrationId
    """, nativeQuery = true)
    String findOrganizationIdByEsimRegistration(@Param("esimRegistrationId") String esimRegistrationId);


    @Query(value = """
        Select * from ORGANIZATION_UNIT ou where ou.CLIENT_ID = :clientId and ou.PARENT_ID is NULL AND ORG_TYPE = 'NBO'    """
        , nativeQuery = true)
    OrganizationUnitEntity getOrgRoot(String clientId);
}
