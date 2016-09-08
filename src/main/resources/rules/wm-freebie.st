<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products)
    <freebieCondition>
    eval(Math.floor(<sumQuantityFreebie>/<quantityFreebie>) > 0)
then
    $cart.addPromotion("<promotionId>",Math.min((int)Math.floor($sumQuantity/<quantityFreebie>),<repeatNumber>));
end
