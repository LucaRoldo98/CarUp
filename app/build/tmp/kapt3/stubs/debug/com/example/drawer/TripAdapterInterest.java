package com.example.drawer;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0018B\u001b\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\r\u001a\u00020\u000eH\u0016J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u000eH\u0016J\u0018\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000eH\u0016J\u0010\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0002H\u0016R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0019"}, d2 = {"Lcom/example/drawer/TripAdapterInterest;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/drawer/TripAdapterInterest$TripViewHolder;", "data", "", "Lcom/example/drawer/TripOthers;", "navController", "Landroidx/navigation/NavController;", "(Ljava/util/List;Landroidx/navigation/NavController;)V", "getData", "()Ljava/util/List;", "getNavController", "()Landroidx/navigation/NavController;", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "onViewRecycled", "TripViewHolder", "app_debug"})
public final class TripAdapterInterest extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.drawer.TripAdapterInterest.TripViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.drawer.TripOthers> data = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.navigation.NavController navController = null;
    
    @java.lang.Override()
    public void onViewRecycled(@org.jetbrains.annotations.NotNull()
    com.example.drawer.TripAdapterInterest.TripViewHolder holder) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.example.drawer.TripAdapterInterest.TripViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.example.drawer.TripAdapterInterest.TripViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.drawer.TripOthers> getData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.navigation.NavController getNavController() {
        return null;
    }
    
    public TripAdapterInterest(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.example.drawer.TripOthers> data, @org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020)J\u0006\u0010*\u001a\u00020%R\u0019\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0019\u0010\n\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0019\u0010\f\u001a\n \u0007*\u0004\u0018\u00010\r0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0019\u0010\u0010\u001a\n \u0007*\u0004\u0018\u00010\u00110\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0014\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\tR\u0019\u0010\u0016\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\tR\u0019\u0010\u0018\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\tR\u0019\u0010\u001a\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\tR\u0019\u0010\u001c\u001a\n \u0007*\u0004\u0018\u00010\u001d0\u001d\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0019\u0010 \u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\tR\u0019\u0010\"\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\t\u00a8\u0006+"}, d2 = {"Lcom/example/drawer/TripAdapterInterest$TripViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "(Landroid/view/View;)V", "arrLocation", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getArrLocation", "()Landroid/widget/TextView;", "arrTime", "getArrTime", "carImage", "Landroid/widget/ImageView;", "getCarImage", "()Landroid/widget/ImageView;", "cardView", "Landroidx/cardview/widget/CardView;", "getCardView", "()Landroidx/cardview/widget/CardView;", "depDate", "getDepDate", "depLocation", "getDepLocation", "depTime", "getDepTime", "duration", "getDuration", "likeButton", "Landroid/widget/ImageButton;", "getLikeButton", "()Landroid/widget/ImageButton;", "price", "getPrice", "seats", "getSeats", "bind", "", "t", "Lcom/example/drawer/TripOthers;", "navController", "Landroidx/navigation/NavController;", "unbind", "app_debug"})
    public static final class TripViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final android.widget.ImageView carImage = null;
        private final android.widget.TextView depLocation = null;
        private final android.widget.TextView arrLocation = null;
        private final android.widget.TextView depTime = null;
        private final android.widget.TextView arrTime = null;
        private final android.widget.TextView depDate = null;
        private final android.widget.TextView duration = null;
        private final android.widget.TextView seats = null;
        private final android.widget.TextView price = null;
        private final androidx.cardview.widget.CardView cardView = null;
        private final android.widget.ImageButton likeButton = null;
        
        public final android.widget.ImageView getCarImage() {
            return null;
        }
        
        public final android.widget.TextView getDepLocation() {
            return null;
        }
        
        public final android.widget.TextView getArrLocation() {
            return null;
        }
        
        public final android.widget.TextView getDepTime() {
            return null;
        }
        
        public final android.widget.TextView getArrTime() {
            return null;
        }
        
        public final android.widget.TextView getDepDate() {
            return null;
        }
        
        public final android.widget.TextView getDuration() {
            return null;
        }
        
        public final android.widget.TextView getSeats() {
            return null;
        }
        
        public final android.widget.TextView getPrice() {
            return null;
        }
        
        public final androidx.cardview.widget.CardView getCardView() {
            return null;
        }
        
        public final android.widget.ImageButton getLikeButton() {
            return null;
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.example.drawer.TripOthers t, @org.jetbrains.annotations.NotNull()
        androidx.navigation.NavController navController) {
        }
        
        public final void unbind() {
        }
        
        public TripViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View v) {
            super(null);
        }
    }
}