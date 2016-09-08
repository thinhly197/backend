package com.ascend.campaign.models;

import lombok.Data;

/**
 * Created by thinhly on 1/20/16.
 */
@Data
public class AuthenticationModel {

    String dnm;
    String usr;
    String typ;
    Long exp;

}
