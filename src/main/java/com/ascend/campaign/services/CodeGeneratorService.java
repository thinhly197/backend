package com.ascend.campaign.services;


import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Code;
import com.ascend.campaign.entities.CodeDetail;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.exceptions.CodeTypeException;
import com.ascend.campaign.models.CodeGeneratorRequest;
import com.ascend.campaign.repositories.CodeDetailRepo;
import com.ascend.campaign.repositories.CodeRepo;
import com.ascend.campaign.utils.CodeGenerateUtil;
import com.ascend.campaign.utils.JSONUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CodeGeneratorService {
    @NonNull
    private final CodeRepo codeRepo;

    @NonNull
    private final CodeDetailRepo codeDetailRepo;

    private CodeGenerateUtil codeGenerateUtil;

    @Autowired
    public CodeGeneratorService(CodeRepo codeRepo, CodeDetailRepo codeDetailRepo, CodeGenerateUtil codeGenerateUtil) {
        this.codeRepo = codeRepo;
        this.codeDetailRepo = codeDetailRepo;
        this.codeGenerateUtil = codeGenerateUtil;
    }

    public CodeDetail codeGenerator(CodeGeneratorRequest codeGeneratorRequest) {

        if (!handleCodeType(codeGeneratorRequest.getType())) {
            throw new CodeTypeException();
        }
        log.warn("content={\"activity\":\"Generate code\", \"msg\":{}}", JSONUtil.toString(codeGeneratorRequest));
        CodeDetail codeDetail = generateCodeDetail(codeGeneratorRequest);
        codeDetail = codeDetailRepo.save(codeDetail);

        List<Code> codes = generateCode(codeDetail.getId(), codeGeneratorRequest);
        if (codes.size() > 0) {
            codes = codeRepo.save(codes);
            codeDetail.setCodes(codes);
            codeDetail.refreshCode();
            codeDetail = codeDetailRepo.save(codeDetail);
            return codeDetail;
        } else {
            codeDetailRepo.delete(codeDetail);
            CodeDetail failResult = new CodeDetail();
            failResult.setId(-1L);
            return failResult;
        }
    }

    private CodeDetail generateCodeDetail(CodeGeneratorRequest codeGeneratorRequest) {
        CodeDetail codeDetail = new CodeDetail();
        codeDetail.setName(codeGeneratorRequest.getName());
        codeDetail.setCodeStatus(CampaignEnum.ACTIVATE.getContent());
        codeDetail.setCodeUsed(0L);
        codeDetail.setPromotionId(codeGeneratorRequest.getPromotionId());
        codeDetail.setLimitOfTimeOrUser(codeGeneratorRequest.getLimitOfTimeOrUser());

        if (CampaignEnum.SINGLE.getContent().equals(codeGeneratorRequest.getType())
                || CampaignEnum.VIP.getContent().equals(codeGeneratorRequest.getType())) {
            String codeType;
            codeType = CampaignEnum.VIP.getContent().equals(codeGeneratorRequest.getType())
                    ? CampaignEnum.VIP.getContent() : CampaignEnum.SINGLE.getContent();
            codeDetail.setCodeType(codeType);
            codeDetail.setAvailable(codeGeneratorRequest.getLimitOfUse());
            codeDetail.setTypeOfLimitation(codeGeneratorRequest.getTypeOfLimitation());
            codeDetail.setLimitOfTimeOrUser(codeGeneratorRequest.getLimitOfTimeOrUser());
            codeDetail.setCodeFormat(codeGeneratorRequest.getFormat());
        } else if (CampaignEnum.UNIQUE.getContent().equals(codeGeneratorRequest.getType())) {
            codeDetail.setCodeType(CampaignEnum.UNIQUE.getContent());
            codeDetail.setAvailable(codeGeneratorRequest.getQuantity());
        }

        codeDetail.setCodeFormatPrefix(codeGeneratorRequest.getPrefix());
        codeDetail.setCodeFormatSuffix(codeGeneratorRequest.getSuffixLength());

        return codeDetail;
    }

    private List<Code> generateCode(Long codeDetailId, CodeGeneratorRequest codeGeneratorRequest) {
        List<Code> codes = Lists.newArrayList();
        Set<Code> allCode = codeRepo.findAll().stream().collect(Collectors.toSet());
        Set<String> allCodeString = allCode.stream().map(Code::getCode).collect(Collectors.toSet());

        if (allCodeString.contains(codeGeneratorRequest.getCode())) {
            return codes;
        }

        String prefix = Strings.nullToEmpty(codeGeneratorRequest.getPrefix());
        int suffixLength = codeGeneratorRequest.getSuffixLength() == null ? 0 : codeGeneratorRequest.getSuffixLength();
        int limitOfTimeOrUser;
        try {
            limitOfTimeOrUser = Integer.parseInt(codeGeneratorRequest.getLimitOfTimeOrUser().toString());
        } catch (Exception e) {
            limitOfTimeOrUser = 0;
        }

        if (CampaignEnum.SINGLE.getContent().equals(codeGeneratorRequest.getType())
                || CampaignEnum.VIP.getContent().equals(codeGeneratorRequest.getType())) {
            if (CampaignEnum.FIX.getContent().equals(codeGeneratorRequest.getFormat())) {
                Code code = new Code();
                code.setCode(codeGeneratorRequest.getCode());
                code.setStatus(CampaignEnum.ACTIVATE.getContent());
                code.setRevenue(0.0);
                code.setAvailable(codeGeneratorRequest.getLimitOfUse());
                code.setUse(0L);
                code.setCodeDetail(codeDetailId);
                code.setLimitOfTimeOrUser(limitOfTimeOrUser);
                codes.add(code);
            } else {
                if (CampaignEnum.RANDOM.getContent().equals(codeGeneratorRequest.getFormat())) {
                    codes = codeGenerateUtil.genSuffixCode(prefix, 1, suffixLength,
                            codeDetailId, allCodeString);
                    if (codes.size() > 0) {
                        codes.get(0).setLimitOfTimeOrUser(limitOfTimeOrUser);
                        codes.get(0).setAvailable(codeGeneratorRequest.getLimitOfUse());
                    } else {
                        codes = new ArrayList<>();
                    }
                }
            }
        } else {
            if (CampaignEnum.UNIQUE.getContent().equals(codeGeneratorRequest.getType())) {
                codes = codeGenerateUtil.genSuffixCode(prefix, codeGeneratorRequest.getQuantity(),
                        suffixLength, codeDetailId, allCodeString);
            }
        }
        return codes;
    }

    public CodeDetail getCodeDetail(Long codeId) {
        return Optional.ofNullable(codeDetailRepo.findOne(codeId)).orElseThrow(CodeNotFoundException::new);
    }

    public Page<Code> getCodePage(Long codeId, int page, int number, Sort.Direction direction, String sort) {
        return codeRepo.findByCodeDetail(codeId, genPageRequest(page - 1, number, direction, sort));
    }

    public Page<CodeDetail> getAllCodeSet(int page, int number, Sort.Direction direction, String sort,
                                          Long searchID, String searchName, String type) {
        return codeDetailRepo.findAll(filterCodeDetailCriteria(searchID, searchName, type),
                genPageRequest(page - 1, number, direction, sort));
    }

    public CodeDetail updateCodeDetail(Long codeSetId, CodeGeneratorRequest codeGeneratorRequest) {
        log.warn("content={\"activity\":\"Update Code Detail\", \"msg\":{\"code_set_id\":\"{}\", \"code_detail\":{}}}",
                codeSetId, JSONUtil.toString(codeGeneratorRequest));
        Optional<CodeDetail> codeDetailOpt = Optional.ofNullable(codeDetailRepo.findOne(codeSetId));
        CodeDetail codeDetailEdit = codeDetailOpt.orElseThrow(CodeNotFoundException::new);

        codeDetailEdit.setName(codeGeneratorRequest.getName());

        if (CampaignEnum.SINGLE.getContent().equals(codeGeneratorRequest.getType())
                || CampaignEnum.VIP.getContent().equals(codeGeneratorRequest.getType())) {
            codeDetailEdit.setLimitOfTimeOrUser(codeGeneratorRequest.getLimitOfTimeOrUser());
            codeDetailEdit.setAvailable(codeGeneratorRequest.getLimitOfUse());

            Optional<Code> codeOpt = Optional.ofNullable(codeRepo.findByCodeDetail(codeDetailEdit.getId()).get(0));
            Code codeEdit = codeOpt.orElseThrow(CodeNotFoundException::new);
            codeEdit.setAvailable(codeGeneratorRequest.getLimitOfUse());
            codeEdit.setLimitOfTimeOrUser(codeGeneratorRequest.getLimitOfTimeOrUser());
            codeRepo.save(codeEdit);
        }

        return codeDetailRepo.save(codeDetailEdit);
    }

    private PageRequest genPageRequest(int page, int number, Sort.Direction direction, String sort) {
        return new PageRequest(page, number, new Sort(direction, sort));
    }

    public Page<Code> getAllCode(Integer page, Integer perPage, Sort.Direction direction, String sort, String search) {
        if (search.length() > 0) {
            return codeRepo.findByCode(search, genPageRequest(page - 1, perPage, direction, sort));
        }
        return codeRepo.findAll(genPageRequest(page - 1, perPage, direction, sort));
    }

    public Code getCode(Long codeId) {
        return Optional.ofNullable(codeRepo.findById(codeId)).orElseThrow(CodeNotFoundException::new);
    }

    public Long isCodeCanApply(String code) {
        Code codes = codeRepo.findByCode(code);
        if (codes == null) {
            return 0L;
        }
        return codes.getCodeDetail();
    }

    public List<Long> findPromotionFromCode(String code) {
        return Optional.ofNullable(codeRepo.findPromotionByCode(code)).orElseThrow(CodeNotFoundException::new);
    }

    public Page<CodeDetail> searchCodeDetailByName(String codeSet, Integer page, Integer perPage,
                                                   Sort.Direction direction, String sort) {
        return codeDetailRepo.findByNameLike(codeSet, genPageRequest(page - 1, perPage, direction, sort));
    }

    @Transactional
    public CodeDetail deleteCode(Long codeId) {
        Long codes = codeRepo.deleteByCodeDetail(codeId);
        codeRepo.flush();
        CodeDetail codeDetailOpt = Optional.ofNullable(codeDetailRepo.findOne(codeId))
                .orElseThrow(CodeNotFoundException::new);
        codeDetailRepo.delete(codeDetailOpt);
        codeDetailRepo.flush();
        return codeDetailOpt;

    }

    public Specification<CodeDetail> filterCodeDetailCriteria(
            Long codeId, String searchName, String codeType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (codeId != null) {
                predicates.add(cb.equal(root.get("id"), codeId));
            }
            if (!StringUtils.isEmpty(searchName)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
            }
            if (!StringUtils.isEmpty(codeType)) {
                predicates.add(cb.equal(root.get("codeType"), codeType));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private boolean handleCodeType(String codeType) {
        log.info("content={\"activity\":\"Check Code Type\", \"msg\":\"{}\"}", codeType);

        List<String> codeTypes = Arrays.asList(
                CampaignEnum.SINGLE.getContent(),
                CampaignEnum.VIP.getContent(),
                CampaignEnum.UNIQUE.getContent());
        return codeTypes.contains(codeType);

    }
}


