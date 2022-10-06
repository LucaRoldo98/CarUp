package com.example.drawer;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u0005\b&\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0011R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0012\u0010\u0013\u001a\u00020\u0014X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010\u00a8\u0006\u0019"}, d2 = {"Lcom/example/drawer/ListUser;", "", "context", "Landroid/content/Context;", "userID", "", "profileImageURI", "fullname", "isTripPast", "", "isRated", "tripID", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLjava/lang/String;)V", "getContext", "()Landroid/content/Context;", "getFullname", "()Ljava/lang/String;", "()Z", "getProfileImageURI", "sortingVal", "", "getSortingVal", "()I", "getTripID", "getUserID", "app_debug"})
public abstract class ListUser {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String userID = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String profileImageURI = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fullname = null;
    private final boolean isTripPast = false;
    private final boolean isRated = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String tripID = null;
    
    public abstract int getSortingVal();
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.Context getContext() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUserID() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getProfileImageURI() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFullname() {
        return null;
    }
    
    public final boolean isTripPast() {
        return false;
    }
    
    public final boolean isRated() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTripID() {
        return null;
    }
    
    public ListUser(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String userID, @org.jetbrains.annotations.NotNull()
    java.lang.String profileImageURI, @org.jetbrains.annotations.NotNull()
    java.lang.String fullname, boolean isTripPast, boolean isRated, @org.jetbrains.annotations.NotNull()
    java.lang.String tripID) {
        super();
    }
}