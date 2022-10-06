package com.example.drawer;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\fR\u0014\u0010\r\u001a\u00020\u000eX\u0096D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0011"}, d2 = {"Lcom/example/drawer/AcceptedUser;", "Lcom/example/drawer/ListUser;", "context", "Landroid/content/Context;", "userID", "", "profileImageURI", "fullname", "isTripPast", "", "isRated", "tripID", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;)V", "sortingVal", "", "getSortingVal", "()I", "app_debug"})
public final class AcceptedUser extends com.example.drawer.ListUser {
    private final int sortingVal = 1;
    
    @java.lang.Override()
    public int getSortingVal() {
        return 0;
    }
    
    public AcceptedUser(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String userID, @org.jetbrains.annotations.NotNull()
    java.lang.String profileImageURI, @org.jetbrains.annotations.NotNull()
    java.lang.String fullname, boolean isTripPast, boolean isRated, @org.jetbrains.annotations.NotNull()
    java.lang.String tripID) {
        super(null, null, null, null, false, false, null);
    }
}