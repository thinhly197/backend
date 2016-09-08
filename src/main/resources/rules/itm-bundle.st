<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products , $promotionType : promotionType ,$promotionParams : promotionParams)
    $countPrimary : Double() from accumulate(Product(<primaryVariant>,$quantity : quantity) from $products, sum( $quantity ) )
    eval($countPrimary > 0)
    $variant : List() from accumulate(Product($variantId : variantId)from $products,collectList($variantId))
    $countBundleSku : Double() from accumulate(Product(<bundleSkuCondition>,$quantity : quantity) from $products, sum( $quantity ) )
    eval($countBundleSku > 0)
then
    $cart.addPromotion("<promotionId>",null);
end