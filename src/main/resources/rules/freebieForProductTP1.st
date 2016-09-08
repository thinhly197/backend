
<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $freebieProduct : FreebieProduct( $productVariant : productVariant  )
      eval(<compareSkuPromotion>)
then
    $freebieProduct.addPromotionId("<promotionId>");
end
