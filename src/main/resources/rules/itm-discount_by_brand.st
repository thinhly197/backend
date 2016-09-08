<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products)
    $minTotalValue : Double()  from accumulate(Product((<brandCondition>) && (<excludedSkusCondition>),$value : getDiscountPriceDrl() , $quantity : quantity) from $products, sum( $value * $quantity ) )
    eval(Math.floor((double)$minTotalValue/<minTotalValue> )> 0)
then
    $cart.addPromotion("<promotionId>",Math.min((int)Math.floor($minTotalValue/<minTotalValue>),<repeatNumber>));
end
