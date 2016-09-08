<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $promotionProduct : PromotionProduct($productVariant : productVariant , $brandVariant : brandVariant)
    eval(<compareBrandPromotion>)
    <compareNotSkuPromotion>
then
    $promotionProduct.addPromotionsIdList(<promotionId>);
end
