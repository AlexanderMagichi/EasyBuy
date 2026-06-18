package com.teamchallenge.easybuy.shop.repository.shopcontact;

import com.teamchallenge.easybuy.infrastructure.enums.ContactMethod;

public interface ContactStat {

    Long getCount();

    ContactMethod getMethod();
}