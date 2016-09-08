
<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $mnpProduct : MNPProduct( $productVariant : productVariant  )
      eval(<compareSkuPromotion>)
then
    $mnpProduct.addPromotionId("<promotionId>");
end
