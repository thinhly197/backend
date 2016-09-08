
<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products,$promotionCode : promotionCode,$cardType : cardType)
    eval(($cart.getDayOfWeek() & <DayOfWeek>) > 0)
    <promotionCode>
    <timePromotion>
    $count : Long() from accumulate(Product((<brandCondition>) && (<excludedSkusCondition>),$value : getDiscountPriceDrl() ) from $products, count( $value ) )
    eval($count > 0)
then
    $cart.addPromotion("<promotionId>",null);
end
