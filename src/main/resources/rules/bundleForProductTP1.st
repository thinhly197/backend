
<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $bundleProduct : BundleProduct( $productVariant : productVariant  )
      eval(<compareSkuPromotion>)
then
    $bundleProduct.addPromotionId("<promotionId>");
end
