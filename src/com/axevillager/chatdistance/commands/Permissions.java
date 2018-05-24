package com.axevillager.chatdistance.commands;

/**
 * Permissions created by AxeVillager on 2018/05/09.
 */

public enum Permissions {

    STAFF_MESSAGE("staff", "chatdistance.staff"),
    BROADCAST_MESSAGE("broadcast", "chatdistance.broadcast"),
    GLOBAL_MESSAGE("global", "chatdistance.global"),
    OOC_MESSAGE("ooc", "chatdistance.outofcontext"),
    SHOUT(null, "chatdistance.shout"),
    WHISPER(null, "chatdistance.whisper"),
    FORMATTING(null, "chatdistance.formatting");

    private final String commandName;
    private final String permission;

    Permissions(final String command, final String permission) {
        this.commandName = command;
        this.permission = permission;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getPermission() {
        return this.permission;
    }
}