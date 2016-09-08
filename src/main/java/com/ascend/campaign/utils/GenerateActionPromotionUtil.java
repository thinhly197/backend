package com.ascend.campaign.utils;

import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.OptionVariant;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.PromotionAction;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.Variant;
import com.ascend.campaign.models.VariantFreebie;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GenerateActionPromotionUtil {
    Function<String, Variant> stringToVariantFunction = s -> {
        Variant variant = new Variant();
        variant.setVariantId(s);
        variant.setQuantity(1);
        variant.setDiscountType("percent");
        variant.setDiscountValue(100.0);
        variant.setDiscountMaximum(0.0);
        return variant;
    };
    Function<VariantFreebie, OptionVariant> variantFreebieToOptionVariantFunction
            = new Function<VariantFreebie, OptionVariant>() {
        @Override
        public OptionVariant apply(VariantFreebie variantFreebie) {
            OptionVariant optionVariant = new OptionVariant();
            optionVariant.setVariantId(variantFreebie.getVariantId());
            optionVariant.setQuantity(variantFreebie.getQuantity());
            optionVariant.setDiscountType("percent");
            optionVariant.setDiscountValue(100D);
            optionVariant.setDiscountMaximum(0D);
            return optionVariant;
        }
    };

    public PromotionAction generateActionPromotionFreebie(List<String> freeSkusList, Integer quantity) {
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setCommand("free");
        promotionAction.setLimit(quantity);
        promotionAction.setVariants(freeSkusList.stream().map(stringToVariantFunction)
                .collect(Collectors.<Variant>toList()));
        return promotionAction;
    }

    public PromotionAction generateActionPromotionFreebieV2(PromotionCondition promotionCondition, Integer quantity) {
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setLimit(quantity);
        promotionAction.setVariants(new ArrayList<>());
        if (promotionCondition.getFreeVariantsSelectable() != null && promotionCondition.getFreeVariantsSelectable()) {
            promotionAction.setCommand("free_option");
            promotionAction.setOptionVariants(promotionCondition.getFreebieConditions().stream().map(
                    variantFreebieToOptionVariantFunction).collect(Collectors.toList()));
        } else {
            promotionAction.setCommand("free");
            List<String> variantList = getFreeVariantForPromotionFreebieItm(promotionCondition);
            promotionAction.setVariants(variantList.stream().map(stringToVariantFunction)
                    .collect(Collectors.<Variant>toList()));
            promotionAction.setOptionVariants(new ArrayList<>());
        }

        return promotionAction;
    }

    private List<String> getFreeVariantForPromotionFreebieItm(PromotionCondition promotionCondition) {
        List<String> freeVariant = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(promotionCondition.getFreebieConditions())) {
            promotionCondition.getFreebieConditions().forEach(variantFreebie -> {
                for (int i = 1; i <= variantFreebie.getQuantity(); i++) {
                    freeVariant.add(variantFreebie.getVariantId());
                }
            });
        }

        return freeVariant;
    }

    public List<Variant> generateBundleSkuListForBundlePromotionForProduct(ArrayList<BundleVariant> proBundle) {
        final Double[] discountValue = new Double[1];
        final String[] discountType = new String[1];

        List<Variant> variantList = Lists.newArrayList();
        proBundle.forEach(bundleVariant2 -> {
            Variant variant = new Variant();
            variant.setVariantId(bundleVariant2.getBundleVariant());
            if (bundleVariant2.getDiscountPercent() == null) {
                discountValue[0] = bundleVariant2.getDiscountFixed();
                discountType[0] = "total";
            } else {
                discountValue[0] = bundleVariant2.getDiscountPercent();
                discountType[0] = "percent";
            }
            variant.setDiscountType(discountType[0]);
            variant.setDiscountValue(discountValue[0]);
            variant.setDiscountMaximum(0.0);
            variantList.add(variant);
        });
        return variantList;
    }

    public PromotionAction generateActionPromotionBundle(List<Product> products, List<BundleVariant> bundleVariant) {
        bundleVariant.forEach(x -> {
            String deleteBlank = x.getBundleVariant().replaceAll(" ", "");
            x.setBundleVariant(deleteBlank);
        });
        PromotionAction promotionAction = new PromotionAction();
        final Double[] discountValue = new Double[1];
        final String[] discountType = new String[1];

        int quantity = products.stream().mapToInt(Product::getQuantity).min().getAsInt();

        promotionAction.setCommand("discount_bundle");
        promotionAction.setLimit(quantity);
        List<Variant> variantList = Lists.newArrayList();

        bundleVariant.forEach(x -> {
            if (x.getDiscountPercent() == null || x.getDiscountPercent() == 0.0) {
                x.setDiscountPercent(0.0);
            } else {
                x.setDiscountFixed(0.0);
            }
        });

        bundleVariant.forEach(bundleVariant2 -> products.forEach(discountProduct -> {
                    if (discountProduct.getVariantId().equals(bundleVariant2.getBundleVariant())) {
                        Variant variant = new Variant();
                        variant.setVariantId(discountProduct.getVariantId());
                        variant.setQuantity(1);
                        if (bundleVariant2.getDiscountPercent() == 0.0) {
                            discountValue[0] = bundleVariant2.getDiscountFixed();
                            discountType[0] = "total";
                        } else {
                            discountValue[0] = bundleVariant2.getDiscountPercent();
                            discountType[0] = "percent";
                        }
                        variant.setDiscountType(discountType[0]);
                        variant.setDiscountValue(discountValue[0]);
                        variant.setDiscountMaximum(0.0);
                        variantList.add(variant);
                    }
                }
        ));
        promotionAction.setVariants(variantList);

        return promotionAction;
    }

    public PromotionAction generateActionPromotionMNP(List<Product> products, List<MNPVariant> mnpVariants) {
        mnpVariants.forEach(x -> {
            String deleteBlank = x.getMnpVariants().replaceAll(" ", "");
            x.setMnpVariants(deleteBlank);
        });
        PromotionAction promotionAction = new PromotionAction();
        final Double[] discountValue = new Double[1];
        final String[] discountType = new String[1];

        int quantity = products.stream().mapToInt(Product::getQuantity).min().getAsInt();

        promotionAction.setCommand("discount_mnp");
        promotionAction.setLimit(quantity);
        List<Variant> variantList = Lists.newArrayList();

        mnpVariants.forEach(x -> {
            if (x.getDiscountPercent() == null || x.getDiscountPercent() == 0.0) {
                x.setDiscountPercent(0.0);
            } else {
                x.setDiscountFixed(0.0);
            }
        });

        mnpVariants.forEach(mnpVariant2 -> products.forEach(discountProduct -> {
                    if (discountProduct.getVariantId().equals(mnpVariant2.getMnpVariants())) {
                        Variant variant = new Variant();
                        variant.setVariantId(discountProduct.getVariantId());
                        variant.setQuantity(1);
                        if (mnpVariant2.getDiscountPercent() == 0.0) {
                            discountValue[0] = mnpVariant2.getDiscountFixed();
                            discountType[0] = "total";
                        } else {
                            discountValue[0] = mnpVariant2.getDiscountPercent();
                            discountType[0] = "percent";
                        }
                        variant.setDiscountType(discountType[0]);
                        variant.setDiscountValue(discountValue[0]);
                        variant.setDiscountMaximum(0.0);
                        variantList.add(variant);
                    }
                }
        ));
        promotionAction.setVariants(variantList);

        return promotionAction;
    }

    public PromotionAction generateActionPromotionDiscountByBrand(List<Product> productList,
                                                                  Integer quantity,
                                                                  PromotionCondition promotionCondition) {
        Double discountValue;
        String discountType;
        if (promotionCondition.getDiscountPercent() == null) {
            discountValue = promotionCondition.getDiscountFixed();
            discountType = "total";
        } else {
            discountValue = promotionCondition.getDiscountPercent();
            discountType = "percent";
            quantity = 1;
        }
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setCommand("discount_item");
        promotionAction.setLimit(quantity);
        List<Variant> variantList = Lists.newArrayList();
        productList.forEach(product -> {
            Variant variant = new Variant();
            variant.setVariantId(product.getVariantId());
            variant.setQuantity(1);
            variant.setDiscountType(discountType);
            variant.setDiscountValue(discountValue);
            variant.setDiscountMaximum(0.0);
            variantList.add(variant);
        });
        promotionAction.setVariants(variantList);

        return promotionAction;
    }

    public PromotionAction generateActionPromotionOptionToBuy(Integer quantity, PromotionCondition promotionCondition) {
        Double discountValue;
        String discountType;
        if (promotionCondition.getDiscountPercent() == null) {
            discountValue = promotionCondition.getDiscountFixed();
            discountType = "total";
        } else {
            discountValue = promotionCondition.getDiscountPercent();
            discountType = "percent";
        }
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setCommand("suggest_discount");
        promotionAction.setLimit(quantity);
        List<Variant> variantList = Lists.newArrayList();
        promotionCondition.getOptionVariants().forEach(optionVariant -> {
            Variant variant = new Variant();
            variant.setVariantId(optionVariant);
            variant.setQuantity(1);
            variant.setDiscountType(discountType);
            variant.setDiscountValue(discountValue);
            variant.setDiscountMaximum(0.0);
            variantList.add(variant);
        });
        promotionAction.setVariants(variantList);
        return promotionAction;
    }

    public PromotionAction generateActionPromotionSpecificTime(List<Product> productList,
                                                               PromotionCondition promotionCondition) {
        if (promotionCondition.getMasterCardPercent() == null) {
            promotionCondition.setMasterCardPercent(0.0);
        }
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setCommand("discount_item");
        promotionAction.setLimit(1);
        List<Variant> variantList = Lists.newArrayList();
        productList.forEach(discountProduct -> {
                    Variant variant = new Variant();
                    variant.setVariantId(discountProduct.getVariantId());
                    variant.setQuantity(1);
                    variant.setDiscountType("percent");
                    double discountValue = (1 - ((1 - promotionCondition.getDiscountPercent() / 100)
                            * (1 - promotionCondition.getMasterCardPercent() / 100))) * 100;
                    variant.setDiscountValue(discountValue);
                    variant.setDiscountMaximum(0.0);
                    variantList.add(variant);
                }
        );
        promotionAction.setVariants(variantList);

        return promotionAction;
    }

    public PromotionAction generateActionPromotionDiscountByCode(List<Product> productList,
                                                                 PromotionCondition promotionCondition) {

        final Double[] discountValue = new Double[1];
        String discountType;
        if (promotionCondition.getDiscountPercent() == null) {
            discountValue[0] = promotionCondition.getDiscountFixed();
            discountType = "total";
        } else {
            discountValue[0] = promotionCondition.getDiscountPercent();
            discountType = "percent";
        }
        PromotionAction promotionAction = new PromotionAction();
        if ("cart".equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            discountType = "total";
            promotionAction.setDiscountPerCart(promotionCondition.getDiscountFixed());
            promotionAction.setCommand("discount_per_cart");
            productList = setQuantityProductDiscountPerCart(productList);

        } else {
            promotionAction.setCommand("discount_item");
            promotionAction.setDiscountPerCart(0D);
        }
        promotionAction.setLimit(1);
        List<Variant> variantList = Lists.newArrayList();
        final String finalDiscountType = discountType;
        final List<Product> finalProductList = productList;
        productList.forEach(discountProduct -> {
                    Variant variant = new Variant();
                    variant.setVariantId(discountProduct.getVariantId());
                    variant.setQuantity(1);
                    if ("cart".equalsIgnoreCase(promotionCondition.getCriteriaType())) {
                        discountValue[0] = setDiscountPerCartToVariant(finalProductList, discountProduct,
                                promotionCondition);
                    }
                    variant.setDiscountType(finalDiscountType);
                    variant.setDiscountValue(discountValue[0]);
                    variant.setDiscountMaximum(promotionCondition.getMaxDiscountValue());
                    variantList.add(variant);
                }
        );
        promotionAction.setVariants(variantList);
        return promotionAction;
    }

    private List<Product> setQuantityProductDiscountPerCart(List<Product> productList) {
        List<Product> products = new ArrayList<>();
        productList.forEach(product -> {
            for (int i = 1; i <= product.getQuantity(); i++) {
                products.add(product);
            }

        });

        return products;
    }

    private Double setDiscountPerCartToVariant(List<Product> productList, Product discountProduct,
                                               PromotionCondition promotionCondition) {
        if (promotionCondition.getMaxDiscountValue() == null) {
            promotionCondition.setMaxDiscountValue(0D);
        }
        Double discount;
        Double sumNormalPrice = productList.stream().map(Product::getDiscountPriceDrl).reduce(0.0, Double::sum);
        if (promotionCondition.getDiscountFixed() != null) {
            discount = (discountProduct.getDiscountPriceDrl() * promotionCondition.getDiscountFixed()) / sumNormalPrice;
        } else {
            Double total = (promotionCondition.getDiscountPercent() / 100) * sumNormalPrice;
            Double discountValue;
            if (total < promotionCondition.getMaxDiscountValue()) {
                discountValue = total;
            } else {
                if (promotionCondition.getMaxDiscountValue() == 0D) {
                    discountValue = total;
                } else {
                    discountValue = promotionCondition.getMaxDiscountValue();
                }
            }
            discount = (discountProduct.getDiscountPriceDrl() * discountValue) / sumNormalPrice;
        }
        return discount;
    }
}
