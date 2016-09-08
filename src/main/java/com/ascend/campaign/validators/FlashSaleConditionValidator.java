package com.ascend.campaign.validators;

import com.ascend.campaign.constants.FlashSaleEnum;
import com.ascend.campaign.entities.FlashSaleProduct;
import com.ascend.campaign.entities.FlashSaleVariant;
import com.ascend.campaign.models.FlashSaleCondition;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class FlashSaleConditionValidator implements ConstraintValidator<FlashSaleConditionCheck, Object> {
    private FlashSaleEnum type;

    private static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public void initialize(FlashSaleConditionCheck constraintAnnotation) {

        this.type = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (type == FlashSaleEnum.FLASH_SALE) {

            FlashSaleCondition flashSaleCondition = (FlashSaleCondition) value;


            if (Optional.ofNullable(flashSaleCondition).isPresent()
                    && resolve(flashSaleCondition::getFlashSaleProduct).isPresent()) {
                return avoidingNullCheckWowBanner(flashSaleCondition);
            } else {
                return Optional.ofNullable(flashSaleCondition).isPresent()
                        && resolve(flashSaleCondition::getFlashSaleProducts).isPresent()
                        && avoidingNullCheckWowExtra(flashSaleCondition);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Operation !!!");
        }
    }

    private boolean avoidingNullCheckWowExtra(FlashSaleCondition flashSaleCondition) {
        return avoidingNullCheckProductWowExtra(flashSaleCondition)
                && avoidingNullCheckVariantsWowExtra(flashSaleCondition);

    }

    private boolean avoidingNullCheckVariantsWowExtra(FlashSaleCondition flashSaleCondition) {
        List<FlashSaleVariant> flashSaleVariantList = flashSaleCondition.getFlashSaleProducts().stream()
                .flatMap(p -> p.getFlashsaleVariants().stream())
                .filter(fv -> fv.getDiscountPercent() == null
                        || fv.getPromotionPrice() == null
                        || fv.getVariantId() == null).collect(Collectors.toList());
        return flashSaleVariantList.isEmpty();
    }

    private boolean avoidingNullCheckProductWowExtra(FlashSaleCondition flashSaleCondition) {
        if (flashSaleCondition.getLimitItem() == null || flashSaleCondition.getLimitItem() < 0) {
            return false;
        } else {
            List<FlashSaleProduct> flashSaleProductList = flashSaleCondition.getFlashSaleProducts()
                    .stream().filter(flashSaleProduct -> flashSaleProduct.getProductKey() == null
                            || flashSaleProduct.getCategoryIds() == null
                            || flashSaleProduct.getFlashsaleVariants() == null
                    ).collect(Collectors.toList());
            return flashSaleProductList.isEmpty();
        }
    }

    private boolean avoidingNullCheckWowBanner(FlashSaleCondition flashSaleCondition) {
        if (flashSaleCondition.getLimitItem() == null
                || flashSaleCondition.getLimitItem() < 0
                || flashSaleCondition.getFlashSaleProduct().getProductKey() == null
                || flashSaleCondition.getFlashSaleProduct().getCategoryIds() == null
                || flashSaleCondition.getFlashSaleProduct().getFlashsaleVariants() == null) {
            return false;
        } else {
            List<FlashSaleVariant> flashSaleVariantList = flashSaleCondition.getFlashSaleProduct()
                    .getFlashsaleVariants().stream()
                    .filter(fv -> fv.getDiscountPercent() == null
                            || fv.getPromotionPrice() == null
                            || fv.getVariantId() == null
                            || fv.getLimitQuantity() == null).collect(Collectors.toList());

            return flashSaleVariantList.isEmpty();
        }
    }
}
