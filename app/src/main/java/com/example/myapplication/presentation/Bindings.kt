package com.example.myapplication.presentation

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibility")
fun View.bindVisibility(visible: Boolean?) {
     visibility = if (visible == true) View.VISIBLE else View.GONE
}