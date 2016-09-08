
<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $promotionProduct : PromotionProduct($productVariant : productVariant ,$brandVariant : brandVariant ,$collection : collection ,$category : category)
    eval(<compareSkuPromotion>)
then
    $promotionProduct.addPromotionsIdList(<promotionId>);
end
