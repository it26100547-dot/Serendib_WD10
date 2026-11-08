package com.serendib.model;

import java.util.Map;


public interface DisplayableCard {


    String getItemType();


    String getCardTitle();


    String getCardSubtitle();


    String getCardCategory();


    String getCardImage();


    String getCardPriceFormatted();


    Map<String, Object> toCardMap();
}
