package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.CodeDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodeDetailRepo extends JpaRepository<CodeDetail, Long>,JpaSpecificationExecutor<CodeDetail> {
    Page<CodeDetail> findByNameLike(String codeName, Pageable pageRequest);
}
