<\n>
rule "<name>_<id>"
date-effective "<startPeriod>"
date-expires "<endPeriod>"
when
    $cart : Cart(<customerType>  $products : products)
    $sumQuantity : Double()  from accumulate(Product(<condition>,$quantity : quantity) from $products, sum( $quantity ) )
    eval(Math.floor($sumQuantity/<quantity>) > 0)
then
    $cart.addPromotion("<promotionId>",Math.min((int)Math.floor($sumQuantity/<quantity>),<repeatNumber>));
end
