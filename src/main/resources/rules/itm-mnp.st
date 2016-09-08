<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products , $promotionType : promotionType ,$promotionParams : promotionParams)
    $countPrimary : Double() from accumulate(Product(<primaryVariant>,$quantity : quantity) from $products, sum( $quantity ) )
    eval($countPrimary > 0)
    <mnpBundle>
then
    $cart.addPromotion("<promotionId>",null);
end