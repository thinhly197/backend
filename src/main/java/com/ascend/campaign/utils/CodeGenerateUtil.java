package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Code;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CodeGenerateUtil {

    public List<Code> genSuffixCode(String prefix, long count, int number,
                                    Long codeDetailId, Set<String> allCodeString) {
        String[] character = new String[]{
                "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M",
                "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "2", "3", "4", "5", "6", "7", "8", "9"
        };

        int existInSet = 0;
        boolean cancel = false;
        Set<String> codeSet = new HashSet<>();
        List<Code> codeList = new ArrayList<>();
        Double require = Math.pow(32, number);
        if ((require / count) < 1.25) {
            return codeList;
        }

        while (codeSet.size() < count) {
            String codeString = prefix;
            boolean isAlreadyGenerate = false;

            for (int i = 0; i < number; i++) {
                int numb = (int) Math.floor(Math.random() * character.length);
                codeString += character[numb];
            }
            if (!allCodeString.contains(codeString)) {
                isAlreadyGenerate = codeSet.add(codeString);
            }
            if (!isAlreadyGenerate) {
                existInSet++;
            } else {
                Code code = new Code();
                code.setCode(codeString);
                code.setStatus(CampaignEnum.ACTIVATE.getContent());
                code.setRevenue(0.0);
                code.setAvailable(1L);
                code.setUse(0L);
                code.setCodeDetail(codeDetailId);

                codeList.add(code);
            }
            if (existInSet > count / 5) {
                cancel = true;
                break;
            }
        }
        if (cancel) {
            codeList = new ArrayList<>();
        }

        return codeList;
    }
}
