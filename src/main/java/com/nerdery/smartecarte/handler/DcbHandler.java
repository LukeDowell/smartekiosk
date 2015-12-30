package com.nerdery.smartecarte.handler;


/**
 * Created by Luke on 12/29/2015.
 */
@FunctionalInterface
public interface DcbHandler {

    void handle(byte[] buffer);
}
