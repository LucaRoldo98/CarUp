package com.example.drawer;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u00a8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010%\u001a\u00020&H\u0002J\b\u0010\'\u001a\u00020(H\u0002J\b\u0010)\u001a\u00020(H\u0002J\"\u0010*\u001a\u00020(2\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020,2\b\u0010.\u001a\u0004\u0018\u00010/H\u0017J\u0010\u00100\u001a\u0002012\u0006\u00102\u001a\u000203H\u0016J\u0012\u00104\u001a\u00020(2\b\u00105\u001a\u0004\u0018\u000106H\u0016J\"\u00107\u001a\u00020(2\u0006\u00108\u001a\u0002092\u0006\u0010:\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u00010=H\u0016J\u0018\u0010>\u001a\u00020(2\u0006\u00108\u001a\u00020?2\u0006\u0010@\u001a\u00020AH\u0016J&\u0010B\u001a\u0004\u0018\u00010;2\u0006\u0010@\u001a\u00020C2\b\u0010D\u001a\u0004\u0018\u00010E2\b\u00105\u001a\u0004\u0018\u000106H\u0016J*\u0010F\u001a\u00020(2\b\u0010G\u001a\u0004\u0018\u00010H2\u0006\u0010I\u001a\u00020,2\u0006\u0010J\u001a\u00020,2\u0006\u0010K\u001a\u00020,H\u0016J\b\u0010L\u001a\u00020(H\u0016J\u0010\u0010M\u001a\u0002012\u0006\u00102\u001a\u000203H\u0016J-\u0010N\u001a\u00020(2\u0006\u0010+\u001a\u00020,2\u000e\u0010O\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060P2\u0006\u0010Q\u001a\u00020RH\u0016\u00a2\u0006\u0002\u0010SJ\u0010\u0010T\u001a\u00020(2\u0006\u0010U\u001a\u000206H\u0016J\"\u0010V\u001a\u00020(2\b\u0010G\u001a\u0004\u0018\u00010W2\u0006\u0010X\u001a\u00020,2\u0006\u0010Y\u001a\u00020,H\u0016J\u001a\u0010Z\u001a\u00020(2\u0006\u0010G\u001a\u00020;2\b\u00105\u001a\u0004\u0018\u000106H\u0017R\u0014\u0010\u0005\u001a\u00020\u0006X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\u00020\u0010X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0015\u001a\u00020\u0006X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\b\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\u00020\u0006X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\b\"\u0004\b\u001b\u0010\u0018R\u001a\u0010\u001c\u001a\u00020\nX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\f\"\u0004\b\u001e\u0010\u000eR\u001a\u0010\u001f\u001a\u00020\u0010X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0012\"\u0004\b!\u0010\u0014R\u001a\u0010\"\u001a\u00020\u0006X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b#\u0010\b\"\u0004\b$\u0010\u0018\u00a8\u0006["}, d2 = {"Lcom/example/drawer/TripEditFragment;", "Landroidx/fragment/app/Fragment;", "Landroid/app/DatePickerDialog$OnDateSetListener;", "Landroid/app/TimePickerDialog$OnTimeSetListener;", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "dropHour", "Landroid/widget/EditText;", "getDropHour", "()Landroid/widget/EditText;", "setDropHour", "(Landroid/widget/EditText;)V", "endMarker", "Lorg/osmdroid/views/overlay/Marker;", "getEndMarker", "()Lorg/osmdroid/views/overlay/Marker;", "setEndMarker", "(Lorg/osmdroid/views/overlay/Marker;)V", "id", "getId", "setId", "(Ljava/lang/String;)V", "imageURL", "getImageURL", "setImageURL", "pickUpHour", "getPickUpHour", "setPickUpHour", "startMarker", "getStartMarker", "setStartMarker", "userID", "getUserID", "setUserID", "createImageFile", "Ljava/io/File;", "dispatchTakePictureIntent", "", "galleryAddPic", "onActivityResult", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onContextItemSelected", "", "item", "Landroid/view/MenuItem;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateContextMenu", "menu", "Landroid/view/ContextMenu;", "v", "Landroid/view/View;", "menuInfo", "Landroid/view/ContextMenu$ContextMenuInfo;", "onCreateOptionsMenu", "Landroid/view/Menu;", "inflater", "Landroid/view/MenuInflater;", "onCreateView", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onDateSet", "view", "Landroid/widget/DatePicker;", "year", "month", "dayOfMonth", "onDestroy", "onOptionsItemSelected", "onRequestPermissionsResult", "permissions", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onSaveInstanceState", "outState", "onTimeSet", "Landroid/widget/TimePicker;", "hourOfDay", "minute", "onViewCreated", "app_debug"})
public final class TripEditFragment extends androidx.fragment.app.Fragment implements android.app.DatePickerDialog.OnDateSetListener, android.app.TimePickerDialog.OnTimeSetListener {
    public java.lang.String id;
    public java.lang.String userID;
    public android.widget.EditText pickUpHour;
    public android.widget.EditText dropHour;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "TripEditFragment";
    public java.lang.String imageURL;
    public org.osmdroid.views.overlay.Marker startMarker;
    public org.osmdroid.views.overlay.Marker endMarker;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    public final void setId(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUserID() {
        return null;
    }
    
    public final void setUserID(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.widget.EditText getPickUpHour() {
        return null;
    }
    
    public final void setPickUpHour(@org.jetbrains.annotations.NotNull()
    android.widget.EditText p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.widget.EditText getDropHour() {
        return null;
    }
    
    public final void setDropHour(@org.jetbrains.annotations.NotNull()
    android.widget.EditText p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTAG() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getImageURL() {
        return null;
    }
    
    public final void setImageURL(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.osmdroid.views.overlay.Marker getStartMarker() {
        return null;
    }
    
    public final void setStartMarker(@org.jetbrains.annotations.NotNull()
    org.osmdroid.views.overlay.Marker p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.osmdroid.views.overlay.Marker getEndMarker() {
        return null;
    }
    
    public final void setEndMarker(@org.jetbrains.annotations.NotNull()
    org.osmdroid.views.overlay.Marker p0) {
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    @android.annotation.SuppressLint(value = {"SetTextI18n"})
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final java.io.File createImageFile() throws java.io.IOException {
        return null;
    }
    
    private final void dispatchTakePictureIntent() {
    }
    
    private final void galleryAddPic() {
    }
    
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.Q)
    @java.lang.Override()
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    @java.lang.Override()
    public void onCreateContextMenu(@org.jetbrains.annotations.NotNull()
    android.view.ContextMenu menu, @org.jetbrains.annotations.NotNull()
    android.view.View v, @org.jetbrains.annotations.Nullable()
    android.view.ContextMenu.ContextMenuInfo menuInfo) {
    }
    
    @java.lang.Override()
    public boolean onContextItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    @java.lang.Override()
    public void onDateSet(@org.jetbrains.annotations.Nullable()
    android.widget.DatePicker view, int year, int month, int dayOfMonth) {
    }
    
    @java.lang.Override()
    public void onCreateOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu, @org.jetbrains.annotations.NotNull()
    android.view.MenuInflater inflater) {
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    @java.lang.Override()
    public void onTimeSet(@org.jetbrains.annotations.Nullable()
    android.widget.TimePicker view, int hourOfDay, int minute) {
    }
    
    @java.lang.Override()
    public void onSaveInstanceState(@org.jetbrains.annotations.NotNull()
    android.os.Bundle outState) {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    public TripEditFragment() {
        super();
    }
}