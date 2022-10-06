
import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0006J\u0016\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0006J\u0016\u0010\u0013\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0006J\u0016\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\nR#\u0010\u0003\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR#\u0010\t\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\b\u00a8\u0006\u0015"}, d2 = {"LSharedViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "startEndAddress", "Landroidx/lifecycle/MutableLiveData;", "Lkotlin/Pair;", "", "getStartEndAddress", "()Landroidx/lifecycle/MutableLiveData;", "startEndPoints", "Lorg/osmdroid/util/GeoPoint;", "getStartEndPoints", "setAddress", "", "p", "sel", "setAddresses", "s", "e", "setPoint", "setPoints", "app_debug"})
public final class SharedViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<kotlin.Pair<org.osmdroid.util.GeoPoint, org.osmdroid.util.GeoPoint>> startEndPoints = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<kotlin.Pair<java.lang.String, java.lang.String>> startEndAddress = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<kotlin.Pair<org.osmdroid.util.GeoPoint, org.osmdroid.util.GeoPoint>> getStartEndPoints() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<kotlin.Pair<java.lang.String, java.lang.String>> getStartEndAddress() {
        return null;
    }
    
    public final void setPoints(@org.jetbrains.annotations.NotNull()
    org.osmdroid.util.GeoPoint s, @org.jetbrains.annotations.NotNull()
    org.osmdroid.util.GeoPoint e) {
    }
    
    public final void setAddresses(@org.jetbrains.annotations.NotNull()
    java.lang.String s, @org.jetbrains.annotations.NotNull()
    java.lang.String e) {
    }
    
    public final void setPoint(@org.jetbrains.annotations.NotNull()
    org.osmdroid.util.GeoPoint p, @org.jetbrains.annotations.NotNull()
    java.lang.String sel) {
    }
    
    public final void setAddress(@org.jetbrains.annotations.NotNull()
    java.lang.String p, @org.jetbrains.annotations.NotNull()
    java.lang.String sel) {
    }
    
    public SharedViewModel() {
        super();
    }
}