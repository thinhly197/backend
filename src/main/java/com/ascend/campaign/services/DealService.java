package com.ascend.campaign.services;

import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.entities.PromotionTask;
import com.ascend.campaign.exceptions.DealNotFoundException;
import com.ascend.campaign.exceptions.PDSServiceException;
import com.ascend.campaign.exceptions.PricingServiceException;
import com.ascend.campaign.models.PDSJson;
import com.ascend.campaign.models.PricingJson;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.SuperDeal;
import com.ascend.campaign.models.VariantDeal;
import com.ascend.campaign.models.VariantDealResponse;
import com.ascend.campaign.repositories.DealRepo;
import com.ascend.campaign.repositories.PromotionTaskRepo;
import com.ascend.campaign.utils.DealUtil;
import com.ascend.campaign.utils.JSONUtil;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DealService {
    @NonNull
    private final DealRepo dealRepo;

    @NonNull
    private final ExternalService externalService;

    @NonNull
    private final DealUtil dealUtil;

    @NonNull
    private PromotionTaskRepo promotionTaskRepo;

    @Autowired
    public DealService(DealRepo dealRepo, ExternalService externalService, DealUtil dealUtil,
                       PromotionTaskRepo promotionTaskRepo) {
        this.dealRepo = dealRepo;
        this.externalService = externalService;
        this.dealUtil = dealUtil;
        this.promotionTaskRepo = promotionTaskRepo;
    }

    public Deal createDeal(Deal deal) {
        deal.setConditionData(JSONUtil.toString(deal.getDealCondition()));
        Deal result = dealRepo.saveAndFlush(deal);
        log.warn("content={\"activity\":\"Create Deal\", \"msg\":{}}", JSONUtil.toString(result));

        PromotionTask promotionTaskStart = new PromotionTask();
        promotionTaskStart.setPromotionId(result.getId());
        promotionTaskStart.setIsStart(true);
        promotionTaskStart.setTriggerAt(result.getStartPeriod());
        promotionTaskRepo.saveAndFlush(promotionTaskStart);

        PromotionTask promotionTaskEnd = new PromotionTask();
        promotionTaskEnd.setPromotionId(result.getId());
        promotionTaskEnd.setIsStart(false);
        promotionTaskEnd.setTriggerAt(result.getEndPeriod());
        promotionTaskRepo.saveAndFlush(promotionTaskEnd);

        return result;
    }

    public Deal updateDeal(Long variantId, Deal deal) {
        log.warn("content={\"activity\":\"Update Deal\", \"msg\":{\"variant_id\":\"{}\", \"deal\":{}}}",
                variantId, JSONUtil.toString(deal));
        Deal foundDeal = Optional.ofNullable(dealRepo.findOne(variantId)).orElseThrow(DealNotFoundException::new);
        foundDeal.setName(deal.getName());
        foundDeal.setStartPeriod(deal.getStartPeriod());
        foundDeal.setEndPeriod(deal.getEndPeriod());
        foundDeal.setEnable(deal.getEnable());
        foundDeal.setChannelType(deal.getChannelType());
        foundDeal.setConditionData(JSONUtil.toString(deal.getDealCondition()));
        foundDeal.setDealCondition(deal.getDealCondition());
        foundDeal.setDescription(deal.getDescription());
        foundDeal.setSuperDeal(deal.getSuperDeal());
        foundDeal.setMember(deal.getMember());
        foundDeal.setImgUrl(deal.getImgUrl());
        foundDeal.setImgUrlEn(deal.getImgUrlEn());
        foundDeal.setNonMember(deal.getNonMember());
        foundDeal.setImgThmUrl(deal.getImgThmUrl());
        foundDeal.setImgThmUrlEn(deal.getImgThmUrlEn());
        foundDeal.setShortDescription(deal.getShortDescription());

        Deal updateDeal = dealRepo.saveAndFlush(foundDeal);
        updateDeal.setConditionData(JSONUtil.toString(updateDeal.getDealCondition()));
        updateDeal.setDealCondition(deal.getDealCondition());

        PromotionTask promotionTaskStart = promotionTaskRepo.findByPromotionIdAndIsStart(updateDeal.getId(), true);
        if (promotionTaskStart != null) {
            promotionTaskStart.setTriggerAt(updateDeal.getStartPeriod());
        } else {
            promotionTaskStart = new PromotionTask();
            promotionTaskStart.setPromotionId(updateDeal.getId());
            promotionTaskStart.setIsStart(true);
            promotionTaskStart.setTriggerAt(updateDeal.getStartPeriod());
        }
        promotionTaskRepo.saveAndFlush(promotionTaskStart);

        PromotionTask promotionTaskEnd = promotionTaskRepo.findByPromotionIdAndIsStart(updateDeal.getId(), false);
        if (promotionTaskEnd != null) {
            promotionTaskEnd.setTriggerAt(updateDeal.getEndPeriod());
        } else {
            promotionTaskEnd = new PromotionTask();
            promotionTaskEnd.setPromotionId(updateDeal.getId());
            promotionTaskEnd.setIsStart(false);
            promotionTaskEnd.setTriggerAt(updateDeal.getEndPeriod());
        }
        promotionTaskRepo.saveAndFlush(promotionTaskEnd);

        return updateDeal;
    }

    public Deal deleteDeal(Long dealId) {
        log.warn("content={\"activity\":\"Delete Deal\", \"msg\":{\"deal_id\":\"{}\"}}", dealId);
        Deal deal = Optional.ofNullable(dealRepo.findOne(dealId)).orElseThrow(DealNotFoundException::new);
        deal.setDealCondition(JSONUtil.parseToDealCondition(deal.getConditionData()));
        dealRepo.delete(deal);
        promotionTaskRepo.deleteByPromotionId(dealId);
        return deal;
    }

    public Page<Deal> getAllDeal(Integer page, Integer perPage, Sort.Direction direction,
                                 String sort, Long searchID, String searchName, Boolean enable,
                                 Boolean active, Long startPeriod, Long endPeriod,
                                 Boolean superDeal, Boolean recommended) {
        Page<Deal> deals = dealRepo.findAll(
                filterDealCriteria(searchID, searchName, enable, startPeriod, endPeriod, superDeal),
                genPageRequest(page - 1, perPage, direction, sort));
        deals.forEach(d -> d.setDealCondition(JSONUtil.parseToDealCondition(d.getConditionData())));
        deals.forEach(d -> d.setLive(dealUtil.isLive(d.getEnable(), d.getStartPeriod(), d.getEndPeriod())));
        if (active != null) {
            deals = filterIsLiveDeal(deals, active);
        }
        if (recommended != null) {
            deals = filterRecommendedDeal(deals, recommended);
        }

        return deals;
    }

    private Page<Deal> filterRecommendedDeal(Page<Deal> deals, Boolean recommended) {
        return null;
    }

    private Page<Deal> filterIsLiveDeal(Page<Deal> deals, Boolean active) {
        return new PageImpl(deals.getContent()
                .stream()
                .filter(x -> x.getLive() == active)
                .collect(Collectors.toList())
        );
    }

    private Specification<Deal> filterDealCriteria(Long searchID, String searchName,
                                                   Boolean enable, Long startPeriod,
                                                   Long endPeriod, Boolean superDeal) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (searchID != null) {
                predicates.add(cb.equal(root.get("id"), searchID));
            }
            if (!StringUtils.isEmpty(searchName)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
            }
            if (enable != null) {
                predicates.add(cb.equal(root.get("enable"), enable));
            }
            if (startPeriod != null && endPeriod != null) {
                Date expiry = new Date(startPeriod);
                Date expiry2 = new Date(endPeriod);
                predicates.add(cb.lessThanOrEqualTo(root.get("startPeriod"), expiry2));
                predicates.add(cb.greaterThanOrEqualTo(root.get("endPeriod"), expiry));

            }
            if (superDeal != null) {
                String queryType = null;
                if (superDeal) {
                    queryType = "wm-super";
                } else {
                    queryType = "discount";
                }
                predicates.add(cb.equal(root.get("type"), queryType));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private PageRequest genPageRequest(int page, int number, Sort.Direction direction, String sort) {
        return new PageRequest(page, number, new Sort(direction, sort));
    }

    public List<Deal> getSuperDeals() {
        return Optional.ofNullable(dealRepo.findBySuperDeal(true)).orElseThrow(DealNotFoundException::new);
    }

    public List<SuperDeal> getTodayDeals() {
        List<Deal> deals = Optional.ofNullable(dealRepo.findDealsByDateTime(
                DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))).orElseThrow(DealNotFoundException::new);
        deals.stream()
                .filter(x -> x.getStartPeriod().before(new DateTime().withTimeAtStartOfDay().plusDays(1).toDate()))
                .collect(Collectors.toList());
        return dealUtil.getAllSuperDealByTodayDeal(deals);
    }

    public List<SuperDeal> getTomorrowDeals() {
        List<Deal> deals = Optional.ofNullable(dealRepo.findDealsByDateTime(
                DateTime.now().plusDays(1).toString("yyyy-MM-dd 00:00:00"))).orElseThrow(DealNotFoundException::new);
        deals.stream()
                .filter(x -> x.getStartPeriod().before(new DateTime().withTimeAtStartOfDay().plusDays(2).toDate()))
                .collect(Collectors.toList());
        return dealUtil.getAllSuperDealByTomorrowDeal(deals);
    }

    public Deal enableDeal(Long dealId) {
        log.warn("content={\"activity\":\"Enable Deal\", \"msg\":{\"deal_id\":\"{}\"}}", dealId);
        Deal deal = Optional.ofNullable(dealRepo.findOne(dealId)).orElseThrow(DealNotFoundException::new);
        deal.setEnable(true);

        return dealRepo.saveAndFlush(deal);
    }

    public Deal disableDeal(Long dealId) {
        log.warn("content={\"activity\":\"Disable Deal\", \"msg\":{\"deal_id\":\"{}\"}}", dealId);
        Deal deal = Optional.ofNullable(dealRepo.findOne(dealId)).orElseThrow(DealNotFoundException::new);
        deal.setEnable(false);

        return dealRepo.saveAndFlush(deal);
    }

    public Deal getDeal(Long dealId) {
        Deal deal = Optional.ofNullable(dealRepo.findOne(dealId)).orElseThrow(DealNotFoundException::new);
        deal.setDealCondition(JSONUtil.parseToDealCondition(deal.getConditionData()));
        deal.setLive(dealUtil.isLive(deal.getEnable(), deal.getStartPeriod(), deal.getEndPeriod()));

        return deal;
    }

    public VariantDealResponse findPromotionPrice(String variantId) {
        PricingJson pricingData = externalService.getPricingData(variantId).orElseThrow(PricingServiceException::new);

        double normalPrice = 0.0;
        if (pricingData.getData() != null && pricingData.getData().getNormalPriceString() != null) {
            normalPrice = Double.parseDouble(pricingData.getData().getNormalPriceString());
        } else {
            log.warn("content={\"activity\":\"Disable Deal\", \"msg\":\"{}\"}",
                    "Cannot get normal price from pricing or normal price string is null !!");
        }

        PDSJson pdsData = externalService.getPDSData(variantId).orElseThrow(PDSServiceException::new);
        Optional<List<Deal>> dealList = Optional.ofNullable(dealRepo.findDealsByDateTime(
                DateTime.now().toString("yyy-MM-dd HH:mm:ss")));
        dealList.orElse(Lists.newArrayList()).stream()
                .filter(x -> dealUtil.isLive(x.getEnable(), x.getStartPeriod(), x.getEndPeriod()))
                .collect(Collectors.toList());

        VariantDeal variantDeal = dealUtil.setPromotionPrice(dealList.get(), pdsData.getData(), normalPrice);
        VariantDealResponse variantDealResponse = new VariantDealResponse();
        variantDealResponse.setPromotionId(variantDeal.getPromotionId());
        variantDealResponse.setPromotionName(variantDeal.getPromotionName());
        variantDealResponse.setVariantID(variantDeal.getVariantID());

        if (variantDeal.getPromotionPrice() != null) {
            variantDealResponse.setPromotionPrice(variantDeal.getPromotionPrice().toString());
        }

        return variantDealResponse;
    }

    public List<Product> setSuperDealAndDeleteSuperDealFromProductCart(
            List<Product> products, List<Product> productsSuperDeal) {
        Optional<List<Deal>> dealList = Optional.ofNullable(dealRepo.findDealsByDateTime(
                DateTime.now().toString("yyy-MM-dd HH:mm:ss")));

        dealList.orElse(Lists.newArrayList()).stream()
                .filter(x -> dealUtil.isLive(x.getEnable(), x.getStartPeriod(), x.getEndPeriod()))
                .collect(Collectors.toList());

        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            PDSJson pdsJson = externalService.getPDSData(product.getVariantId()).orElseThrow(PDSServiceException::new);
            VariantDeal variantDeal = dealUtil.setPromotionPrice(dealList.get(), pdsJson.getData(),
                    product.getNormalPrice());
            if (variantDeal.getPromotionPrice() != null) {
                product.setFinalPrice(variantDeal.getPromotionPrice());
                productsSuperDeal.add(product);
            } else {
                productList.add(product);
            }
        }
        return productList;
    }
}
