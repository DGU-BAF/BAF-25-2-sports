package com.example.baro.core.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.baro.core.util.showToast

abstract class BaseFragment(@LayoutRes layoutResId: Int) : Fragment(layoutResId) {

    // 공통 초기화 로직이 있으면 여기서 처리
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        initListener()
    }

    // 자식 Fragment에서 override해서 쓰면 됨
    open fun initView() {}
    open fun initObserver() {}
    open fun initListener() {}

    fun showShortToast(message: String) {
        context?.showToast(message)
    }
}
