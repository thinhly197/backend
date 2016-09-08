package com.ascend.campaign.services;

import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.entities.PromotionTask;
import com.ascend.campaign.models.DealCriteriaValue;
import com.ascend.campaign.repositories.DealRepo;
import com.ascend.campaign.repositories.PromotionTaskRepo;
import com.ascend.campaign.utils.DealUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional
public class ScheduledTask {
    @NotNull
    private final PromotionTaskRepo promotionTaskRepo;

    @NonNull
    private final DealRepo dealRepo;

    @NonNull
    private final DealService dealService;

    @NotNull
    private final ExternalService externalService;

    @NonNull
    private final DealUtil dealUtil;

    @Autowired
    public ScheduledTask(PromotionTaskRepo promotionTaskRepo, DealRepo dealRepo,
                         DealService dealService, ExternalService externalService, DealUtil dealUtil) {
        this.promotionTaskRepo = promotionTaskRepo;
        this.dealRepo = dealRepo;
        this.dealService = dealService;
        this.externalService = externalService;
        this.dealUtil = dealUtil;
    }

    @Scheduled(fixedRateString = "${spring.fixedRate.milliseconds}")
    public void pollingPromotions() {
        Optional<List<PromotionTask>> promotionTasks = Optional.ofNullable(
                promotionTaskRepo.findByTriggerAtLessThanEqual(DateTime.now().toDate()));

        if (promotionTasks.isPresent()) {
            Map<Long, Long> mapPromotionTasks = promotionTasks.get().stream()
                    .collect(Collectors.groupingBy(PromotionTask::getPromotionId, Collectors.counting()));

            List<Long> promotionTaskToDelete = mapPromotionTasks.entrySet().stream()
                    .filter(x -> x.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            List<Long> promotionTaskToProcess = mapPromotionTasks.entrySet().stream()
                    .filter(x -> x.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (promotionTaskToProcess != null) {
                updateSearchSyncByDealId(promotionTaskToProcess, promotionTasks.get());
            }

            if (promotionTaskToDelete != null) {
                promotionTaskToDelete.forEach(this::deletePromotionTask);
            }
        } else {
            log.warn("===== Promotion Task have not to sync =====");
        }
    }

    private void updateSearchSyncByDealId(List<Long> promotionTaskToProcess, List<PromotionTask> promotionTaskList) {
        List<Deal> dealList = dealRepo.findDealsByDateTime(DateTime.now().toString("yyy-MM-dd HH:mm:ss"))
                .stream().filter(x -> dealUtil.isLive(x.getEnable(), x.getStartPeriod(), x.getEndPeriod()))
                .collect(Collectors.toList());
        for (Long promotionTask : promotionTaskToProcess) {
            Long id = promotionTaskList.stream()
                    .filter(x -> promotionTask.equals(x.getPromotionId()))
                    .map(PromotionTask::getId).findFirst().get();
            log.warn("content={\"activity\":\"Delete promotion task\", \"msg\":{\"id\":\"{}\"}}", id);
            promotionTaskRepo.delete(id);
            Deal deal = dealService.getDeal(promotionTask);
            if (deal.getDealCondition().getCriteriaVariants() != null) {
                List<String> variantList = deal.getDealCondition().getCriteriaVariants().stream()
                        .map(DealCriteriaValue::getVariantId).collect(Collectors.toList());
                variantList.forEach(variant -> updateSearchSync(variant, dealList));
            }
        }
    }

    private void deletePromotionTask(Long promotionTask) {
        log.warn("content={\"activity\":\"Delete promotion task\", \"msg\":{\"id\":\"{}\"}}", promotionTask);
        promotionTaskRepo.deleteByPromotionId(promotionTask);
    }

    public void updateSearchSync(String variant, List<Deal> dealList) {
        externalService.triggerPromotionPriceToPricing(variant, dealList);
    }

}