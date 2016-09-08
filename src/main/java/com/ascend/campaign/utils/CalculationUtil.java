package com.ascend.campaign.utils;

import com.ascend.campaign.models.Calculation;
import com.ascend.campaign.models.CampaignApplied;
import com.ascend.campaign.models.CampaignSuggestion;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.PromotionAction;
import com.ascend.campaign.models.Variant;
import com.ascend.campaign.models.VariantCalculation;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalculationUtil {

    @NonNull
    DecimalUtil decimalUtil;

    @Autowired
    public CalculationUtil(DecimalUtil decimalUtil) {
        this.decimalUtil = decimalUtil;
    }

    public Calculation setFlatDiscountData(List<Product> products, List<CampaignSuggestion> campaignSuggestion,
                                           List<CampaignApplied> campaignAppliedList,
                                           List<Product> productsSuperDeal) {
        checkVariantAndSetLimitByCampaignApplied(campaignAppliedList, campaignSuggestion);

        List<VariantCalculation> variantCalculations = productToVariantCalculation(products);

        List<Variant> variantsFromVariantCalculationList = setVariantCalculationToVariantList(campaignSuggestion,
                variantCalculations);

        variantCalculations = variantToVariantCalculation(variantCalculations, variantsFromVariantCalculationList);

        setFinalPriceAndPercentDiscountToDealProduct(variantCalculations, productsSuperDeal);

        List<VariantCalculation> allVariantFromSetFlat = setPriceAndDiscountData(variantCalculations,
                sumDiscountPromotion(variantCalculations));

        setFinalPriceVariantCalculationNotMachPromotion(allVariantFromSetFlat, products);
        setFlatDiscountToVariantCalculation(campaignSuggestion, allVariantFromSetFlat);
        setTotalPercentDiscountAndFinalPrice(allVariantFromSetFlat);

        return setDataToCalculation(allVariantFromSetFlat);
    }

    private void setTotalPercentDiscountAndFinalPrice(List<VariantCalculation> allVariantFromSetFlat) {
        allVariantFromSetFlat.forEach(variantCalculation -> {
            variantCalculation.setFinalPrice(variantCalculation.getFinalPrice()
                    - variantCalculation.getFlatDiscount());
            variantCalculation.setFinalPriceString(variantCalculation.getFinalPrice().toString());
            variantCalculation.setTotalPercentDiscount(
                    findPercentDiscountDeal(variantCalculation.getNormalPrice(),
                            variantCalculation.getFinalPrice()));
            variantCalculation.setTotalPercentDiscountString(variantCalculation.getTotalPercentDiscount().toString());
        });
    }

    private void setFinalPriceVariantCalculationNotMachPromotion(List<VariantCalculation> allVariantFromSetFlat,
                                                                 List<Product> products) {
        allVariantFromSetFlat.stream().filter(variantCalculation ->
                variantCalculation.getNormalPriceString().equalsIgnoreCase(variantCalculation.getFinalPriceString()))
                .forEach(variantCalculation -> {
                    Optional<Product> product = products.stream().filter(x -> x.getVariantId()
                            .equalsIgnoreCase(variantCalculation.getVariantId())).findFirst();
                    if (product.isPresent()) {
                        variantCalculation.setFinalPrice(product.get().getNormalPrice());
                        variantCalculation.setFinalPriceString(product.get().getNormalPrice().toString());
                    }
                });
    }

    private void setFinalPriceAndPercentDiscountToDealProduct(
            List<VariantCalculation> variantCalculations, List<Product> productsSuperDeal) {
        for (VariantCalculation variantCalculation : variantCalculations) {
            Optional<Product> product = productsSuperDeal.stream()
                    .filter(x -> x.getVariantId().equalsIgnoreCase(variantCalculation.getVariantId())).findFirst();
            if (product.isPresent() && product.get().getFinalPrice() != null) {
                variantCalculation.setFinalPrice(product.get().getFinalPrice());
                variantCalculation.setPercentDiscount(
                        findPercentDiscountDeal(variantCalculation.getNormalPrice(), product.get().getFinalPrice()));
                variantCalculation.setTotalPercentDiscount(
                        findPercentDiscountDeal(variantCalculation.getNormalPrice(), product.get().getFinalPrice()));
            }
        }
    }

    private Double findPercentDiscountDeal(Double normalPrice, Double finalPrice) {
        return (normalPrice - finalPrice) * 100 / normalPrice;
    }

    private List<Variant> setVariantCalculationToVariantList(
            List<CampaignSuggestion> campaignSuggestion,
            List<VariantCalculation> variantCalculations) {
        List<Variant> allFreeVariants = new ArrayList<>();
        for (CampaignSuggestion campSuggest : campaignSuggestion) {
            setQuantityFreeVariantForPromotionAction(campSuggest.getPromotionAction().get(0));
            List<Variant> freeVariants;
            List<Variant> notFreeVariants;
            List<Variant> variants = campSuggest.getPromotionAction().get(0).getVariants();
            freeVariants = collectFreeVariants(variants);
            allFreeVariants.addAll(freeVariants);
            notFreeVariants = collectNotFreeVariants(variants);
            setLimitNotFreeVariants(variantCalculations, campSuggest, notFreeVariants);
        }

        List<Variant> sumDiscountNotFreeVariant = sumDiscountForNotFreeVariant(variantCalculations);

        allFreeVariants.addAll(sumDiscountNotFreeVariant);

        return allFreeVariants;
    }

    private void setLimitNotFreeVariants(List<VariantCalculation> variantCalculations, CampaignSuggestion campSuggest,
                                         List<Variant> notFreeVariants) {
        for (Variant variant : notFreeVariants) {
            if ("discount_item".equalsIgnoreCase(campSuggest.getPromotionAction().get(0).getCommand())
                    || "discount_per_cart".equalsIgnoreCase(campSuggest.getPromotionAction().get(0).getCommand())) {
                int countQuantity = (int) variantCalculations.stream()
                        .filter(x -> x.getVariantId().equalsIgnoreCase(variant.getVariantId())).count();
                campSuggest.getPromotionAction().get(0).setLimit(countQuantity);
            }
            setDiscountForProduct(variantCalculations, variant,
                    campSuggest.getPromotionAction().get(0).getLimit(),
                    campSuggest.getPromotionAction().get(0).getCommand());
        }
    }

    private List<Variant> collectNotFreeVariants(List<Variant> variants) {
        return variants
                .stream()
                .filter(x -> !("percent".equalsIgnoreCase(x.getDiscountType()) && 100 == x.getDiscountValue()))
                .map(Variant::new)
                .collect(Collectors.toList());
    }

    private List<Variant> collectFreeVariants(List<Variant> variants) {
        return variants
                .stream()
                .filter(x -> "percent".equalsIgnoreCase(x.getDiscountType()) && 100 == x.getDiscountValue())
                .map(Variant::new)
                .collect(Collectors.toList());
    }

    private void setFlatDiscountToVariantCalculation(List<CampaignSuggestion> campaignSuggestion,
                                                     List<VariantCalculation> variantCalculations) {
        campaignSuggestion.forEach(x -> {
            if ("discount_per_cart".equalsIgnoreCase(x.getPromotionAction().get(0).getCommand())) {
                List<String> variantLists = x.getPromotionAction().get(0).getVariants()
                        .stream().map(Variant::getVariantId).collect(Collectors.toList());
                Double sumNormalPrice = variantCalculations.stream()
                        .filter(v -> variantLists.contains(v.getVariantId()))
                        .map(VariantCalculation::getNormalPrice)
                        .reduce(0.0, Double::sum);
                variantCalculations.forEach(variantCalculation -> {
                    Double discount = (variantCalculation.getNormalPrice() * x.getPromotionAction().get(0)
                            .getDiscountPerCart()) / sumNormalPrice;
                    if (variantCalculation.getFlatDiscount() == null) {
                        variantCalculation.setFlatDiscount(discount);
                        variantCalculation.setFlatDiscountString(variantCalculation.getFlatDiscount().toString());
                    } else {
                        variantCalculation.setFlatDiscount(variantCalculation.getFlatDiscount() + discount);
                        variantCalculation.setFlatDiscountString(variantCalculation.getFlatDiscount().toString());
                    }
                });
            }
        });
    }

    private Calculation setDataToCalculation(List<VariantCalculation> allVariantFromSetFlat) {
        Calculation calculation = new Calculation();
        calculation.setPromotionForProducts(allVariantFromSetFlat);
        calculation.setTotalFlatDiscount(sumTotalFlatDiscount(allVariantFromSetFlat));
        calculation.setTotalFlatDiscountString(String.valueOf(
                decimalUtil.roundTo2Decimals(sumTotalFlatDiscount(allVariantFromSetFlat))));
        return calculation;
    }

    private List<VariantCalculation> variantToVariantCalculation(
            List<VariantCalculation> variantCalculations, List<Variant> freeVariants) {
        Double setPromotionDiscount;
        for (VariantCalculation variantCalculation : variantCalculations) {
            Optional<Variant> variant = freeVariants.stream()
                    .filter(x -> x.getVariantId().equalsIgnoreCase(variantCalculation.getVariantId())).findFirst();
            if (variant.isPresent()) {
                freeVariants.remove(variant.get());
                variantCalculation.setVariantId(variant.get().getVariantId());
                setPromotionDiscount = (variantCalculation.getNormalPrice() * variant.get().getDiscountValue()) / 100;
                variantCalculation.setPercentDiscount(
                        variant.get().getDiscountValue());
                variantCalculation.setPercentDiscountString(String.valueOf(
                        decimalUtil.roundTo2DecimalsCEILING(variantCalculation.getPercentDiscount())));
                variantCalculation.setTotalPercentDiscount(
                        decimalUtil.roundTo2DecimalsCEILING(setPromotionDiscount));
                variantCalculation.setFinalPrice(
                        decimalUtil.roundTo2DecimalsFLOOR(variantCalculation.getNormalPrice() - setPromotionDiscount));
            }
        }

        variantCalculations.forEach(variantCalculation -> {
            if (variantCalculation.getPercentDiscount() == null) {
                variantCalculation.setPercentDiscount(0.0D);
                variantCalculation.setPercentDiscountString("0.0");
                if (variantCalculation.getTotalPercentDiscount() == null) {
                    variantCalculation.setTotalPercentDiscount(0.0D);
                }
            }
        });
        return variantCalculations;
    }

    public Double sumFlatDiscount(List<Variant> variantList, double nmPrice) {
        List<Double> percentList = Lists.newArrayList();
        List<Double> fixedList = Lists.newArrayList();

        variantList.stream().filter(x -> "percent".equalsIgnoreCase(x.getDiscountType()))
                .forEach(x -> percentList.add((1 - (x.getDiscountValue() / 100))));

        variantList.stream().filter(x -> !"percent".equalsIgnoreCase(x.getDiscountType()))
                .forEach(x -> fixedList.add(x.getDiscountValue()));

        Double productPercentList = percentList.stream().reduce(1.0, (acc, element) -> acc * element);
        Double sumFix = fixedList.stream().reduce(0.0, Double::sum);

        double discountPrice = nmPrice - (nmPrice * productPercentList);
        double finaleDiscount = discountPrice + sumFix;

        return 100 * finaleDiscount / nmPrice;
    }

    private List<Variant> sumDiscountForNotFreeVariant(List<VariantCalculation> variantCalculations) {
        List<Variant> notFreeVariants = Lists.newArrayList();
        variantCalculations.stream()
                .filter(variantCalculation -> variantCalculation.getVariantList() != null)
                .forEach(variantCalculation -> {
                    Double discountValue = sumFlatDiscount(variantCalculation.getVariantList(),
                            variantCalculation.getNormalPrice());
                    Variant variant = new Variant();
                    variant.setVariantId(variantCalculation.getVariantId());
                    variant.setDiscountType("percent");
                    variant.setDiscountValue(discountValue);
                    notFreeVariants.add(variant);
                });

        return notFreeVariants;
    }

    private void checkVariantAndSetLimitByCampaignApplied(List<CampaignApplied> campaignAppliedList,
                                                          List<CampaignSuggestion> campaignSuggestion) {
        for (CampaignApplied cpa : campaignAppliedList) {
            campaignSuggestion.stream()
                    .filter(cps -> cpa.getPromotionId().toString().equals(cps.getPromotionId()))
                    .forEach(cps -> {
                        cps.getPromotionAction().get(0).setLimit(cpa.getLimit());
                        cps.getPromotionAction().get(0).setVariants(checkVariantInCampaignApplied(
                                cps.getPromotionAction().get(0).getVariants(), cpa.getVariantId()));
                    });
        }
    }

    private List<Variant> checkVariantInCampaignApplied(List<Variant> variants, List<String> variantId) {
        Set<String> setVariant = new HashSet<>(variantId);
        List<Variant> variantList = new ArrayList<>();
        for (Variant v : variants) {
            variantList.addAll(setVariant.stream()
                    .filter(vaId -> v.getVariantId().equalsIgnoreCase(vaId))
                    .map(vaId -> v).collect(Collectors.toList()));
        }

        return variantList;
    }

    private void setQuantityFreeVariantForPromotionAction(PromotionAction promotionAction) {
        if ("free".equalsIgnoreCase(promotionAction.getCommand())
                && 1 < promotionAction.getLimit()) {
            promotionAction.setVariants(setQuantityFreeVariant(promotionAction));
        }
    }

    private List<Variant> setQuantityFreeVariant(PromotionAction promotionAction) {
        List<Variant> variantGroup = new ArrayList<>();
        for (int i = 1; i <= promotionAction.getLimit(); i++) {
            variantGroup.addAll(promotionAction.getVariants());
        }

        return variantGroup;
    }

    private List<VariantCalculation> productToVariantCalculation(List<Product> products) {
        List<VariantCalculation> variantCalculations = new ArrayList<>();
        for (Product product : products) {
            VariantCalculation variantCalculation = new VariantCalculation();
            variantCalculation.setVariantId(product.getVariantId());
            variantCalculation.setNormalPrice(product.getNormalPrice());
            variantCalculation.setFinalPrice(product.getNormalPrice());
            variantCalculations.add(variantCalculation);
        }

        return variantCalculations;
    }

    private void setDiscountForProduct(List<VariantCalculation> variantCalculations, Variant variant, int limit,
                                       String command) {
        for (VariantCalculation variantCalculation : variantCalculations) {
            if (variantCalculation.getVariantId().equalsIgnoreCase(variant.getVariantId()) && limit > 0) {
                if (variantCalculation.getVariantList() == null) {
                    variantCalculation.variantList = new ArrayList<>();
                }
                if (!"discount_per_cart".equalsIgnoreCase(command)) {
                    variantCalculation.getVariantList().add(variant);
                }
                limit--;
            }
        }
    }

    public Double sumDiscountPromotion(List<VariantCalculation> variantCalculationList2) {
        return variantCalculationList2.stream().map(VariantCalculation::getFinalPrice).reduce(0.0, Double::sum);
    }

    private List<VariantCalculation> setPriceAndDiscountData(List<VariantCalculation> variantCalculationList,
                                                             Double beforeFlat) {
        List<VariantCalculation> variantCalculation = new ArrayList<>();
        double lastFinalPrice = 0.0;
        for (VariantCalculation vc : variantCalculationList) {
            lastFinalPrice = decimalUtil.roundTo2DecimalsFLOOR(vc.getFinalPrice());
            /*  if (beforeFlat > 0) {
                double flatDiscountToArray = (vc.getFinalPrice() * totalFlatDiscount) / beforeFlat;
                sumFlatDiscountArray.add(decimalUtil.roundTo2DecimalsCEILING(flatDiscountToArray));
               *//* Double flatDiscount = (vc.getFinalPrice() * totalFlatDiscount) / beforeFlat;
                vc.setFlatDiscount(
                        decimalUtil.roundTo2DecimalsCEILING(flatDiscount));*//*
                vc.setFinalPrice(decimalUtil.roundTo2DecimalsFLOOR(vc.getFinalPrice()
                        - (vc.getFinalPrice() * totalFlatDiscount) / beforeFlat));
            } else {
                vc.setFlatDiscount(0.0D);
                vc.setFinalPrice(0.0D);
            }*/
           /* double setTotalPercentDiscount = (
                    (vc.getTotalPercentDiscount() + vc.getFlatDiscount())
                            / vc.getNormalPrice()) * 100;*/

            // vc.setTotalPercentDiscount(decimalUtil.roundTo2DecimalsCEILING(setTotalPercentDiscount));
            vc.setNormalPriceString(vc.getNormalPrice().toString());
            vc.setFinalPriceString(String.valueOf(vc.getFinalPrice()));
            /*vc.setTotalPercentDiscountString(String.valueOf(
                    decimalUtil.roundTo2DecimalsCEILING(setTotalPercentDiscount)));*/
            vc.setFlatDiscountString(String.valueOf(
                    vc.getFlatDiscount()));
            variantCalculation.add(vc);
        }

       /* if (sumFlatDiscountArray.size() > 1) {
            setDataToLastVariantCalculationInList(totalFlatDiscount, lastFinalPrice,
                    lastTotalPercentDiscount, sumFlatDiscountArray, variantCalculation);

        }*/


        return variantCalculation;
    }

    /*private void setDataToLastVariantCalculationInList(
            Double totalFlatDiscount, Double lastFinalPrice, double lastTotalPercentDiscount,
            ArrayList<Double> sumFlatDiscountArray, List<VariantCalculation> variantCalculation) {
        double sumFlatDiscount = sumTotalFlatDiscountBeforeLastElementInList(sumFlatDiscountArray);

        double lastFlatDiscount = totalFlatDiscount - decimalUtil.roundTo2Decimals(sumFlatDiscount);
        int vcSize = variantCalculation.size() - 1;
        variantCalculation.get(vcSize).setFinalPrice(
                lastFinalPrice - lastFlatDiscount);

        variantCalculation.get(vcSize).setFinalPriceString(
                String.valueOf(decimalUtil.roundTo2DecimalsFLOOR(lastFinalPrice - lastFlatDiscount)));
        variantCalculation.get(vcSize).setFlatDiscount(lastFlatDiscount);
        variantCalculation.get(vcSize).setFlatDiscountString(String.valueOf(
                decimalUtil.roundTo2Decimals(lastFlatDiscount)));
        double setLastTotalPercentDiscount = ((lastTotalPercentDiscount
                + variantCalculation.get(vcSize).getFlatDiscount())
                / variantCalculation.get(vcSize).getNormalPrice()) * 100;
        variantCalculation.get(vcSize).setTotalPercentDiscount(
                setLastTotalPercentDiscount);
        variantCalculation.get(vcSize).setTotalPercentDiscountString(String.valueOf(decimalUtil.roundTo2DecimalsCEILING(
                setLastTotalPercentDiscount)));
    }*/

    private double sumTotalFlatDiscount(List<VariantCalculation> allVariantFromSetFlat) {
        return allVariantFromSetFlat.stream().map(VariantCalculation::getFlatDiscount).reduce(0.0, Double::sum);
    }

    private double sumTotalFlatDiscountBeforeLastElementInList(List<Double> flatDiscount) {
        flatDiscount.remove(flatDiscount.size() - 1);
        return flatDiscount.stream().reduce(0.0, Double::sum);
    }

    public Calculation reCalculation(List<VariantCalculation> promotionForProducts, Double totalFlatDiscount) {
        mapStringToDoubleField(promotionForProducts);
        Calculation calculation = new Calculation();
        calculation.setTotalFlatDiscount(totalFlatDiscount);
        List<VariantCalculation> variantCalculationList = new ArrayList<>();
        for (VariantCalculation v : promotionForProducts) {
            VariantCalculation variantCalculation = new VariantCalculation();
            variantCalculation.setVariantId(v.getVariantId());
            variantCalculation.setNormalPrice(v.getNormalPrice());
            variantCalculation.setPercentDiscount(v.getPercentDiscount());
            variantCalculation.setPercentDiscountString(v.getPercentDiscount().toString());
            if (v.getTotalPercentDiscount() > 0.0) {
                double setPromotionDiscount = v.getNormalPrice() * v.getPercentDiscount() / 100;
                variantCalculation.setTotalPercentDiscount(setPromotionDiscount);
                variantCalculation.setFinalPrice(v.getNormalPrice() - setPromotionDiscount);
            } else {
                variantCalculation.setFinalPrice(v.getFinalPrice());
                variantCalculation.setTotalPercentDiscount(0.0);
            }
            variantCalculationList.add(variantCalculation);
        }
       /* List<VariantCalculation> allVariantFromSetFlat = setPriceAndDiscountData(variantCalculationList,
                totalFlatDiscount,
                sumDiscountPromotion);*/
        //calculation.setPromotionForProducts(allVariantFromSetFlat);

        return calculation;
    }

    private void mapStringToDoubleField(List<VariantCalculation> promotionForProducts) {
        promotionForProducts.stream().filter(
                variantCalculation -> variantCalculation.getNormalPriceString() != null).forEach(variantCalculation -> {
            variantCalculation.setNormalPrice(Double.valueOf(variantCalculation.getNormalPriceString()));
            variantCalculation.setFinalPrice(Double.valueOf(variantCalculation.getFinalPriceString()));
            variantCalculation.setFlatDiscount(Double.valueOf(variantCalculation.getFinalPriceString()));
            variantCalculation.setPercentDiscount(Double.valueOf(
                    variantCalculation.getPercentDiscountString()));
            variantCalculation.setTotalPercentDiscount(Double.valueOf(
                    variantCalculation.getTotalPercentDiscountString()));
        });
    }

    public Calculation setCalculationIfCartNotHavePromotion(List<Product> products, List<Product> productsSuperDeal) {
        Calculation calculation = new Calculation();
        calculation.setTotalFlatDiscount(0.0D);
        calculation.setTotalFlatDiscountString("0.0");
        List<VariantCalculation> variantCalculations = getVariantCalculations(products);
        setFinalPriceAndPercentDiscountToDealProduct(variantCalculations, productsSuperDeal);

        calculation.setPromotionForProducts(variantCalculations);

        return calculation;
    }

    private List<VariantCalculation> getVariantCalculations(List<Product> products) {
        List<VariantCalculation> variantCalculations = Lists.newArrayList();
        for (Product product : products) {
            VariantCalculation variantCalculation = new VariantCalculation();
            variantCalculation.setFlatDiscount(0.0D);
            variantCalculation.setFlatDiscountString("0.0");
            variantCalculation.setFinalPrice(product.getNormalPrice());
            variantCalculation.setFinalPriceString(product.getNormalPrice().toString());
            variantCalculation.setNormalPrice(product.getNormalPrice());
            variantCalculation.setVariantId(product.getVariantId());
            variantCalculation.setPercentDiscount(0.0D);
            variantCalculation.setPercentDiscountString("0.0");
            variantCalculation.setTotalPercentDiscount(0.0D);
            variantCalculation.setTotalPercentDiscountString("0.0");
            variantCalculation.setNormalPriceString(product.getNormalPrice().toString());
            variantCalculations.add(variantCalculation);
        }

        return variantCalculations;
    }
}
