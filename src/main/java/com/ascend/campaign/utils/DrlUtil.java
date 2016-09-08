package com.ascend.campaign.utils;

import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
public class DrlUtil {

    private String generateDrlRuleBase(String data, String filename) throws IOException {
        Path path = Paths.get(getClass().getResource("/rules/").getPath() + filename);
        if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.createFile(path);
        }

        Files.write(path, ("import com.ascend.campaign.models.*;\n"
                + "import java.util.*;\n"
                + "import com.ascend.campaign.utils.GenerateActionPromotionUtil;\n"
                + data).getBytes("UTF-8"));

        return "success";
    }

    public String generateDrlRuleBaseItm(List<Promotion> promotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(promotions)) {
                for (Promotion promotion : promotions) {
                    sb.append(generatePromotionUtil.generateStringDataItmPromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrlRuleBaseItm: {}", e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseWm(List<PromotionWM> promotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(promotions)) {
                for (PromotionWM promotion : promotions) {
                    sb.append(generatePromotionUtil.generateStringDataWmPromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrlRuleBaseItm: {}", e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseForProductItm(List<Promotion> promotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(promotions)) {
                for (Promotion promotion : promotions) {
                    sb.append(generatePromotionUtil.generateStringDataItmProductPromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrl: {}, {}", filename, e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseForProductWm(List<PromotionWM> promotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(promotions)) {
                for (PromotionWM promotion : promotions) {
                    sb.append(generatePromotionUtil.generateStringDataWmProductPromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrl: {}, {}", filename, e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseForProductBundleItm(List<Promotion> allActivePromotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(allActivePromotions)) {
                for (Promotion promotion : allActivePromotions) {
                    sb.append(generatePromotionUtil.generateStringDataItmProductBundlePromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrl: {}, {}", filename, e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseForProductBundleWm(List<PromotionWM> allActivePromotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(allActivePromotions)) {
                for (PromotionWM promotion : allActivePromotions) {
                    sb.append(generatePromotionUtil.generateStringDataWmProductBundlePromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrl: {}, {}", filename, e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseForProductMNPItm(List<Promotion> allActivePromotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(allActivePromotions)) {
                for (Promotion promotion : allActivePromotions) {
                    sb.append(generatePromotionUtil.generateStringDataItmProductMNPPromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrl: {}, {}", filename, e.getMessage(), e);
            return "";
        }
    }

    public String generateDrlRuleBaseForProductFreebieItm(List<Promotion> allActivePromotions, String filename) {
        GeneratePromotionUtil generatePromotionUtil = new GeneratePromotionUtil();
        try {
            StringBuilder sb = new StringBuilder("\n");
            if (CollectionUtils.isNotEmpty(allActivePromotions)) {
                for (Promotion promotion : allActivePromotions) {
                    sb.append(generatePromotionUtil.generateStringDataItmProductFreebiePromotion(promotion));
                }
            }

            return generateDrlRuleBase(sb.toString(), filename);
        } catch (Exception e) {
            log.error("Error occur generateDrl: {}, {}", filename, e.getMessage(), e);
            return "";
        }
    }

    public void deleteDrl(String pattern) {
        final File folder = Paths.get(getClass().getResource("/rules/").getPath()).toFile();
        if (folder != null) {
            final File[] files = folder.listFiles((dir, name) -> {
                return name.matches(pattern);
            });

            if (files != null) {
                for (final File file : files) {
                    if (!file.delete()) {
                        log.error("Can't remove: {} ", file.getAbsolutePath());
                    }
                }
            }
        }
    }
}