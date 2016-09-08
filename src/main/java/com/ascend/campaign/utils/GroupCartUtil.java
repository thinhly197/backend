package com.ascend.campaign.utils;


import com.ascend.campaign.models.Product;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupCartUtil {

    public static List<Product> groupProductCart(List<Product> products) {
        List<Product> productGroup = new ArrayList<>();
        if (products != null) {
            Set<Product> productSet = new HashSet<>(products);
            Multiset<Product> productMultiset = HashMultiset.create();
            productMultiset.addAll(products);
            Product[] productArray = productSet.toArray(new Product[productSet.size()]);
            for (int i = 0; i < productSet.size(); i++) {
                productArray[i].setQuantity(productMultiset.count(productArray[i]));
            }

            Collections.addAll(productGroup, productArray);
        }

        return productGroup;
    }
}
