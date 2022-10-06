package com.example.drawer;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001cB#\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u0010\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011H\u0016J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u0011H\u0016J\u0018\u0010\u0017\u001a\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0011H\u0016J\u0010\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0002H\u0016R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\rR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001d"}, d2 = {"Lcom/example/drawer/RequestUserAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/drawer/RequestUserAdapter$RequestUserViewHolder;", "isTripPast", "", "data", "", "Lcom/example/drawer/ListUser;", "navController", "Landroidx/navigation/NavController;", "(ZLjava/util/List;Landroidx/navigation/NavController;)V", "getData", "()Ljava/util/List;", "()Z", "getNavController", "()Landroidx/navigation/NavController;", "getItemCount", "", "getItemViewType", "position", "onBindViewHolder", "", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "onViewRecycled", "RequestUserViewHolder", "app_debug"})
public final class RequestUserAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.drawer.RequestUserAdapter.RequestUserViewHolder> {
    private final boolean isTripPast = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.drawer.ListUser> data = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.navigation.NavController navController = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.example.drawer.RequestUserAdapter.RequestUserViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void onViewRecycled(@org.jetbrains.annotations.NotNull()
    com.example.drawer.RequestUserAdapter.RequestUserViewHolder holder) {
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.example.drawer.RequestUserAdapter.RequestUserViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemViewType(int position) {
        return 0;
    }
    
    public final boolean isTripPast() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.drawer.ListUser> getData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.navigation.NavController getNavController() {
        return null;
    }
    
    public RequestUserAdapter(boolean isTripPast, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.example.drawer.ListUser> data, @org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u0006\u0010 \u001a\u00020\u001bR\u0019\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0019\u0010\n\u001a\n \u0007*\u0004\u0018\u00010\u000b0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0019\u0010\u000e\u001a\n \u0007*\u0004\u0018\u00010\u000f0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0019\u0010\u0012\u001a\n \u0007*\u0004\u0018\u00010\u000f0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0019\u0010\u0014\u001a\n \u0007*\u0004\u0018\u00010\u00150\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0019\u0010\u0018\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\t\u00a8\u0006!"}, d2 = {"Lcom/example/drawer/RequestUserAdapter$RequestUserViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "(Landroid/view/View;)V", "acceptButton", "Landroid/widget/ImageButton;", "kotlin.jvm.PlatformType", "getAcceptButton", "()Landroid/widget/ImageButton;", "cardViewReq", "Landroidx/cardview/widget/CardView;", "getCardViewReq", "()Landroidx/cardview/widget/CardView;", "clickToRateTv", "Landroid/widget/TextView;", "getClickToRateTv", "()Landroid/widget/TextView;", "fullname", "getFullname", "profileImage", "Landroid/widget/ImageView;", "getProfileImage", "()Landroid/widget/ImageView;", "refuseButton", "getRefuseButton", "bind", "", "r", "Lcom/example/drawer/ListUser;", "navController", "Landroidx/navigation/NavController;", "unbind", "app_debug"})
    public static final class RequestUserViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final android.widget.ImageView profileImage = null;
        private final android.widget.TextView fullname = null;
        private final androidx.cardview.widget.CardView cardViewReq = null;
        private final android.widget.ImageButton acceptButton = null;
        private final android.widget.ImageButton refuseButton = null;
        private final android.widget.TextView clickToRateTv = null;
        
        public final android.widget.ImageView getProfileImage() {
            return null;
        }
        
        public final android.widget.TextView getFullname() {
            return null;
        }
        
        public final androidx.cardview.widget.CardView getCardViewReq() {
            return null;
        }
        
        public final android.widget.ImageButton getAcceptButton() {
            return null;
        }
        
        public final android.widget.ImageButton getRefuseButton() {
            return null;
        }
        
        public final android.widget.TextView getClickToRateTv() {
            return null;
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.example.drawer.ListUser r, @org.jetbrains.annotations.NotNull()
        androidx.navigation.NavController navController) {
        }
        
        public final void unbind() {
        }
        
        public RequestUserViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View v) {
            super(null);
        }
    }
}