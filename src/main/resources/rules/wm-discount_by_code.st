<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products , $promotionCode : promotionCode ,$codeGroupId : codeGroupId)
    eval(<codeGroupId> == $codeGroupId)
    $minTotalValue : Double()  from accumulate(Product((<criteriaCondition>) && (<excludedCondition>),$value : getDiscountPriceDrl() , $quantity : quantity) from $products, sum( $value * $quantity ) )
    eval(Math.floor((double)$minTotalValue/<minTotalValue> )> 0)
then
    $cart.addPromotion("<promotionId>",null);
end
