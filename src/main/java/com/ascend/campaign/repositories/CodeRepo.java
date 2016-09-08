package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CodeRepo extends JpaRepository<Code, Long> {
    Page<Code> findByCodeDetail(Long codeDetail, Pageable pageRequest);

    List<Code> findByCodeDetail(Long codeDetail);

    Code findById(Long codeId);

    Page<Code> findByCode(String code, Pageable pageRequest);

    Code findByCode(String code);

    @Query(value = "SELECT a.promotion_id FROM code_detail a INNER JOIN code b"
            + " WHERE b.detail_id = a.id AND b.code = :code",
            nativeQuery = true)
    List<Long> findPromotionByCode(@Param("code") String code);

    List<Code> removeByCodeDetail(Long codeId);

    Long deleteByCodeDetail(Long codeId);
}
